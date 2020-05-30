package com.asl.pms.controller;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.asl.pms.model.Drug;
import com.asl.pms.model.User;
import com.asl.pms.service.DrugService;
import com.asl.pms.service.RoleService;
import com.asl.pms.service.UserService;
import com.asl.pms.util.GlobalMethod;


@Controller
public class WelcomeController {

    @Autowired
    Enforcer enforcer;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    DrugService drugService;
    
    @Autowired
    Environment environment;
    @Autowired
    GlobalMethod globalMethod;
   

   

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getHome(Model model, HttpServletRequest request) {
        return "redirect:/adminpanel/dashboard";
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String loginPage(HttpServletRequest request, Model model) {
        if (getPrincipal().equals("anonymousUser")) {
        	 model.addAttribute("user", new User());
        	 return "login";
        }                   
        else {
        	return "redirect:/";
        }
        	
    }
   
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @RequestMapping(value = {"/adminpanel/dashboard"}, method = RequestMethod.GET)
    public String dashboard(Model model, @RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "50") Integer total,
			@RequestParam(required = false, defaultValue = "name") String sort) {
        List<String> stringList =  globalMethod.getAllFromCasbin(globalMethod.getRole());
        //
        model.addAttribute("menulist", stringList);
       
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        
        //
        
     
    		Sort.Order order;
    		if (sort.startsWith("-"))
    			order = Sort.Order.asc(sort.replace("-", ""));
    		else
    			order = Sort.Order.desc(sort);

    		Sort sortBy = Sort.by(order);
    		PageRequest pageable = PageRequest.of(page, total, sortBy);
    		Page<Drug> drugs = drugService.findDrugs(pageable);
    		List<Drug> content = drugs.getContent();
    		model.addAttribute("drugs", content);

    		model.addAttribute("total", drugs.getTotalElements());
    		model.addAttribute("pages", drugs.getTotalPages());
    		model.addAttribute("current", drugs.getNumber());
    		model.addAttribute("size", drugs.getSize());
    		model.addAttribute("sortBy", sort);

    		int previous = drugs.hasPrevious() ? drugs.previousPageable().getPageNumber() : 0;
    		model.addAttribute("previous", previous);

    		int next = drugs.hasNext() ? drugs.nextPageable().getPageNumber() : drugs.getTotalPages();
    		model.addAttribute("next", next);
        //

        return "/dashboard/dashboard";
    }

    @RequestMapping(value = {"/adminpanel/lock/lockmysession"}, method = RequestMethod.GET)
    public String lockmysession(Model model, HttpSession httpSession){
        String userName = null;
        String password = null;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
            password = ((UserDetails) principal).getPassword();

        } else {
            userName = principal.toString();
        }

        httpSession.setAttribute("password", password);
        httpSession.setAttribute("userName", userName);

        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        return "lockscreen";
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
