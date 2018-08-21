package com.omt.modbus;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping(value = "/hello")
    public String say(){
        System.out.println("hello");
        return "hello word";
    }
}
