package com.asl.pms.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;

import org.casbin.jcasbin.main.Enforcer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.asl.pms.configuration.SecurityUtility;
import com.asl.pms.model.Role;
import com.asl.pms.model.User;
import com.asl.pms.service.BranchService;
import com.asl.pms.service.RoleService;
import com.asl.pms.service.UserService;
import com.asl.pms.util.GlobalMethod;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);
    @Autowired
	private Environment env;

	public String getPicturePath() {
		String picturePath = env.getProperty("pms.picture.upload.path");
		return picturePath;
	}

    @Autowired
    BranchService branchService;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    List<Role> roleList = new ArrayList<>();
    private Connection connection;
    @Autowired
    private Environment environment;
    @Autowired
    Enforcer enforcer;
    @Autowired
    GlobalMethod globalMethod;
    @Autowired
    ServletContext servletContext;

    @RequestMapping(value = {"/adminpanel/user/userlists"}, method = RequestMethod.GET)
    public String userList(Model model) {

        model.addAttribute("userlist", userService.findActiveUser(true));
        List<String> stringList = getAllFromCasbin(userService.findByUserName(getPrincipal()).getRoles().iterator().next().getName());
        model.addAttribute("menulist", stringList);
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        model.addAttribute("image", globalMethod.getUserImage());
        return "/dashboard/user/user_list";
    }

    @RequestMapping(value = {"/adminpanel/user/adduser"}, method = RequestMethod.GET)
    public String addUser(Model model) {
        roleList = roleService.getAll();
        model.addAttribute("branchlist", branchService.getAll());
        model.addAttribute("rolelist", roleList);
        model.addAttribute("user", new User());
        List<String> stringList = getAllFromCasbin(userService.findByUserName(getPrincipal()).getRoles().iterator().next().getName());
        model.addAttribute("menulist", stringList);
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        model.addAttribute("image", globalMethod.getUserImage());
        return "/dashboard/user/add_user";
    }

    @RequestMapping(value = {"/adminpanel/user/usersave"}, method = RequestMethod.POST)
    public String saveUser(Model model, @Valid @ModelAttribute("user") User user, BindingResult result) throws IOException {

        if(result.hasErrors()){
            logger.info("Validation Error");
            model.addAttribute("rolelist", roleList);
            model.addAttribute("user", user);
            List<String> stringList = getAllFromCasbin(userService.findByUserName(getPrincipal()).getRoles().iterator().next().getName());
            model.addAttribute("menulist", stringList);
            model.addAttribute("fullname", globalMethod.getPrincipalFullName());
            model.addAttribute("image", globalMethod.getUserImage());
            return "/dashboard/user/add_user";
        }

        User user1 = userService.usernameExist(user.getEmail());
        if(user1 != null){
            logger.info("Username already exist");
            user.setEmail("");
            model.addAttribute("user", user);
            model.addAttribute("rolelist", roleList);
            List<String> stringList = getAllFromCasbin(userService.findByUserName(getPrincipal()).getRoles().iterator().next().getName());
            model.addAttribute("menulist", stringList);
            model.addAttribute("fullname", globalMethod.getPrincipalFullName());
            model.addAttribute("image", globalMethod.getUserImage());
            return "/dashboard/user/add_user";
        }

        String base64String = user.getPhoto();
        String[] strings = base64String.split(",");
        String extension;
        switch (strings[0]) {
            case "data:image/jpeg;base64": extension = "jpeg"; break;
            case "data:image/png;base64":  extension = "png"; break;
            default: extension = "jpg"; break;
        }

        byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);

        String fileName = String.format("%s.%s", System.currentTimeMillis(), extension);
       
        Path picPath = Paths.get(getPicturePath());
		Files.write(picPath.resolve(fileName), data);

        user.setPhoto(fileName);

        for(String id: user.getSelectedRole()){
            Role role = roleList.stream().filter(role1 -> role1.getId().equals(Long.parseLong(id))).findFirst().orElse(null);
            user.getRoles().add(role);
        }

        user.setUserName(user.getEmail());
        user.setActive(true);
        user.setPassword(SecurityUtility.passwordEncoder().encode(user.getPassword()));
        userService.saveUser(user);
        insertToCasbin(user);
        enforcer.loadPolicy();
        return "redirect:/adminpanel/user/userlists";
    }

    @RequestMapping(value = {"/adminpanel/user/useredit/{id}"}, method = RequestMethod.GET)
    public String editUser(@PathVariable Long id, Model model){
        User user = userService.findOne(id);
        for(Role role: user.getRoles()){ user.getSelectedRole().add(role.getId()+""); }
        roleList = roleService.getAll();
        model.addAttribute("rolelist", roleList);
        model.addAttribute("branchlist", branchService.getAll());
        model.addAttribute("user", user);
        List<String> stringList = getAllFromCasbin(userService.findByUserName(getPrincipal()).getRoles().iterator().next().getName());
        model.addAttribute("menulist", stringList);
        model.addAttribute("fullname", globalMethod.getPrincipalFullName());
        model.addAttribute("image", globalMethod.getUserImage());
        return "/dashboard/user/edit_user";
    }

    @RequestMapping(value = {"/adminpanel/user/userupdate/{id}"}, method = RequestMethod.POST)
    public String updateUser(@PathVariable Long id, Model model, @Valid @ModelAttribute("user") User user, BindingResult result) throws IOException {
        if(result.hasErrors()){
            logger.info("Validation Error");
            model.addAttribute("rolelist", roleList);
            model.addAttribute("user", user);
            List<String> stringList = getAllFromCasbin(userService.findByUserName(getPrincipal()).getRoles().iterator().next().getName());
            model.addAttribute("menulist", stringList);
            model.addAttribute("fullname", globalMethod.getPrincipalFullName());
            model.addAttribute("image", globalMethod.getUserImage());
            return "/dashboard/user/edit_user";
        }

        String photo = user.getPhoto();
        if(!photo.isEmpty()){
            String base64String = photo;
            String[] strings = base64String.split(",");
            String extension;
            switch (strings[0]) {
                case "data:image/jpeg;base64": extension = "jpeg"; break;
                case "data:image/png;base64":  extension = "png"; break;
                default: extension = "jpg"; break;
            }
            byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);

            String fileName = String.format("%s.%s", System.currentTimeMillis(), extension);
           // Files.write(APP_PATH.resolve(UPLOADS_DIR).resolve(fileName), data);
            Path picPath = Paths.get(getPicturePath());
    		Files.write(picPath.resolve(fileName), data);

            user.setPhoto(fileName);

        }else {
            user.setPhoto(userService.findOne(id).getPhoto());
        }

        for(String roleId: user.getSelectedRole()){
            Role role = roleList.stream().filter(role1 -> role1.getId().equals(Long.parseLong(roleId))).findFirst().orElse(null);
            user.getRoles().add(role);
        }

        user.setId(id);
        user.setUserName(user.getEmail());
        user.setPassword(SecurityUtility.passwordEncoder().encode(user.getPassword()));

        user.setActive(true);
        userService.saveUser(user);
        insertToCasbin(user);
//        deleteFromCasbin(user);
//        insertToCasbin(user);
        enforcer.loadPolicy();
        return "redirect:/adminpanel/user/userlists";
    }

    @RequestMapping(value = {"/adminpanel/user/userdelete/{id}"}, method = RequestMethod.GET)
    public String deleteUser(@PathVariable Long id, Model model){
        User user = userService.findOne(id);
        userService.saveUser(user);
        user.setActive(false);
        enforcer.loadPolicy();
        return "redirect:/adminpanel/user/userlists";
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

    public void insertToCasbin(User user){
        for(Role role: user.getRoles()){
            enforcer.addGroupingPolicy(user.getUsername(),role.getName());
        }
    }

    public void deleteFromCasbin(User user){
        try {
            Class.forName(environment.getRequiredProperty("spring.datasource.driver-class-name"));

            String url = environment.getRequiredProperty("spring.datasource.url");
            String username = environment.getRequiredProperty("spring.datasource.username");
            String password = environment.getRequiredProperty("spring.datasource.password");

            connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = null;
            String deleteSql = "DELETE FROM casbin_rule WHERE v0 = ?";
            statement = connection.prepareStatement(deleteSql);
            statement.setString(1,user.getUsername());
            statement.executeUpdate();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
