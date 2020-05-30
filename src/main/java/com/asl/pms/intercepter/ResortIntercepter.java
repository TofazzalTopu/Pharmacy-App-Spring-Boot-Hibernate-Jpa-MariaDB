package com.asl.pms.intercepter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import java.nio.file.Paths;
import java.util.Locale;

@Component
public class ResortIntercepter implements WebMvcConfigurer {

	@Autowired
	SampleInterceptor sampleInterceptor;
	
	@Value("${pms.picture.upload.path}")
	private String picturePath;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(sampleInterceptor);
		//
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//System.out.println("addResourceHandlers L36: picturePath="+picturePath);
		//String path = System.getProperty("user.dir");
		String path =picturePath;
		registry.addResourceHandler("/uploads/**")
				.addResourceLocations(Paths.get(path).toUri().toString());
	//	.addResourceLocations(Paths.get(path).resolve("uploads").toUri().toString());
	}

	@Bean
	public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
		return new ResourceUrlEncodingFilter();
	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.US);
		return slr;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}
}
