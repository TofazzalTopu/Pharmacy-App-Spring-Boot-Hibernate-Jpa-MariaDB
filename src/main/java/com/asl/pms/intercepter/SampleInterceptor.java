package com.asl.pms.intercepter;

import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SampleInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	Enforcer enforcer;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String sub = getPrincipal();
		String requestURI = request.getRequestURI();
		String obj = requestURI.substring(request.getContextPath().length());
		String act = request.getMethod();
		
		
		if (obj.startsWith("/assets") || obj.startsWith("/uploads") || obj.startsWith("/error")) {
		//if(requestURI.startsWith("/assets") || requestURI.startsWith("/error") || requestURI.startsWith("/uploads")){
			return true;
		}

		if(enforcer.enforce(sub, obj, act)) {
			return true;
		} else {
			if(sub.equals("anonymousUser"))
				response.sendRedirect(request.getContextPath()+"/login");
			else{
				response.sendRedirect(request.getContextPath()+"/error");
			}
		}
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
	
	 
    private String getPrincipal() {
        String userName = null;
		Object principal = null;

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null)
        	principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }
}