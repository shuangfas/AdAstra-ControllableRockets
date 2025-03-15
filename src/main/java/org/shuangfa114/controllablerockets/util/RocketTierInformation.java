package org.shuangfa114.controllablerockets.util;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;

import java.util.HashMap;

public enum RocketTierInformation {
    TIER_1(0.65, 0.65),
    TIER_2(0.65, 0.65),
    TIER_3(0.65, 0.65),
    TIER_4(0, 1.35);

    public static final HashMap<Integer, RocketTierInformation> informationMap = new HashMap<>();

    static {
        informationMap.put(1, TIER_1);
        informationMap.put(2, TIER_2);
        informationMap.put(3, TIER_3);
        informationMap.put(4, TIER_4);
    }

    public final double renderOffset;
    public final double cameraOffset;

    RocketTierInformation(double renderOffset, double cameraOffset) {
        this.renderOffset = renderOffset;
        this.cameraOffset = cameraOffset;
    }

    public static RocketTierInformation getTierByEntity(Rocket rocket) {
        return informationMap.get(Rocket.ROCKET_TO_PROPERTIES.get(rocket.getType()).tier());
    }
}
