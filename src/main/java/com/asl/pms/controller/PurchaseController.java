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

import com.asl.pms.configuration.Constants;
import com.asl.pms.model.Drug;

import com.asl.pms.model.PurchaseDetails;
import com.asl.pms.model.PurchaseMaster;
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
import com.asl.pms.service.ShelfService;
import com.asl.pms.service.StockService;
import com.asl.pms.service.UserService;
import com.asl.pms.util.GlobalMethod;
import com.asl.pms.util.StringUtil;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

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
	StockService stockService;

	@RequestMapping(method = RequestMethod.GET, value = { "/purchaseList" })
	public String getOrderList(Model model, @RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "50") Integer total,
			@RequestParam(required = false, defaultValue = "") String range) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());
		
		if(StringUtil.isEmptyString(range) || range.length()!=21) {
			List<PurchaseMaster> purchaseMasterList = purchaseService.findAllPurchaseMaster();
			model.addAttribute("purchaseMasterList", purchaseMasterList);
		}else {			
			//List<PurchaseMaster> purchaseMasterList = purchaseService.purchaseListByDateRange(range.split("_")[0], range.split("_")[1]);
			List<PurchaseMaster> purchaseMasterList = purchaseService.findAllByInvDateBetween(range.split("_")[0], range.split("_")[1]);			
			//List<PurchaseMaster> purchaseMasterList = purchaseService.findAllByCreateTimeBetween(range.split("_")[0], range.split("_")[1]);		
			model.addAttribute("purchaseMasterList", purchaseMasterList);
		}
		

		return "dashboard/purchase/list";
	}

	@RequestMapping(method = RequestMethod.GET, value = { "/addPurchase" })
	public String addPurchaseForm(Model model) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());
		Drug drug = new Drug();
		model.addAttribute("drug", drug);

		return "dashboard/purchase/add";
	}

	@RequestMapping(value = { "/addPurchase" }, method = RequestMethod.POST)
	public String savePurchase(Model model, @Valid @ModelAttribute("purchaseMaster") PurchaseMaster purchaseMaster,
			BindingResult result) throws IOException {
		if (result.hasErrors()) {
			List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
			model.addAttribute("menulist", stringList);
			model.addAttribute("image", globalMethod.getUserImage());
			model.addAttribute("fullname", globalMethod.getPrincipalFullName());
			return "dashboard/purchase/add";
		}

		purchaseMaster.setCreatedBy(globalMethod.getPrincipal());
		purchaseMaster.setReturnFlag(Constants.PURCHASE_NOT_RETURN);
		purchaseMaster.setCreateTime(LocalDateTime.now());

		// save purchaseMaster
		if (purchaseMaster.getPurchaseDetails() != null && purchaseMaster.getPurchaseDetails().size() > 0) {

			PurchaseMaster pmSaved = purchaseService.savePurchaseMaster(purchaseMaster);
			for (PurchaseDetails purchaseDetails : purchaseMaster.getPurchaseDetails()) {
				purchaseDetails.setPurchaseMaster(pmSaved);
				Long dId = Long.parseLong(purchaseDetails.getDrugId());
				Drug drug = drugService.findDrug(dId);
				purchaseDetails.setDrug(drug);
				if (purchaseDetails.getTotalQty() > 0 && purchaseDetails.getTpAmount() > 0) {
					purchaseService.savePurchaseDetails(purchaseDetails);
					// Update sales Price
					drug.setPrice(purchaseDetails.getSalePrice());
					drugService.save(drug);
				}

			}

			List<PurchaseDetails> purchaseDetailsList = purchaseService.findByPurchaseMaster(pmSaved);
			if (purchaseDetailsList != null && purchaseDetailsList.size() > 0) {
				TnxMaster tnxMaster = new TnxMaster();
				tnxMaster.setTnxType(Constants.TNX_PURCHASE);
				tnxMaster.setTnxMode(Constants.DEBIT);
				tnxMaster.setCreatedBy(globalMethod.getPrincipal());
				tnxMaster.setCreateTime(LocalDateTime.now());
				tnxMaster.setRefId(pmSaved.getId().toString());
				TnxMaster tnxMstSaved = stockService.saveTnxMaster(tnxMaster);

				purchaseDetailsList.forEach(pd -> {
					TnxDetails tnxDtl = new TnxDetails();
					tnxDtl.setRefId(pd.getId().toString());
					tnxDtl.setTnxMaster(tnxMstSaved);
					tnxDtl.setDrug(pd.getDrug());
					tnxDtl.setTotalQty(pd.getTotalQty());
					TnxDetails tnxDtlSaved = stockService.saveTnxDetails(tnxDtl);

					List<Stock> stockList = stockService.findStockByDrug(tnxDtlSaved.getDrug());
					if (stockList != null && stockList.size() > 0) {
						// Stock update
						Stock stock = stockList.get(0);
						int nQty = (int) tnxDtl.getTotalQty();
						stock.setQty(stock.getQty() + nQty);
						stock.setModifydBy(globalMethod.getPrincipal());
						stock.setModifyTime(LocalDateTime.now());
						stockService.updateStock(stock);
					} else {
						// Add new Stock
						Stock stock = new Stock();
						stock.setDrug(tnxDtlSaved.getDrug());
						int nQty = (int) tnxDtl.getTotalQty();
						stock.setQty(nQty);
						stock.setModifydBy(globalMethod.getPrincipal());
						stock.setModifyTime(LocalDateTime.now());
						stockService.updateStock(stock);
					}
				});
			}
			return "redirect:/purchase/purchaseList";
		} else {
			return "redirect:/purchase/addPurchase";
		}

	}

	@RequestMapping(value = "/showPurchase/{id}", method = RequestMethod.GET)
	public String show(Model model, @PathVariable Long id) {
		List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
		model.addAttribute("menulist", stringList);
		model.addAttribute("image", globalMethod.getUserImage());
		model.addAttribute("fullname", globalMethod.getPrincipalFullName());
		PurchaseMaster purchaseMaster = purchaseService.getPurchaseMasterById(id);
		List<PurchaseDetails> purchaseDetails = purchaseService.findByPurchaseMaster(purchaseMaster);
		purchaseMaster.setPurchaseDetails(purchaseDetails);
		model.addAttribute("purchaseMaster", purchaseMaster);

		return "dashboard/purchase/view";
	}

	@RequestMapping(value = "/updatePurchase/{id}", method = RequestMethod.GET)
	public String updateForm(Model model, @PathVariable Long id) {
		// purchaseService.deleteByPurchaseMasterId(id);
		return "redirect:/purchase/purchaseList";
	}

	@RequestMapping(value = "/updatePurchase/{id}", method = RequestMethod.POST)
	public String update(Model model, @PathVariable Long id,
			@Valid @ModelAttribute("purchaseMaster") PurchaseMaster purchaseMaster, BindingResult result) {
		// purchaseService.deleteByPurchaseMasterId(id);
		return "redirect:/purchase/purchaseList";
	}

}
