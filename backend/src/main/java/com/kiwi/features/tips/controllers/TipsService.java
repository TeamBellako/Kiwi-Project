package com.kiwi.features.tips.controllers;

import com.kiwi.features.tips.data.TipDomain;
import com.kiwi.features.tips.data.TipMapper;
import com.kiwi.features.tips.data.TipPersistence;
import com.kiwi.features.tips.exceptions.TipNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TipsService {
    private final TipsRepository tipsRepository;

    public TipsService(TipsRepository tipsRepository) {
        this.tipsRepository = tipsRepository;
    }

    public TipDomain getTip(Long id) {
        Optional<TipPersistence> tipPersistence = tipsRepository.findById(id);
        
        if (tipPersistence.isPresent()) { return TipMapper.toDomain(tipPersistence.get()); }
        
        throw new TipNotFoundException(id);
    }
}
