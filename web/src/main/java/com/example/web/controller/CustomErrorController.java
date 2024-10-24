package com.example.web.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // You can log the error details if necessary
        Object status = request.getAttribute("javax.servlet.error.status_code");

        // Redirect to /new in case of any error
        return "redirect:/error";
    }

}