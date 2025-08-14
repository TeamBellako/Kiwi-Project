package com.kiwi.features.personality;

import java.util.List;

public class AppsDTO {
    private List<String> goodApps;
    private List<String> badApps;

    public AppsDTO(List<String> goodApps, List<String> badApps) {
        this.goodApps = goodApps;
        this.badApps = badApps;
    }

    public List<String> getGoodApps() { return goodApps; }
    public void setGoodApps(List<String> goodApps) { this.goodApps = goodApps; }

    public List<String> getBadApps() { return badApps; }
    public void setBadApps(List<String> badApps) { this.badApps = badApps; }

}
