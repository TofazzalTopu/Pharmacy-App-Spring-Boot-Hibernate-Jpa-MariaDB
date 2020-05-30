package com.asl.pms.controller;

import com.asl.pms.configuration.Constants;
import com.asl.pms.model.PurchaseDetails;
import com.asl.pms.model.PurchaseMaster;
import com.asl.pms.model.PurchaseReturn;
import com.asl.pms.model.PurchaseReturnDetails;
import com.asl.pms.service.DrugService;
import com.asl.pms.service.PurchaseReturnService;
import com.asl.pms.service.PurchaseService;
import com.asl.pms.util.GlobalMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/purchaseReturn")
public class PurchaseReturnController {


    @Autowired
    GlobalMethod globalMethod;

    @Autowired
    PurchaseService purchaseService;

    @Autowired
    PurchaseReturnService purchaseReturnService;

    @Autowired
    DrugService drugService;


    @RequestMapping(value = "/viewPurchaseForm", method = RequestMethod.GET)
    public String viewPurchaseForm(Model model) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        List<PurchaseMaster> purchaseMasterList = new ArrayList<>();
        purchaseMasterList = purchaseService.getAllByReturnFlag(Constants.PURCHASE_NOT_RETURN);

        model.addAttribute("purchaseMasterList", purchaseMasterList);
        return "dashboard/purchasereturn/view";
    }

    @RequestMapping(value = "/viewPurchaseList", method = RequestMethod.GET)
    public String viewPurchaseList(Model model, @ModelAttribute("purchaseMaster") PurchaseMaster purchaseMaster,
                                   BindingResult result) throws IOException {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        List<PurchaseMaster> purchaseMasterList = new ArrayList<>();
        if (purchaseMaster.getPurchaseNo().equals("") && purchaseMaster.getSpInvId().equals("")) {
            purchaseMasterList = purchaseService.getAllByReturnFlag(Constants.PURCHASE_NOT_RETURN);
        } else {
            purchaseMasterList = purchaseService.qryByReturnFlugAndPurchaseNoOrInvoiceId(
                    purchaseMaster.getPurchaseNo(), purchaseMaster.getSpInvId()
            );

        }

        model.addAttribute("purchaseNo", purchaseMaster.getPurchaseNo().equals("") ? "" : purchaseMaster.getPurchaseNo());
        model.addAttribute("spInvId", purchaseMaster.getSpInvId().equals("") ? "" : purchaseMaster.getSpInvId());
        model.addAttribute("purchaseMasterList", purchaseMasterList);
        model.addAttribute("purchaseMaster", purchaseMaster);

        return "dashboard/purchasereturn/view";
    }

    @RequestMapping(value = "/editPurchaseList/{id}", method = RequestMethod.GET)
    public String editPurchaseList(Model model, @PathVariable Long id) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        PurchaseMaster purchaseMaster = purchaseService.getPurchaseMasterById(id);
        List<PurchaseDetails> purchaseDetails = purchaseService.findByPurchaseMaster(purchaseMaster);
        purchaseMaster.setPurchaseDetails(purchaseDetails);

        model.addAttribute("purchaseId", id);
        model.addAttribute("purchaseDetails", purchaseDetails);

        return "dashboard/purchasereturn/edit";
    }

    @RequestMapping(value = "/updatePurchase", method = RequestMethod.POST)
    public String updatePurchase(Model model, @ModelAttribute("purchaseReturn") PurchaseReturn purchaseReturn,
                                 BindingResult result) {

        try {
            List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
            model.addAttribute("menulist", stringList);
            model.addAttribute("image", globalMethod.getUserImage());
            model.addAttribute("fullname", globalMethod.getPrincipalFullName());

            purchaseReturn.setCreatedBy(globalMethod.getPrincipal());
            purchaseReturn.setCreateTime(LocalDateTime.now());
            purchaseReturn.setPurchaseMaster(purchaseReturn.getPurchaseMaster());
            List<PurchaseReturnDetails> purchaseReturnDetailsList = new ArrayList<>();
            List<PurchaseReturnDetails> returnDetailsList = purchaseReturn.getPurchaseReturnDetails();
            purchaseReturn.setPurchaseReturnDetails(returnDetailsList);
            // save PurchaseReturn
            PurchaseReturn savedPurchaseReturn = purchaseReturnService.savePurchaseReturn(purchaseReturn);

            if (returnDetailsList.size() > 0 || returnDetailsList != null) {
                returnDetailsList.forEach(pReturn ->
                        {
                            pReturn.setPurchaseReturn(savedPurchaseReturn);
                            pReturn.setDrug(pReturn.getDrug());
                            purchaseReturnDetailsList.add(pReturn);
                        }
                );

                List<PurchaseReturnDetails> details = purchaseReturnService.savePurchaseReturnDetails(returnDetailsList);
                PurchaseMaster purchaseMaster = purchaseReturn.getPurchaseMaster();
                purchaseMaster.setReturnFlag(Constants.PURCHASE_RETURN);
                PurchaseMaster master = purchaseService.savePurchaseMaster(purchaseMaster);
                boolean isUpdated = purchaseReturnService.updateTransactionAndStock(purchaseReturn, details);
                if (!isUpdated) {
                    return "redirect:/purchasereturn/editPurchaseList";
                }
                model.addAttribute("detailsList", details);

                return "dashboard/purchasereturn/view";
            } else {
                return "redirect:/purchasereturn/editPurchaseList";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/purchasereturn/editPurchaseList";
        }
    }


    @RequestMapping(value = "/viewReturnForm", method = RequestMethod.GET)
    public String viewReturn(Model model) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        return "dashboard/purchasereturn/viewReturnList";
    }

    @RequestMapping(value = "/viewReturnList", method = RequestMethod.GET)
    public String viewReturnList(Model model,
                                 @ModelAttribute("purchaseMaster") PurchaseMaster purchaseMaster,
                                 BindingResult result) throws IOException {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        List<PurchaseReturn> purchaseReturnList = new ArrayList<>();
        purchaseReturnList = purchaseReturnService.getPurchaseReturnList(purchaseMaster.getPurchaseNo(), purchaseMaster.getSpInvId());


        model.addAttribute("purchaseNo", purchaseMaster.getPurchaseNo().equals("") ? "" : purchaseMaster.getPurchaseNo());
        model.addAttribute("spInvId", purchaseMaster.getSpInvId().equals("") ? "" : purchaseMaster.getSpInvId());
        model.addAttribute("purchaseReturnList", purchaseReturnList);

        return "dashboard/purchasereturn/viewReturnList";
    }

    @RequestMapping(value = "/showDetailsList/{id}", method = RequestMethod.GET)
    public String showDetailsList(Model model, @PathVariable Long id) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        PurchaseReturn purchaseReturn = purchaseReturnService.getPurchaseReturnById(id);
        List<PurchaseReturnDetails> purchaseReturnDetailsList = purchaseReturnService.findByPurchaseReturn(purchaseReturn);
        purchaseReturn.setPurchaseReturnDetails(purchaseReturnDetailsList);

        model.addAttribute("purchaseId", id);
        model.addAttribute("purchaseReturnDetailsList", purchaseReturnDetailsList);

        return "dashboard/purchasereturn/viewReturnDetailsList";
    }


}
