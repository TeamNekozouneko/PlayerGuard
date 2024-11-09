package net.nekozouneko.playerguard.listener;

import net.nekozouneko.playerguard.PlayerGuard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangedWorldListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onChangedWorld(PlayerChangedWorldEvent e) {
        PlayerGuard.getInstance().getSelectionStorage().clear(e.getPlayer().getUniqueId());
    }
}
