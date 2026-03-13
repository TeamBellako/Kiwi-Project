package com.kiwi.features.tips.controllers;

import com.kiwi.features.tips.data.TipDTO;
import com.kiwi.features.tips.data.TipDomain;
import com.kiwi.features.tips.data.TipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tips")
public class TipsController {
    private final TipsService tipsService;
    
    @Autowired
    public TipsController(TipsService tipsService) {
        this.tipsService = tipsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipDTO> getTip(@PathVariable int id) {
        TipDomain domain = tipsService.getTip((long) id);
        return ResponseEntity.ok(TipMapper.toDTO(domain));
    }
}
