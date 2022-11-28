package com.api.goldenraspbarry.resources;

import com.api.goldenraspbarry.domain.DTO.AwardsBreakDTO;
import com.api.goldenraspbarry.services.IndicatedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/indicated")
public class IndicatedResource {

    @Autowired
    private IndicatedService indicatedService;

    @GetMapping(value = "/awardsbreak")
    public ResponseEntity<AwardsBreakDTO> findAwardsBreak(){
        return ResponseEntity.ok().body(indicatedService.findAwardsBreak());
    }
}
