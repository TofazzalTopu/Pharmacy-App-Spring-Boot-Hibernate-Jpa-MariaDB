package com.asl.pms.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asl.pms.model.Drug;
import com.asl.pms.model.OrderDetails;
import com.asl.pms.model.OrderMaster;
import com.asl.pms.model.OrderMaster.Status;
import com.asl.pms.model.Role;
import com.asl.pms.model.User;
import com.asl.pms.service.CompanyService;
import com.asl.pms.service.DosageFormService;
import com.asl.pms.service.DrugService;
import com.asl.pms.service.GenericService;
import com.asl.pms.service.OrderService;
import com.asl.pms.service.RackService;
import com.asl.pms.service.ShelfService;
import com.asl.pms.service.StockService;
import com.asl.pms.service.UserService;
import com.asl.pms.util.GlobalMethod;
import com.asl.pms.viewmodels.DrugStock;
import com.asl.pms.viewmodels.ICustomer;
import com.asl.pms.viewmodels.IDrug;

@Controller
@RequestMapping("/orders")
public class OrdersController {

	

	@Autowired
	private Environment env;

	public String getPicturePath() {
		String picturePath = env.getProperty("pms.picture.upload.path");
		return picturePath;
	}

	@Autowired
	GlobalMethod globalMethod;

	@Autowired
	DrugService drugService;

	@Autowired
	GenericService genericService;

	@Autowired
	DosageFormService dosageFormService;

	@Autowired
	CompanyService companyService;

	@Autowired
	RackService rackService;

	@Autowired
	ShelfService shelfService;

	@Autowired
	OrderService orderService;
	
	@Autowired
	StockService stockService;

	@RequestMapping(method = RequestMethod.GET, value = { "/list" })
	public String getOrderList(Model model, @RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "50") Integer total,
			@RequestParam(required = false, defaultValue = "name") String sort) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());

		List<OrderMaster> orderMasterList = orderService.findAllOrderMaster();
		model.addAttribute("orderMasterList", orderMasterList);
		return "dashboard/order/order_list";
	}

	@RequestMapping(method = RequestMethod.GET, value = { "/create" })
	public String addOrderForm(Model model) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());
		Drug drug = new Drug();
		model.addAttribute("drug", drug);

		return "dashboard/order/order_new";
	}

	@RequestMapping(value = { "/create" }, method = RequestMethod.POST)
	public String saveOrderMaster(Model model, @Valid @ModelAttribute("orderMaster") OrderMaster orderMaster,
			BindingResult result) throws IOException {

		if (result.hasErrors()) {
			List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
			model.addAttribute("menulist", stringList);
			model.addAttribute("image", globalMethod.getUserImage());
			model.addAttribute("fullname", globalMethod.getPrincipalFullName());
			Drug drug = new Drug();
			model.addAttribute("drug", drug);

			return "dashboard/order/order_new";
		}

		//
		String base64StringPr1 = orderMaster.getPrescription1();
		String[] stringsP1 = base64StringPr1.split(",");
		String extension;
		switch (stringsP1[0]) {
		case "data:image/jpeg;base64":
			extension = "jpeg";
			break;
		case "data:image/png;base64":
			extension = "png";
			break;
		default:
			extension = "jpg";
			break;
		}

		byte[] dataP1 = DatatypeConverter.parseBase64Binary(stringsP1[1]);
		String fileNameP1 = String.format("%s.%s", System.currentTimeMillis(), extension);
		Path picPath = Paths.get(getPicturePath());
		Files.write(picPath.resolve(fileNameP1), dataP1);
		
		String base64StringPr2 = orderMaster.getPrescription2();
		String[] stringsP2 = base64StringPr2.split(",");
		switch (stringsP2[0]) {
		case "data:image/jpeg;base64":
			extension = "jpeg";
			break;
		case "data:image/png;base64":
			extension = "png";
			break;
		default:
			extension = "jpg";
			break;
		}

		byte[] dataP2 = DatatypeConverter.parseBase64Binary(stringsP2[1]);
		String fileNameP2 = String.format("%s.%s", System.currentTimeMillis(), extension);
		Path picPath2 = Paths.get(getPicturePath());
		Files.write(picPath2.resolve(fileNameP2), dataP2);
		

		User customer = userService.findOne(orderMaster.getCustomer().getId());
		OrderMaster om = new OrderMaster();
		om.setCustomer(customer);
		om.setPrescription1(fileNameP1);
		om.setPrescription2(fileNameP2);
		om.setPrescription3(fileNameP2);
		om.setStatus(Status.PENDING);
		om.setCreateTime(LocalDateTime.now());

		// save orderMaster
		if (orderMaster.getOrderDetails().size() > 0 && customer != null) {
			OrderMaster omSaved = orderService.saveOrderMaster(om);
			for (OrderDetails orderDetails : orderMaster.getOrderDetails()) {
				Long dId = orderDetails.getDrug().getId();
				Drug drug = drugService.findDrug(dId);
				orderDetails.setDrug(drug);
				double total = 0;
				drug.getPrice();
				int qty = Integer.parseInt(orderDetails.getQty().trim());
				total = qty * drug.getPrice();
				orderDetails.setTotal(total);
				orderDetails.setOrderMaster(omSaved);
				OrderDetails odSaved = orderService.saveOrderDetails(orderDetails);
				// save order details
				System.out.println(odSaved);
			}

			List<OrderMaster> orderMasterList = orderService.findAllOrderMaster();
			List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
			model.addAttribute("menulist", stringList);
			model.addAttribute("image", globalMethod.getUserImage());
			model.addAttribute("fullname", globalMethod.getPrincipalFullName());
			model.addAttribute("orderMasterList", orderMasterList);
			return "dashboard/order/order_list";
		} else {
			return "redirect:/orders/create";
		}

	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@ResponseBody
	public List<DrugStock> searchStock(@RequestParam(required = false) String name) {
		List<DrugStock> stocks = stockService.searchStockByMedicineName(name);
		return stocks;
		
	}
	
	public List<IDrug> searchDrug(@RequestParam(required = false) String name) {
		List<IDrug> drugs = drugService.searchByQuery(name);
		return drugs;
	}

	@Autowired
	UserService userService;

	@RequestMapping(value = "/customerSearch", method = RequestMethod.GET)
	@ResponseBody
	public List<ICustomer> searchCustomer(@RequestParam(required = false) String name) {

		List<ICustomer> users = userService.findByEmailStartingWith(name);
		Role role = new Role();
		role.setId(111L);
		// List<User> users=userService.findByRolesAndEmailStartingWith(role, name);
		System.out.println(users);
		return users;

	}

	@RequestMapping(value = "/show/{id}", method = RequestMethod.GET)
	public String showOrder(Model model, @PathVariable Long id) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());
		OrderMaster orderMaster = orderService.getOrderMasterById(id);
		List<OrderDetails> orderDetails = orderService.findByOrderMaster(orderMaster);
		orderMaster.setOrderDetails(orderDetails);
		model.addAttribute("orderMaster", orderMaster);

		return "dashboard/order/view_order";
	}

	@RequestMapping(value = "/updateOrder/{id}", method = RequestMethod.GET)
	public String updateOrderForm(Model model, @PathVariable Long id) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());
		OrderMaster orderMaster = orderService.getOrderMasterById(id);
		List<OrderDetails> orderDetails = orderService.findByOrderMaster(orderMaster);
		orderMaster.setOrderDetails(orderDetails);
		model.addAttribute("orderMaster", orderMaster);

		return "dashboard/order/update_order";
	}

	@RequestMapping(value = "/updateOrder/{id}", method = RequestMethod.POST)
	public String updateOrder(Model model, @PathVariable Long id,
			@Valid @ModelAttribute("orderMaster") OrderMaster orderMaster, BindingResult result) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());

		OrderMaster OrderMasterDB = orderService.getOrderMasterById(orderMaster.getId());

		OrderMasterDB.setStatus(orderMaster.getStatus());
		OrderMasterDB.setModifyTime(LocalDateTime.now());
		orderService.saveOrderMaster(OrderMasterDB);

		return "redirect:/orders/list";
	}

}
