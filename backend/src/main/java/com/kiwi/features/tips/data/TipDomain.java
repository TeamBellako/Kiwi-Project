package com.kiwi.features.tips.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipDomain {
    private String title;
    private String text;
    private String readMoreURL;

    public TipDomain(String title, String text, String readMoreURL) {
        this.title = title;
        this.text = text;
        this.readMoreURL = readMoreURL;
    }
}
