package com.asl.pms.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asl.pms.model.Drug;
import com.asl.pms.model.Stock;
import com.asl.pms.service.DrugService;
import com.asl.pms.service.StockService;
import com.asl.pms.util.GlobalMethod;
import com.asl.pms.viewmodels.DrugStock;

@Controller
public class StockController {

    @Autowired
    GlobalMethod globalMethod;

    @Autowired
    private DrugService drugService;
    
    @Autowired
    private StockService stockService;

    @ResponseBody
    @RequestMapping(value = "/notification/getNotificationCount")
    public int getNotificationCount() {       
        int count = 0;
        count=stockService.showMedicineStockShortageCount();
        return count;
    }


    @RequestMapping(value = "/notification/getStockList")
    public String getStockList(Model model) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        List<Stock> stockList = new ArrayList<>();
        stockList = stockService.getStockList();
        model.addAttribute("stockList", stockList);
        return "dashboard/notification/list";
    }
    
    @RequestMapping(value = "/stock/stockList")
    public String showStockList(Model model) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());       
        List<DrugStock> stockList = new ArrayList<>();
        stockList = stockService.showMedicineStock();
        model.addAttribute("stockList", stockList);        
       
        return "dashboard/notification/stockList";
    }
    
   
	
    @RequestMapping(value = "/stock/medicineInOutHistory/{id}", method = RequestMethod.GET)
    public String medicineInOutHistory(Model model, @PathVariable String id) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        List<DrugStock> stockList= stockService.medicineInOutHistory(id);        
        model.addAttribute("stockList", stockList);
       
        
        Drug drug = drugService.findDrug(Long.parseLong(id));
        model.addAttribute("drug", drug);
       
        return "dashboard/notification/medicineInOutHistory";
    }
}
