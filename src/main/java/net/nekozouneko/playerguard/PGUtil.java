package net.nekozouneko.playerguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public final class PGUtil {
    private PGUtil() {}

    public static ProtectedRegion getCurrentPositionRegion(Player player) {
        RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(player.getWorld()));

        ApplicableRegionSet ars =
                rm.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));

        return ars.getRegions().stream()
                .filter(region -> !(region instanceof GlobalProtectedRegion))
                .filter(region -> StateFlag.test(region.getFlag(PlayerGuard.getGuardRegisteredFlag())))
                .findFirst().orElse(null);
    }

    public static Map<ProtectedRegion, World> getPlayerRegions(Player player) {
        Map<ProtectedRegion, World> result = new HashMap<>();

        RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        Bukkit.getWorlds().forEach(world -> {
            RegionManager rm = rc.get(BukkitAdapter.adapt(world));

            rm.getRegions().values().stream()
                    .filter(region -> !(region instanceof GlobalProtectedRegion))
                    .filter(region -> StateFlag.test(region.getFlag(PlayerGuard.getGuardRegisteredFlag())))
                    .filter(region -> region.getOwners().contains(player.getUniqueId()))
                    .forEach(region -> result.put(region, world));
        });

        return result;
    }

    public static Map.Entry<ProtectedRegion, World> findPlayerGuardRegions(String id) {
        ProtectedRegion region = null;
        World world = null;

        RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();

        for (World w : Bukkit.getWorlds()) {
            RegionManager rm = rc.get(BukkitAdapter.adapt(w));

            ProtectedRegion pr = rm.getRegion(id);
            if (pr == null) continue;

            boolean flagState = StateFlag.test(pr.getFlag(PlayerGuard.getGuardRegisteredFlag()));
            boolean notGlobal = !(pr instanceof GlobalProtectedRegion);

            if (flagState && notGlobal) {
                region = pr;
                world = w;
                break;
            }
        }

        if (region == null || world == null) return null;

        return new AbstractMap.SimpleEntry<>(region, world);
    }

    public static StateFlag.State boolToState(Boolean bool) {
        return bool == null ? null : (bool ? StateFlag.State.ALLOW : StateFlag.State.DENY);
    }
}
