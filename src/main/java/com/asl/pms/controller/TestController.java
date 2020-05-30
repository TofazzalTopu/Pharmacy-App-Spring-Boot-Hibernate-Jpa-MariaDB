package com.asl.pms.controller;

import com.asl.pms.model.Demand;
import com.asl.pms.service.DemandService;
import com.asl.pms.util.GlobalMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestController {

    @Autowired
    GlobalMethod globalMethod;

    @Autowired
    DemandService demandService;

    @RequestMapping(method = RequestMethod.GET, value = {"/demandList"})
    public String getOrderList(Model model,
                               @RequestParam(required = false) String drugName,
                               @RequestParam(required = false) String priority,
                               @RequestParam(required = false, defaultValue = "0") Integer page,
                               @RequestParam(required = false, defaultValue = "50") Integer total,
                               @RequestParam(required = false, defaultValue = "name") String sort) {
        List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());

        List<Demand> demandList = demandService.demandList();
        if (priority == null) {
            demandList = demandList;
        } else {
            demandList = demandService.getDemandListByIdAndPriority(drugName, priority);
        }

        model.addAttribute("demandList", demandList);
        return "dashboard/demand/list";
    }

    @ResponseBody
    @RequestMapping(value = "getDemandListByName", method = RequestMethod.GET)
//    public String getDemandListByName(Model model, @RequestParam(required = false) String drugName, String priority) {
    public List<Demand> getDemandListByName(@RequestParam(required = false) String drugName, String priority) {

      /*  List<String> stringList = globalMethod.getAllFromCasbin(globalMethod.getRole());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
*/
        List<Demand> demandList = demandService.getDemandListByIdAndPriority(drugName, priority);
      /*  model.addAttribute("demandList", demandList);
        model.addAttribute("drugName", drugName);
        model.addAttribute("priority", priority);*/
        Map<String, List> map = new HashMap<>();
        map.put("demandList", demandList);
        return demandList;
//        return "redirect:/demand/demandList";
//        model.addAttribute("demandList", demandList);

//        return "dashboard/demand/list";
    }
}
