package com.asl.pms.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MyErrorController implements ErrorController {

	@RequestMapping("/error")
	public String handleError(HttpServletRequest httpServletRequest, Model model) {
		Object status = httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());

			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				model.addAttribute("exceptionMessage", "");
				return "404";
			}
		}
		model.addAttribute("exceptionMessage", "");
		return "404";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}
}
