package com.kiwi.features.incidences.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncidenceDomain {
    private String name;
    private boolean value;

    public IncidenceDomain(String name, boolean value) {
        this.name = name;
        this.value = value;
    }
}
