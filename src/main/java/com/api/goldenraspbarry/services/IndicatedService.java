package com.api.goldenraspbarry.services;

import com.api.goldenraspbarry.domain.DTO.AwardsBreakDTO;
import com.api.goldenraspbarry.domain.Indicated;

import java.io.BufferedReader;
import java.util.List;

public interface IndicatedService {

    Indicated findById(Integer id);

    AwardsBreakDTO findAwardsBreak();
    List<Indicated> getCSVDataIndicated(String fullPath);
    List<Indicated> getCSVDataIndicated(String fullPath, BufferedReader br);
    List<Indicated> getCSVDataIndicated(BufferedReader br);
    AwardsBreakDTO getAwardsBreak(List<Indicated> indicated);
}
