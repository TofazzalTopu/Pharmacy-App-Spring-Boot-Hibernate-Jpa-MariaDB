package com.asl.pms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.asl.pms.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class GlobalMethod {
	@Autowired
    Environment environment;
	
    public String getPrincipal() {
        String userName = null;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();

        } else {
            userName = principal.toString();
        }
        return userName;
    }

    public String getPrincipalFullName() {

        String userFullname=null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {

            userFullname = ((User) principal).getFirstName()+" "+((User) principal).getLastName();
        } else {
            userFullname = principal.toString();
        }

        return userFullname;
    }

    public String getUserImage() {

        String imageName=null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {

            imageName = ((User) principal).getPhoto();
        } else {
            imageName = "";
        }

        return imageName;
    }

    public String getRole(){
        String name = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRoles().iterator().next().getName();

        return name;
    }

    Date date = null;
    public Date getDate(){
        String timeStampl = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String pattern = "yyyy-mm-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        try {
            date = simpleDateFormat.parse(timeStampl);
        } catch (ParseException e) {
            System.out.println("The exception is : "+e);
        }
        return date;
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