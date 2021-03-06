package com.limbo.app.web;

import java.util.Map;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.limbo.app.service.ClientService;

@Controller
public class MainController {
	
	@Autowired
	private ClientService clientService;
	
	@RequestMapping("/")
	public String home() {
		return "redirect:/index";
	}
	
	@RequestMapping("/index")
	public String listClients(Map<String, Object> map) {
		return "redirect:/repair/list";	
	}
	
	@RequestMapping("/contacts")
	public String contactForm(Map<String, Object> map) {
		return "about";	
	}
	
}
