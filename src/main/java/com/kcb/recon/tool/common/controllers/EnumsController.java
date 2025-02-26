package com.kcb.recon.tool.common.controllers;

import com.kcb.recon.tool.common.enums.FrequencyOfIntervention;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/Configs")
@CrossOrigin
public class EnumsController {

    @GetMapping("/FrequencyOfIntervention")
    public ResponseEntity<?> FrequencyOfIntervention() {
        return new ResponseEntity<>(Arrays.asList(FrequencyOfIntervention.values()), HttpStatus.OK);
    }

}
