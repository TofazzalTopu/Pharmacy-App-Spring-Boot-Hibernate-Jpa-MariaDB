package com.asl.pms.controller;

import com.asl.pms.model.Branch;
import com.asl.pms.service.BranchService;
import com.asl.pms.service.UserService;
import com.asl.pms.util.GlobalMethod;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BranchController {

    @Autowired
    BranchService branchService;
    @Autowired
    UserService userService;
    @Autowired
    private Environment environment;
    @Autowired
    Enforcer enforcer;
    @Autowired
    GlobalMethod globalMethod;

    @RequestMapping(value = {"/adminpanel/branches/brancheslist"}, method = RequestMethod.GET)
    public String allFunctions(Model model){
        model.addAttribute("branchesList", branchService.getAll());
        List<String> stringList = getAllFromCasbin(userService.findByUserName(getPrincipal()).getRoles().iterator().next().getName());
        model.addAttribute("menulist", stringList);
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        model.addAttribute("image", globalMethod.getUserImage());
        return "dashboard/branches/branches_list";
    }


    @RequestMapping(value = {"/adminpanel/branches/addbranches"}, method = RequestMethod.GET)
    public String addFunctions(Model model){
        model.addAttribute("branches", new Branch());
        List<String> stringList = getAllFromCasbin(userService.findByUserName(getPrincipal()).getRoles().iterator().next().getName());
        model.addAttribute("menulist", stringList);
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        model.addAttribute("image", globalMethod.getUserImage());
        return "dashboard/branches/add_branches";
    }

    @RequestMapping(value = {"/adminpanel/branches/savebranches"}, method = RequestMethod.POST)
    public String saveFunctions(Model model,@Valid @ModelAttribute("branches") Branch branches){
        branchService.saveBranchs(branches);
        return "redirect:/adminpanel/branches/brancheslist";
    }


    @RequestMapping(value = {"/adminpanel/branches/branchesedit/{id}"}, method = RequestMethod.GET)
    public String editFunctions(@PathVariable Long id, Model model){
        model.addAttribute("branches", branchService.findOne(id));
        List<String> stringList = getAllFromCasbin(userService.findByUserName(getPrincipal()).getRoles().iterator().next().getName());
        model.addAttribute("menulist", stringList);
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        model.addAttribute("image", globalMethod.getUserImage());
        return "dashboard/branches/edit_branches";
    }

    @RequestMapping(value = {"/adminpanel/branches/updatebranches/{id}"}, method = RequestMethod.POST)
    public String saveFunctions(Model model, @PathVariable Long id,@Valid @ModelAttribute("branches") Branch branches){
        branches.setId(id);
        branchService.saveBranchs(branches);
        return "redirect:/adminpanel/branches/brancheslist";
    }

    private String getPrincipal() {
        String userName = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }

    public List<String> getAllFromCasbin(String role){
        List<String> stringList = new ArrayList<>();
        try {
            Class.forName(environment.getRequiredProperty("spring.datasource.driver-class-name"));

            String url = environment.getRequiredProperty("spring.datasource.url");
            String username = environment.getRequiredProperty("spring.datasource.username");
            String password = environment.getRequiredProperty("spring.datasource.password");

            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "SELECT v1 from casbin_rule WHERE v0=? AND v2=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, role);
            statement.setString(2, "GET");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                stringList.add(resultSet.getString("v1"));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stringList;
    }
}
