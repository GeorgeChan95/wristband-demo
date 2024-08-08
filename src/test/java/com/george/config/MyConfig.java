package com.george.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.interfaces.MyInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.activation.DataHandler;
import java.util.List;

@Configuration
public class MyConfig {

//    @Autowired
//    private MyInterface myInterface;

    public MyConfig(ObjectMapper objectMapper) {
        System.out.println(1);
    }

    @Bean
    public DataHandler dataHandler(ObjectMapper objectMapper, List<MyInterface> myInterfaces) {
        System.out.println(objectMapper);
        System.out.println(myInterfaces);
        return null;
    }
}
