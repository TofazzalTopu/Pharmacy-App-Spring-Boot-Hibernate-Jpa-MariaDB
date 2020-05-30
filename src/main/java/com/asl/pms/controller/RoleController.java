package com.asl.pms.controller;

import com.asl.pms.model.*;
import com.asl.pms.service.RoleService;
import com.asl.pms.service.UserService;
import com.asl.pms.util.GlobalMethod;
import com.google.gson.GsonBuilder;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class RoleController {
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    @Autowired
    private Environment environment;
    @Autowired
    private RoleService roleService;
    List<Casbin> casbinList= new ArrayList<Casbin>();
    @Autowired
    Enforcer enforcer;
    @Autowired
    UserService userService;
    @Autowired
    GlobalMethod globalMethod;

    @PostConstruct
    public void init() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods =
                this.requestMappingHandlerMapping.getHandlerMethods();

        for(Map.Entry<RequestMappingInfo, HandlerMethod> item : handlerMethods.entrySet()) {
            RequestMappingInfo mapping = item.getKey();
            HandlerMethod method = item.getValue();

            for (String urlPattern : mapping.getPatternsCondition().getPatterns()) {
                casbinList.add(new Casbin(urlPattern, method.getMethod().getName()));
//                System.out.println(
//                        method.getBeanType().getName() + "#" + method.getMethod().getName() +
//                                " <-- " + urlPattern);
            }
        }
    }

    @RequestMapping(value = {"/adminpanel/casbin/addcasbin"}, method = RequestMethod.GET)
    public String getCasbinList(Model model){
        Tree tree = Tree.parseWithoutSelected(casbinList);
        model.addAttribute("tree", new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(Arrays.asList(tree.getRoot())));
        model.addAttribute("casbinrole", new CasbinRole());
        List<String> stringList = getAllFromCasbin(userService.findByUserName(getPrincipal()).getRoles().iterator().next().getName());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("menulist", stringList);
        return "/dashboard/casbin/add_casbin";
    }

    @RequestMapping(value = {"/adminpanel/casbin/save"}, method = RequestMethod.POST)
    public String saveCasbin(Model model, @Valid @ModelAttribute("casbin")CasbinRole casbin, BindingResult result){
        roleService.saveRole(new Role(casbin.getRoleName()));
        insertToCasbin(casbin.getRoleName(),casbin.getUrlName());
        enforcer.loadPolicy();
        return "redirect:/adminpanel/casbin/casbinlists";
    }

    @RequestMapping(value = {"/adminpanel/casbin/casbinlists"}, method = RequestMethod.GET)
    public String casbinLists(Model model){
        model.addAttribute("casbinlist", roleService.getAll());
        List<String> stringList = getAllFromCasbin(userService.findByUserName(getPrincipal()).getRoles().iterator().next().getName());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        return "/dashboard/casbin/casbin_list";
    }

    @RequestMapping(value = {"/adminpanel/casbin/editcasbin/{name}"}, method = RequestMethod.GET)
    public String deleteCasbin(Model model, @PathVariable String name){
        List<String> allFromCasbin = getAllFromCasbin(name);
        CasbinRole casbinRole = new CasbinRole(name, allFromCasbin);
        Tree tree = Tree.parse(casbinList, allFromCasbin);

        model.addAttribute("casbinrole", casbinRole);
        model.addAttribute("selectedList", allFromCasbin);
        model.addAttribute("tree", new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(Arrays.asList(tree.getRoot())));
        List<String> stringList = getAllFromCasbin(userService.findByUserName(getPrincipal()).getRoles().iterator().next().getName());
        model.addAttribute("menulist", stringList);
        model.addAttribute("image", globalMethod.getUserImage());
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        return "/dashboard/casbin/edit_casbin";
    }

    @RequestMapping(value = {"/adminpanel/casbin/casbinupdate"}, method = RequestMethod.POST)
    public String updateCasbin(Model model, @Valid @ModelAttribute("casbin")CasbinRole casbin){
        deleteFromCasbin(casbin.getRoleName());
        enforcer.loadPolicy();
        List<String> stringList = casbin.getUrlName();
        insertToCasbin(casbin.getRoleName(), stringList);
        enforcer.loadPolicy();
        return "redirect:/adminpanel/casbin/casbinlists";
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

    public void deleteFromCasbin(String name){
        try {
            Class.forName(environment.getRequiredProperty("spring.datasource.driver-class-name"));

            String url = environment.getRequiredProperty("spring.datasource.url");
            String username = environment.getRequiredProperty("spring.datasource.username");
            String password = environment.getRequiredProperty("spring.datasource.password");

            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "DELETE from casbin_rule WHERE ptype=? AND v0=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "p");
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertToCasbin(String roleName,List<String> stringList){
        for(String endpoint: stringList){
            for(int i=0; i<2; i++){
                if(i==0)
                    enforcer.addPolicy(roleName, endpoint, "GET");
                else
                    enforcer.addPolicy(roleName, endpoint, "POST");
            }
        }
        enforcer.addPolicy(roleName, "/", "GET");
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
