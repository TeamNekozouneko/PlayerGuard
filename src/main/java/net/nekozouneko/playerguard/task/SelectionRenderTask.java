package net.nekozouneko.playerguard.task;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.nekozouneko.playerguard.PlayerGuard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class SelectionRenderTask extends TaskManager {

    private Map<UUID, CuboidRegion> previous = new HashMap<>();
    private boolean flushState = true;

    @Override
    public void run() {
        Map<UUID, CuboidRegion> current = PlayerGuard.getInstance().getSelectionStorage().getSelections();

        if (current.isEmpty() && previous.isEmpty()) {
            previous = current;
            return;
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!(current.containsKey(player.getUniqueId()) || previous.containsKey(player.getUniqueId())))
                return;

            CuboidRegion now = current.get(player.getUniqueId());
            CuboidRegion prev = previous.get(player.getUniqueId());

            process(player, now, prev);
        });


        previous = PlayerGuard.getInstance().getSelectionStorage().getSelections();
        flushState = !flushState;
    }

    private void process(Player player, CuboidRegion now, CuboidRegion prev) {
        Preconditions.checkArgument(now != null || prev != null);
        if (now != null && prev != null) {
            if (!now.equals(prev)) {
                resetRender(player, prev);
                if (flushState) render(player, now);
                return;
            }

            if (flushState) render(player, now);
            else resetRender(player, prev);
        }

        if (now == null) {
            resetRender(player, prev);
            return;
        }

        if (flushState) render(player, now);
        else resetRender(player, now);
    }

    private void render(Player player, CuboidRegion region) {
        Set<Location> pos1Locations = new HashSet<>();
        Set<Location> pos2Locations = new HashSet<>();
        Location pos1 = BukkitAdapter.adapt(player.getWorld(), region.getPos1());
        Location pos2 = BukkitAdapter.adapt(player.getWorld(), region.getPos2());
        pos1Locations.add(pos1);
        if (!region.getPos1().equals(region.getPos2())) {
            pos2Locations.add(pos2);
        }

        // X
        if (pos1.getBlockX() != pos2.getBlockX()) {
            Location highXPos, lowXPos;
            boolean highIsPos1;
            if (pos1.getBlockX() > pos2.getBlockX()) {
                highXPos = pos1.clone();
                lowXPos = pos2.clone();
                highIsPos1 = true;
            }
            else {
                highXPos = pos2.clone();
                lowXPos = pos1.clone();
                highIsPos1 = false;
            }

            highXPos.setX(highXPos.getBlockX()-1);
            lowXPos.setX(lowXPos.getBlockX()+1);

            if (highIsPos1) {
                pos1Locations.add(highXPos);
                pos2Locations.add(lowXPos);
            }
            else {
                pos1Locations.add(lowXPos);
                pos2Locations.add(highXPos);
            }
        }

        // Y
        if (pos1.getBlockY() != pos2.getBlockY()) {
            Location highYPos, lowYPos;
            boolean highIsPos1;
            if (pos1.getBlockY() > pos2.getBlockY()) {
                highYPos = pos1.clone();
                lowYPos = pos2.clone();
                highIsPos1 = true;
            }
            else {
                highYPos = pos2.clone();
                lowYPos = pos1.clone();
                highIsPos1 = false;
            }

            highYPos.setY(highYPos.getBlockY()-1);
            lowYPos.setY(lowYPos.getBlockY()+1);

            if (highIsPos1) {
                pos1Locations.add(highYPos);
                pos2Locations.add(lowYPos);
            }
            else {
                pos1Locations.add(lowYPos);
                pos2Locations.add(highYPos);
            }
        }

        // Z
        if (pos1.getBlockZ() != pos2.getBlockZ()) {
            Location highZPos, lowZPos;
            boolean highIsPos1;
            if (pos1.getBlockZ() > pos2.getBlockZ()) {
                highZPos = pos1.clone();
                lowZPos = pos2.clone();
                highIsPos1 = true;
            }
            else {
                highZPos = pos2.clone();
                lowZPos = pos1.clone();
                highIsPos1 = false;
            }

            highZPos.setZ(highZPos.getBlockZ()-1);
            lowZPos.setZ(lowZPos.getBlockZ()+1);

            if (highIsPos1) {
                pos1Locations.add(highZPos);
                pos2Locations.add(lowZPos);
            }
            else {
                pos1Locations.add(lowZPos);
                pos2Locations.add(highZPos);
            }
        }

        pos1Locations.forEach(l -> player.sendBlockChange(l, Material.DIAMOND_BLOCK.createBlockData()));
        pos2Locations.forEach(l -> player.sendBlockChange(l, Material.GOLD_BLOCK.createBlockData()));
    }

    private void resetRender(Player player, CuboidRegion region) {
        Location pos1 = BukkitAdapter.adapt(player.getWorld(), region.getPos1());
        Location pos2 = BukkitAdapter.adapt(player.getWorld(), region.getPos2());

        resetBlockChange(player, pos1);
        if (!region.getPos1().equals(region.getPos2())) {
            resetBlockChange(player, pos2);
        }

        // X
        if (pos1.getBlockX() != pos2.getBlockX()) {
            Location highXPos, lowXPos;
            if (pos1.getBlockX() > pos2.getBlockX()) {
                highXPos = pos1.clone();
                lowXPos = pos2.clone();
            }
            else {
                highXPos = pos2.clone();
                lowXPos = pos1.clone();
            }

            highXPos.setX(highXPos.getBlockX()-1);
            lowXPos.setX(lowXPos.getBlockX()+1);

            resetBlockChange(player, highXPos);
            resetBlockChange(player, lowXPos);
        }

        // Y
        if (pos1.getBlockY() != pos2.getBlockY()) {
            Location highYPos, lowYPos;
            if (pos1.getBlockY() > pos2.getBlockY()) {
                highYPos = pos1.clone();
                lowYPos = pos2.clone();
            }
            else {
                highYPos = pos2.clone();
                lowYPos = pos1.clone();
            }

            highYPos.setY(highYPos.getBlockY()-1);
            lowYPos.setY(lowYPos.getBlockY()+1);

            resetBlockChange(player, highYPos);
            resetBlockChange(player, lowYPos);
        }

        // Z
        if (pos1.getBlockZ() != pos2.getBlockZ()) {
            Location highZPos, lowZPos;
            if (pos1.getBlockZ() > pos2.getBlockZ()) {
                highZPos = pos1.clone();
                lowZPos = pos2.clone();
            }
            else {
                highZPos = pos2.clone();
                lowZPos = pos1.clone();
            }

            highZPos.setZ(highZPos.getBlockZ()-1);
            lowZPos.setZ(lowZPos.getBlockZ()+1);

            resetBlockChange(player, highZPos);
            resetBlockChange(player, lowZPos);
        }
    }
    private void resetBlockChange(Player player, Location location){
        if(!PlayerGuard.isFolia){
            player.sendBlockChange(location, location.getBlock().getBlockData());
        }else{
            Runnable runnable = () -> player.sendBlockChange(location, location.getBlock().getBlockData());
            Bukkit.getRegionScheduler().execute(PlayerGuard.getInstance(), location, runnable);
        }
    }
}
