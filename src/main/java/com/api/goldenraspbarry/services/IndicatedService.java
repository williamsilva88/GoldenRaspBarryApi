package com.api.goldenraspbarry.services;

import com.api.goldenraspbarry.domain.DTO.AwardsBreakDTO;
import com.api.goldenraspbarry.domain.Indicated;

public interface IndicatedService {

    Indicated findById(Integer id);

    AwardsBreakDTO findAwardsBreak();
    void getCSVDataIndicated();
}
