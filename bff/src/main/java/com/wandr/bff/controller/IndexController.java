package com.wandr.bff.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin(origins = {"http://localhost:8080"})
public class IndexController {

    @RequestMapping(value={"", "/"})
    public String getBranches() {
        return "index.html";
    }
}
