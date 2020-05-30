package com.asl.pms.controller;


import com.asl.pms.model.Demand;
import com.asl.pms.model.Drug;
import com.asl.pms.model.PurchaseMaster;
import com.asl.pms.service.*;
import com.asl.pms.util.GlobalMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/demand")
public class DemandController {

    @Autowired
    private Environment env;

    public String getPicturePath() {
        String picturePath = env.getProperty("pms.picture.upload.path");
        return picturePath;
    }


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
    DemandService demandService;

    @RequestMapping(method = RequestMethod.GET, value = {"/addDemand"})
    public String addPurchaseForm(Model model) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        Demand demand = new Demand();
        model.addAttribute("demand", demand);

        return "dashboard/demand/add";
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/demandList"})
    public String getOrderList(Model model, @ModelAttribute("demand") Demand demand,
                               BindingResult result) throws IOException {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        List<Demand> demandList = demandService.demandList();
       // demandList = demandService.getDemandListByIdAndPriority(demand.getDrug().getName(), demand.getPriority());

        model.addAttribute("demandList", demandList);
        return "dashboard/demand/list";
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/createDemand"})
    public String createDemand(Model model,
                               @RequestParam(required = false) Map<String, String> data) throws IOException {
        List<Demand> demandList = new ArrayList<>();
        Demand demand = new Demand();

        try {
            int count = Integer.parseInt(data.get("count"));

            for (int i = 0; i < count; i++) {
                Drug drug = drugService.findDrug(Long.parseLong(data.get("drugId[" + i + "]")));
                Demand newDemand = new Demand();
                newDemand.setDrug(drug);
                newDemand.setPriority(data.get("priority[" + i + "]"));
                newDemand.setRemarks(data.get("remarks[" + i + "]"));
                newDemand.setCreateDate(new Date());
                demandList.add(newDemand);
            }
            boolean isCreated = demandService.createDemandList(demandList);

            if (isCreated) {
                return "redirect:/demand/demandList";
            } else {
                List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
                model.addAttribute("menulist", stringList);
                model.addAttribute("image", globalMethod.getUserImage());
                model.addAttribute("fullname", globalMethod.getPrincipalFullName());
                model.addAttribute("demand", demand);
                return "dashboard/demand/add";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/demand/demandList";
    }

    @RequestMapping(value = "/showDemand/{id}", method = RequestMethod.GET)
    public String show(Model model, @PathVariable Long id) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        Demand demand = demandService.findById(id);
        model.addAttribute("demand", demand);

        return "dashboard/demand/view";
    }

    @RequestMapping(value = "/updateDemand/{id}", method = RequestMethod.GET)
    public String updateForm(Model model, @PathVariable Long id) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        Demand savedDemand = demandService.findById(id);

        model.addAttribute("demand", savedDemand);

        return "dashboard/demand/update";
    }

    @RequestMapping(value = "getDemandListByName", method = RequestMethod.GET)
    public String getDemandListByName(Model model, @RequestParam(required = false) String drugName, String priority) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        List<Demand> demandList = demandService.getDemandListByIdAndPriority(drugName, priority);
        model.addAttribute("demandList", demandList);
        model.addAttribute("drugName", drugName);
        model.addAttribute("priority", priority);
        Map<String, List> map = new HashMap<>();
        map.put("demandList", demandList);
        return "dashboard/demand/list";
    }
}
