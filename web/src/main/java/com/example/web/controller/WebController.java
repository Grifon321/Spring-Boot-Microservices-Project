package com.example.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.web.model.Task;
import com.example.web.service.ApiService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SessionAttributes({"token", "username"})
public class WebController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/")
    public String emptyPage() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String password, @RequestParam String email, Model model) {
        String responce = apiService.signup(username, password, email);
        String token = apiService.authenticate(username, password);
        if (token != null) {
            return "redirect:/login";
        }
        return "signup";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        String token = apiService.authenticate(username, password);
        if (token != null) {
            model.addAttribute("token", token);// Save token in session
            model.addAttribute("username", username);// Save token in session
            return "redirect:/content";
        }
        return "login";
    }

    @PostMapping("/content")
    public String contentPageRedirect() {
        return "login";
    }

    @GetMapping("/content")
    public String contentPage(Model model) {
        if (model.getAttribute("token") == null)
            return "redirect:/login";

        String token = (String) model.getAttribute("token");
        String username = (String) model.getAttribute("username");

        if (token != "true") {
            List<Task> contents = apiService.getAllTasksByUsername(token, username);
            model.addAttribute("contents", contents);
            return "content";
        } else {
            return "redirect:/login";
        }
    }

    

    @PostMapping("/search")
    public String searchContents(@RequestParam String query, Model model) {
        if (model.getAttribute("token") == null)
            return "redirect:/login";

        String token = (String) model.getAttribute("token");
        String username = (String) model.getAttribute("username");
        
        if (token != "true") {
            List<Task> contents = apiService.searchByUsernameQuery(token, username, query);
            model.addAttribute("contents", contents);
            return "content";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/createTask")
    public String createTask(@RequestParam("name") String name,
                                @RequestParam("text") String text,
                                @RequestParam("deadline") String deadline,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (model.getAttribute("token") == null)
            return "redirect:/login";

        String token = (String) model.getAttribute("token");
        String username = (String) model.getAttribute("username");
        
        if (token != "true") {
            apiService.createNewTask(token, username, name, text, deadline);
            return "redirect:/content";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/editTask")
    public String editTask(@RequestParam("id") String id,
                                @RequestParam("name") String name,
                                @RequestParam("text") String text,
                                @RequestParam("deadline") String deadline,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (model.getAttribute("token") == null)
            return "redirect:/login";

        String token = (String) model.getAttribute("token");
        String username = (String) model.getAttribute("username");
        
        if (token != "true") {
            apiService.editTask(id,token, username, name, text, deadline);
            return "redirect:/content";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/deleteTask")
    public String deleteTask(@RequestParam("id") String id,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (model.getAttribute("token") == null)
            return "redirect:/login";

        String token = (String) model.getAttribute("token");
        String username = (String) model.getAttribute("username");
        
        if (token != "true") {
            apiService.deleteTask(id,token);
            return "redirect:/content";
        } else {
            return "redirect:/login";
        }
    }

    

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
