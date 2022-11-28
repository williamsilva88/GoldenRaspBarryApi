package com.api.goldenraspbarry.services;

import com.api.goldenraspbarry.domain.DTO.AwardsBreakDTO;
import com.api.goldenraspbarry.domain.Indicated;

import java.io.BufferedReader;

public interface IndicatedService {

    Indicated findById(Integer id);

    AwardsBreakDTO findAwardsBreak();
    void getCSVDataIndicated(String fullPath);
    void getCSVDataIndicated(String fullPath, BufferedReader br);
    void getCSVDataIndicated(BufferedReader br);
}
