package com.asl.pms.controller;

import com.asl.pms.configuration.Constants;
import com.asl.pms.model.*;
import com.asl.pms.service.DrugService;
import com.asl.pms.service.SalesReturnService;
import com.asl.pms.service.SalesService;
import com.asl.pms.util.GlobalMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/salesReturn")
public class SalesReturnController {

    @Autowired
    GlobalMethod globalMethod;

    @Autowired
    DrugService drugService;

    @Autowired
    SalesService salesService;

    @Autowired
    SalesReturnService salesReturnService;

    @RequestMapping(value = "/salesDetailsView/{id}", method = RequestMethod.GET)
    public String show(Model model, @PathVariable Long id) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        SalesMaster salesMaster = salesService.getSalesMasterById(id);
        List<SalesDetails> salesDetails = salesService.findBySalesMaster(salesMaster);
        salesMaster.setSalesDetails(salesDetails);
        model.addAttribute("salesMasterId", salesMaster.getId());
        model.addAttribute("salesMaster", salesMaster);

        return "dashboard/salesreturn/view";
    }


    @RequestMapping(value = "/executeSalesReturn", method = RequestMethod.POST)
    public String updatePurchase(Model model, @ModelAttribute("salesReturn") SalesReturn salesReturn,
                                 BindingResult result) {

        try {
            List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
            model.addAttribute("menulist", stringList);
            model.addAttribute("image", globalMethod.getUserImage());
            model.addAttribute("fullname", globalMethod.getPrincipalFullName());

            SalesMaster salesMaster = salesService.getSalesMasterById(salesReturn.getSalesMaster().getId());
            salesReturn.setCreatedBy(globalMethod.getPrincipal());
            salesReturn.setCreateTime(LocalDateTime.now());
            salesReturn.setSalesMaster(salesMaster);

            List<SalesReturnDetails> salesReturnDetailsList = new ArrayList<>();
            List<SalesReturnDetails> returnDetailsList = salesReturn.getSalesReturnDetails();
            salesReturn.setSalesReturnDetails(returnDetailsList);

            // save SalesReturn
            SalesReturn savedSalesReturn = salesReturnService.saveSalesReturn(salesReturn);
            if (returnDetailsList.size() > 0 || returnDetailsList != null) {
                returnDetailsList.forEach(pReturn ->
                        {
                            double amount = pReturn.getDrug().getPrice() * Double.parseDouble(pReturn.getSalesQty());
                            pReturn.setSalesReturn(savedSalesReturn);
                            pReturn.setDrug(pReturn.getDrug());
                            pReturn.setSalesAmount(amount);
                            salesReturnDetailsList.add(pReturn);
                        }
                );

                List<SalesReturnDetails> details = salesReturnService.saveSalesReturnDetails(returnDetailsList);

                SalesMaster newSalesMaster = salesReturn.getSalesMaster();
                newSalesMaster.setReturnFlag(Constants.SALES_RETURN);
                SalesMaster master = salesService.saveSalesMaster(newSalesMaster);

                boolean isUpdated = salesReturnService.updateTransactionAndStock(salesReturn, details);

                if (!isUpdated) {
                    return "redirect:/salesreturn/salesDetailsView";
                }
                model.addAttribute("salesReturn", savedSalesReturn);
                model.addAttribute("salesReturnList", details);

                return "dashboard/salesreturn/salesList";
            } else {
                return "redirect:/salesreturn/salesDetailsView";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/salesreturn/salesDetailsView";
        }
    }


    @RequestMapping(method = RequestMethod.GET, value = {"/salesReturnList"})
    public String getOrderList(Model model, @RequestParam(required = false, defaultValue = "0") Integer page,
                               @RequestParam(required = false, defaultValue = "50") Integer total,
                               @RequestParam(required = false, defaultValue = "name") String sort) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        List<SalesReturn> salesReturnList = salesReturnService.getAll();
        model.addAttribute("salesReturnList", salesReturnList);

        return "dashboard/salesreturn/viewSalesReturnList";
    }

    @RequestMapping(value = "/showSalesReturnDetails/{id}", method = RequestMethod.GET)
    public String showDetailsList(Model model, @PathVariable Long id) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        SalesReturn salesReturn = salesReturnService.findById(id);
        List<SalesReturnDetails> salesReturnDetailsList = salesReturnService.findAllBySalesReturn(salesReturn);
        salesReturn.setSalesReturnDetails(salesReturnDetailsList);

        model.addAttribute("salesReturnId", id);
        model.addAttribute("salesReturnDetailsList", salesReturnDetailsList);

        model.addAttribute("salesReturn", salesReturn);
        model.addAttribute("salesReturnList", salesReturnDetailsList);

        return "dashboard/salesreturn/salesList";
    }


}
