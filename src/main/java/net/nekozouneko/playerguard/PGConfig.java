package net.nekozouneko.playerguard;

import net.nekozouneko.playerguard.flag.GuardFlags;
import org.bukkit.configuration.Configuration;

import java.util.HashSet;
import java.util.Set;

public class PGConfig {

    private static Configuration config;

    public static void setConfig(Configuration config) {
        PGConfig.config = config;
    }

    public static long getLimit(int day) {
        Set<String> keys = config.getConfigurationSection("protection.limit").getKeys(false);

        int nearest = 0;
        for (String key : keys) {
            try {
                int parsed = Integer.parseInt(key);

                if (parsed == day) {
                    nearest = day;
                    break;
                }

                if (parsed > day || Math.abs(day - parsed) > Math.abs(day - nearest)) continue;

                nearest = parsed;
            }
            catch (NumberFormatException nfe) { continue; }
        }

        return config.getLong("protection.limit." + nearest, 0);
    }

    public static boolean isFlagDisabled(GuardFlags flag) {
        return !config.getBoolean("protection.flags." + flag.getConfigId());
    }

    public static long getMinSpacingBetweenRegions() {
        return config.getLong("protection.spacing.min-spacing-between-regions");
    }

    public static boolean doApplyToSamePlayerSRegion() {
        return config.getBoolean("protection.spacing.apply-to-same-player-s-region");
    }

}
