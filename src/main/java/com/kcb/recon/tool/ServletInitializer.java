package com.kcb.recon.tool;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.
				 sources(ReconToolApplication.class)
				.properties("spring.config.location=file:/var/recontool/core/configuration/application.properties");
	}

}
