package net.nekozouneko.playerguard.gui;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.RegisteredListener;

public abstract class AbstractGUI implements Listener, InventoryHolder {

    @Getter
    private final Player player;
    protected Inventory inventory;

    public AbstractGUI(Player player) {
        this.player = player;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public abstract void init();

    public void open() {
        init();
        player.openInventory(inventory);
    }

    public static void clearAllAGUIListeners(Player player) {
        HandlerList.getHandlerLists().forEach(hl -> {
            for (RegisteredListener rl : hl.getRegisteredListeners()) {
                if (rl.getListener() instanceof AbstractGUI && ((AbstractGUI) rl.getListener()).getPlayer().equals(player)) {
                    HandlerList.unregisterAll(rl.getListener());
                }
            }
        });
    }

}
