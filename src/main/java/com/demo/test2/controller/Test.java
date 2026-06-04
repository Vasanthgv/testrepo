package com.demo.test2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
	
	
	@GetMapping("/gv")
	public String getName() {
		return "Welcome Vasanth";
	}

}
