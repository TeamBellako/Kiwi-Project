package com.kiwi.features.combat.data.domain;

import com.kiwi.features.combat.data.enums.StatType;
import lombok.*;

@Getter
@Builder
public class StatsDomain {

    private int currentHp;
    private int maxHp;

    private int shield;

    private int patk;
    private int matk;

    private int pdef;
    private int mdef;

    private int acc;
    private int eva;

    private int lck;

    private int turns;

    //------------------------------------------------------------------------------------------------------------------

    public int getStat(StatType stat) {

       switch (stat) {

           case CURRENT_HP -> {
               return this.currentHp;
           }

           case MAX_HP -> {
               return this.maxHp;
           }

           case SHIELD -> {
               return this.shield;
           }

           case PATK -> {
               return this.patk;
           }

           case MATK -> {
               return this.matk;
           }

           case PDEF -> {
               return this.pdef;
           }

           case MDEF -> {
               return this.mdef;
           }

           case ACC -> {
               return this.acc;
           }

           case EVA -> {
               return this.eva;
           }

           case LCK -> {
               return this.lck;
           }

           case TURNS -> {
               return this.turns;
           }

       }

       throw new IllegalArgumentException("Not valid stat type");
    }

    //------------------------------------------------------------------------------------------------------------------

    public void setStat(StatType stat, int value) {

        switch (stat) {

            case CURRENT_HP -> this.currentHp = Math.max(0, value);
            case MAX_HP -> this.maxHp = Math.max(1, value);

            case SHIELD -> this.shield = Math.max(0, value);

            case PATK -> this.patk = value;
            case MATK -> this.matk = value;

            case PDEF -> this.pdef = value;
            case MDEF -> this.mdef = value;

            case ACC -> this.acc = value;
            case EVA -> this.eva = value;

            case LCK -> this.lck = value;

            case TURNS -> this.turns = value;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
}
