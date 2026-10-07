package com.kiwi.features.tips.controllers;

import com.kiwi.features.tips.data.TipDomain;
import com.kiwi.features.tips.data.TipMapper;
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
        Optional<TipsRepository.LocalizedTip> tip = tipsRepository.findLocalizedById(id);
        
        if (tip.isPresent()) {
            TipsRepository.LocalizedTip localizedTip = tip.get();
            return new TipDomain(localizedTip.getTitle(), localizedTip.getText(), localizedTip.getReadMoreURL());
        }
        
        throw new TipNotFoundException(id);
    }
}
