package com.kiwi.features.tips.data;

public class TipMapper {
    public static TipDomain toDomain(TipPersistence persistence) {
        return new TipDomain(persistence.getTitle(), persistence.getText(), persistence.getReadMoreURL());
    }
    
    public static TipDTO toDTO(TipDomain domain) {
        TipDTO dto = new TipDTO();
        
        dto.setTitle(domain.getTitle());
        dto.setText(domain.getText());
        dto.setReadMoreURL(domain.getReadMoreURL());
        
        return dto;
    }
    
    public static TipPersistence toPersistence(TipDomain domain) {
        TipPersistence persistence = new TipPersistence();

        persistence.setTitle(domain.getTitle());
        persistence.setText(domain.getText());
        persistence.setReadMoreURL(domain.getReadMoreURL());

        return persistence;
    }
}
