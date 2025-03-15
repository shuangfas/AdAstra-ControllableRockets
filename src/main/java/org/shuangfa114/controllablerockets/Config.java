package org.shuangfa114.controllablerockets;

import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;

@com.teamresourceful.resourcefulconfig.common.annotations.Config(ControllableRockets.MODID)
public final class Config {
    @ConfigEntry(
            id = "launchTick",
            type = EntryType.INTEGER,
            translation = "config.controllable_rockets.launchTick"
    )
    public static int launchTick = 200;
    @ConfigEntry(
            id = "maxRocketSpeed",
            type = EntryType.FLOAT,
            translation = "config.controllable_rockets.maxSpeed"
    )
    public static float maxRocketSpeed = 5F;
    @ConfigEntry(
            id = "maxRocketAccelerationTick",
            type = EntryType.INTEGER,
            translation = "config.controllable_rockets.maxRocketAccelerationTick"
    )
    @Comment(value = "The acceleration tick of a rocket")
    public static int maxRocketAccelerationTick = 400;
    @ConfigEntry(
            id = "reverseXRotControl",
            type = EntryType.BOOLEAN,
            translation = "config.controllable_rockets.reverseXRotControl"
    )
    public static boolean reverseXRotControl = false;

    public Config() {

    }
}
