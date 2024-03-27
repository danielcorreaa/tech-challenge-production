package com.techchallenge.infrastructure.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectApi {
	
	@GetMapping("/production")
	public String swagger() {
		return "redirect:swagger-ui.html";
	}

}
