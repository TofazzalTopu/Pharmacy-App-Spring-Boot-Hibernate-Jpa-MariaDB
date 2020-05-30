package com.asl.pms.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.asl.pms.service.UserService;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "com.asl.pms")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Qualifier("userService")
    private final UserService userDetailsService;

    private final Environment environment;

    @Autowired
    public SecurityConfiguration(UserService userService, Environment environment) {
        this.userDetailsService = userService;
        this.environment = environment;
    }

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider());
    }

    private BCryptPasswordEncoder passwordEncoder() {
        return SecurityUtility.passwordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        http.addFilterBefore(filter, CsrfFilter.class);

        http.authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .and().formLogin().loginPage("/login").defaultSuccessUrl("/adminpanel/dashboard")
                .and().csrf().csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                .and().exceptionHandling().accessDeniedPage("/Access_Denied");
               // .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login");
    }

   /* @Bean
    public Enforcer enforcer() throws FileNotFoundException, IOException {
        JDBCAdapter adapter = new JDBCAdapter(environment.getRequiredProperty("spring.datasource.driver-class-name"),
                environment.getRequiredProperty("spring.datasource.url"), environment.getRequiredProperty("spring.datasource.username"),
                environment.getRequiredProperty("spring.datasource.password"), true);

        ClassLoader classLoader = getClass().getClassLoader();
        Enumeration c = classLoader.getResources("model.conf");
        while (c.hasMoreElements()){
            System.out.println(((URL)c.nextElement()).getPath() );
        }
//        for(int 1 = 0; i < c.)
//        System.out.println(c);
        Enforcer enforcer = null;
        try {
            File file1 = new File("G:\\Tofazzal\\ASL\\ASL Projetcs\\pharmacy\\pharmacy\\src\\main\\resources\\model.conf");
//            File file1 = new File(classLoader.getResource("model.conf").getFile());
//            File file1 = new File(getClass().getClassLoader().getResourceAsStream("/model.conf"));
//            (getClass().getClassLoader().getResourceAsStream("/model.conf").

            enforcer = new Enforcer(file1.getAbsolutePath(), adapter);

            enforcer.loadPolicy();
            // enforcer.addPolicy("asl123", "/superadmin", "GET");
            // enforcer.addPolicy("SUPERADMIN","/superadmin","GET");
            // enforcer.addPolicy("SUPERADMIN","/superadmin","POST");
            // enforcer.addGroupingPolicy("asl123","SUPERADMIN");

            //enforcer.savePolicy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enforcer;
    }*/


    /*@Bean
    public Enforcer enforcer() {
        JDBCAdapter adapter = new JDBCAdapter(environment.getRequiredProperty("spring.datasource.driver-class-name"),
                environment.getRequiredProperty("spring.datasource.url"), environment.getRequiredProperty("spring.datasource.username"),
                environment.getRequiredProperty("spring.datasource.password"), true);

        ClassLoader classLoader = getClass().getClassLoader();
        File file1 = new File(classLoader.getResource("model.conf").getFile());

        Enforcer enforcer = new Enforcer(file1.getAbsolutePath(), adapter);

        enforcer.loadPolicy();
        return enforcer;
    }*/

    @Bean
    public Enforcer enforcer() throws FileNotFoundException, IOException {
        JDBCAdapter adapter = new JDBCAdapter(environment.getRequiredProperty("spring.datasource.driver-class-name"),
                environment.getRequiredProperty("spring.datasource.url"), environment.getRequiredProperty("spring.datasource.username"),
                environment.getRequiredProperty("spring.datasource.password"), true);

        ClassLoader classLoader = getClass().getClassLoader();
        Enumeration c = classLoader.getResources("model.conf");
        while (c.hasMoreElements()) {
            System.out.println(((URL) c.nextElement()).getPath());
        }
        Enforcer enforcer = null;
        try {
            String rootDir = System.getProperty("user.dir");
            URL path = this.getClass().getResource("model.conf");
            File file = new File(rootDir + File.separator + "/src/main/resources/model.conf");
            enforcer = new Enforcer(file.getAbsolutePath(), adapter);
            enforcer.loadPolicy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enforcer;
    }
}
