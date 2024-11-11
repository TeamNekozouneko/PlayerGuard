package net.nekozouneko.playerguard.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.nekozouneko.playerguard.PlayerGuard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getItem() == null || e.getItem().getType() != Material.GOLDEN_AXE) return;

        e.setCancelled(true);

        CuboidRegion selection = PlayerGuard.getInstance().getSelectionStorage()
                .getSelection(e.getPlayer().getUniqueId());

        if (selection == null || !selection.getPos1().equals(selection.getPos2())) {
            selection = CuboidRegion.fromCenter(BukkitAdapter.asBlockVector(e.getClickedBlock().getLocation()), 0);
            e.getPlayer().sendMessage(String.format(ChatColor.DARK_AQUA+"■ "+ChatColor.AQUA+"%s を設定。", locationFormatted(selection.getPos1())));
        }
        else if (selection.getPos1().equals(selection.getPos2())) {
            selection = selection.clone();
            selection.setPos2(BukkitAdapter.asBlockVector(e.getClickedBlock().getLocation()));
            e.getPlayer().sendMessage(String.format(ChatColor.DARK_AQUA+"■ "+ChatColor.AQUA+"%s -> %s を設定。(x%d)",
                    locationFormatted(selection.getPos1()),
                    locationFormatted(selection.getPos2()),
                    selection.getVolume()
            ));
        }
        else return;

        PlayerGuard.getInstance().getSelectionStorage().putSelection(e.getPlayer().getUniqueId(), selection);
    }

    private String locationFormatted(BlockVector3 vector3) {
        return String.format("(%d, %d, %d)", vector3.x(), vector3.y(), vector3.z());
    }
}
