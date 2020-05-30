package com.asl.pms.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asl.pms.configuration.Constants;
import com.asl.pms.model.Drug;
import com.asl.pms.model.PurchaseMaster;
import com.asl.pms.model.SalesDetails;
import com.asl.pms.model.SalesMaster;
import com.asl.pms.model.Stock;
import com.asl.pms.model.TnxDetails;
import com.asl.pms.model.TnxMaster;
import com.asl.pms.service.CompanyService;
import com.asl.pms.service.DosageFormService;
import com.asl.pms.service.DrugService;
import com.asl.pms.service.GenericService;
import com.asl.pms.service.OrderService;
import com.asl.pms.service.PurchaseService;
import com.asl.pms.service.RackService;
import com.asl.pms.service.SalesService;
import com.asl.pms.service.ShelfService;
import com.asl.pms.service.StockService;
import com.asl.pms.service.UserService;
import com.asl.pms.util.GlobalMethod;
import com.asl.pms.util.StringUtil;
import com.asl.pms.viewmodels.DrugStock;

@Controller
@RequestMapping("/sales")
public class SalesController {

	@Autowired
	GlobalMethod globalMethod;

	@Autowired
	UserService userService;

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
	PurchaseService purchaseService;

	@Autowired
	SalesService salesService;

	@Autowired
	StockService stockService;

	@RequestMapping(method = RequestMethod.GET, value = { "/salesList" })
	public String getOrderList(Model model, @RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "50") Integer total,
			@RequestParam(required = false, defaultValue = "") String range) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());
		

		if(StringUtil.isEmptyString(range) || range.length()!=21) {
			List<SalesMaster> salesMasterList = salesService.findAllSalesMaster();
			model.addAttribute("salesMasterList", salesMasterList);
		}else {			
			List<SalesMaster> salesMasterList = salesService.findAllBySalesDateBetween(range.split("_")[0], range.split("_")[1]);			
			model.addAttribute("salesMasterList", salesMasterList);
		}

		return "dashboard/sales/list";
	}

	@RequestMapping(method = RequestMethod.GET, value = { "/createSale" })
	public String addPurchaseForm(Model model) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());

		return "dashboard/sales/add";
	}

	@RequestMapping(value = { "/createSale" }, method = RequestMethod.POST)
	public String saveSalesMaster(Model model, @Valid @ModelAttribute("salesMaster") SalesMaster salesMaster,
			BindingResult result) throws IOException {
		if (result.hasErrors()) {
			List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
			model.addAttribute("menulist", stringList);
			model.addAttribute("image", globalMethod.getUserImage());
			model.addAttribute("fullname", globalMethod.getPrincipalFullName());
			// return "dashboard/sales/add";
			return "redirect:/sales/createSale";
		}
		
		salesMaster.setCreatedBy(globalMethod.getPrincipal());
		salesMaster.setReturnFlag(Constants.SALES_NOT_RETURN);
		salesMaster.setCreateTime(LocalDateTime.now());

		// save purchaseMaster
		if (salesMaster.getSalesDetails() != null && salesMaster.getSalesDetails().size() > 0) {

			SalesMaster smSaved = salesService.saveSalesMaster(salesMaster);
			for (SalesDetails salesDetails : salesMaster.getSalesDetails()) {
				salesDetails.setSalesMaster(smSaved);
				Long dId = Long.parseLong(salesDetails.getDrugId());
				Drug drug = drugService.findDrug(dId);
				salesDetails.setDrug(drug);
				salesService.saveSalesDetails(salesDetails);
			}

			List<SalesDetails> salesDetailsList = salesService.findBySalesMaster(smSaved);
			TnxMaster tnxMaster = new TnxMaster();
			tnxMaster.setTnxType(Constants.TNX_SALE);
			tnxMaster.setTnxMode(Constants.CREDIT);
			tnxMaster.setCreatedBy(globalMethod.getPrincipal());
			tnxMaster.setCreateTime(LocalDateTime.now());
			tnxMaster.setRefId(smSaved.getId().toString());
			TnxMaster tnxMstSaved = stockService.saveTnxMaster(tnxMaster);

			salesDetailsList.forEach(pd -> {
				TnxDetails tnxDtl = new TnxDetails();
				tnxDtl.setRefId(pd.getId().toString());
				tnxDtl.setTnxMaster(tnxMstSaved);
				tnxDtl.setDrug(pd.getDrug());
				tnxDtl.setTotalQty(Double.parseDouble(pd.getSalesQty()));
				TnxDetails tnxDtlSaved = stockService.saveTnxDetails(tnxDtl);
				
				// Stock update
				List<Stock> stockList = stockService.findStockByDrug(tnxDtlSaved.getDrug());
				if (stockList != null && stockList.size() > 0) {
					Stock stock = stockList.get(0);
					int nQty = (int) tnxDtl.getTotalQty();
					stock.setQty(stock.getQty() - nQty);
					stock.setModifydBy(globalMethod.getPrincipal());
					stock.setModifyTime(LocalDateTime.now());
					stockService.updateStock(stock);
				}

			});

			return "redirect:/sales/salesList";
		} else {
			List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
			model.addAttribute("menulist", stringList);
			model.addAttribute("image", globalMethod.getUserImage());
			model.addAttribute("fullname", globalMethod.getPrincipalFullName());
			return "dashboard/sales/add";
			// return "redirect:/sales/createSale";
		}

	}

	@RequestMapping(value = "/salesView/{id}", method = RequestMethod.GET)
	public String show(Model model, @PathVariable Long id) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());
		SalesMaster salesMaster = salesService.getSalesMasterById(id);
		List<SalesDetails> salesDetails = salesService.findBySalesMaster(salesMaster);
		salesMaster.setSalesDetails(salesDetails);
		model.addAttribute("salesMaster", salesMaster);

		return "dashboard/sales/view";
	}

	@RequestMapping(value = "/updateSale/{id}", method = RequestMethod.GET)
	public String updateForm(Model model, @PathVariable Long id) {
		purchaseService.deleteByPurchaseMasterId(id);
		return "redirect:/sales/salesList";
	}

	@RequestMapping(value = "/updateSale/{id}", method = RequestMethod.POST)
	public String update(Model model, @PathVariable Long id,
			@Valid @ModelAttribute("purchaseMaster") PurchaseMaster purchaseMaster, BindingResult result) {
		purchaseService.deleteByPurchaseMasterId(id);
		return "redirect:/sales/salesList";
	}

	@RequestMapping(value = "/searchStock", method = RequestMethod.GET)
	@ResponseBody
	public List<DrugStock> searchStock(@RequestParam(required = false) String name) {
		List<DrugStock> stocks = stockService.searchStockByMedicineName(name);
		return stocks;

	}

}
