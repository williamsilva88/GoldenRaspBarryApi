package com.api.goldenraspbarry.config;

import com.api.goldenraspbarry.domain.Indicated;
import com.api.goldenraspbarry.repositories.IndicatedRepository;

import com.api.goldenraspbarry.services.IndicatedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.*;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("local")
public class LocalConfig {

    @Autowired
    private IndicatedRepository indicatedRepository;

    @Autowired
    private IndicatedService indicatedService;

    @Bean
    public void startDB(){
        indicatedService.getCSVDataIndicated();
    }



}
