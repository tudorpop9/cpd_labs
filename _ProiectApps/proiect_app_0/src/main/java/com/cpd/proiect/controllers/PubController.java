package com.cpd.proiect.controllers;

import com.cpd.proiect.publish.FirstPublisher;
import com.cpd.proiect.publish.SecondPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PubController {

    @Autowired
    private FirstPublisher pub1;

    @Autowired
    private SecondPublisher pub2;

    @PostMapping(value = "/publisher1/{message}")
    public String publishOnFirstQueue(@PathVariable String message){
        pub1.publish(message);
        return "Message sent using pub1";
    }

    @PostMapping(value = "/publisher2/{message}")
    public String publishOnSecondQueue(@PathVariable String message){
        pub2.publish(message);
        return "Message sent using pub2";
    }
}
