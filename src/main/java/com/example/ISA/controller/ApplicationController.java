package com.example.ISA.controller;

import com.example.ISA.controller.form.UserForm;
import com.example.ISA.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ApplicationController {
    @Autowired
    UserService userService;
    @Autowired
    HttpServletRequest request;


}
