package com.kiwi.features.incidences.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncidenceDomain {
    private String name;

    public IncidenceDomain(String name) {
        this.name = name;
    }
}
