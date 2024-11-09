package net.nekozouneko.playerguard.gui;

import com.sk89q.worldguard.blacklist.action.DenyAction;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.nekozouneko.commons.spigot.inventory.ItemStackBuilder;
import net.nekozouneko.commons.spigot.persistence.EnumDataType;
import net.nekozouneko.playerguard.PGUtil;
import net.nekozouneko.playerguard.PlayerGuard;
import net.nekozouneko.playerguard.flag.GuardFlags;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

public class MenuGUI extends AbstractGUI{

    private final ProtectedRegion region;

    public MenuGUI(PlayerGuard instance, Player player, ProtectedRegion region) {
        super(player);

        this.region = region;

        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @Override
    public void init() {
        if (inventory == null)
            inventory = Bukkit.createInventory(this, 9, "フラグを管理");
        inventory.clear();

        NamespacedKey key = new NamespacedKey(PlayerGuard.getInstance(), "flag");

        ItemStack breakFlag = ItemStackBuilder.of(Material.IRON_PICKAXE)
                .name(ChatColor.WHITE + "ブロックの破壊")
                .lore(ChatColor.GRAY + "状態："+GuardFlags.getState(region, GuardFlags.BREAK).name())
                .persistentData(key, new EnumDataType<>(GuardFlags.class), GuardFlags.BREAK)
                .build();
        ItemStack placeFlag = ItemStackBuilder.of(Material.CRAFTING_TABLE)
                .name(ChatColor.WHITE + "ブロックの設置")
                .lore(ChatColor.GRAY + "状態："+GuardFlags.getState(region, GuardFlags.PLACE).name())
                .persistentData(key, new EnumDataType<>(GuardFlags.class), GuardFlags.PLACE)
                .build();
        ItemStack interactFlag = ItemStackBuilder.of(Material.REDSTONE)
                .name(ChatColor.WHITE + "インタラクト、チェストを開く")
                .lore(ChatColor.GRAY + "状態："+GuardFlags.getState(region, GuardFlags.INTERACT).name())
                .persistentData(key, new EnumDataType<>(GuardFlags.class), GuardFlags.INTERACT)
                .build();
        ItemStack pvpFlag = ItemStackBuilder.of(Material.IRON_SWORD)
                .name(ChatColor.WHITE + "PvP (プレイヤー同士のダメージ)")
                .lore(ChatColor.GRAY + "状態："+GuardFlags.getState(region, GuardFlags.PVP).name())
                .persistentData(key, new EnumDataType<>(GuardFlags.class), GuardFlags.PVP)
                .build();
        ItemStack entityAttackFlag = ItemStackBuilder.of(Material.TRIDENT)
                .name(ChatColor.WHITE + "エンティティへのダメージ")
                .lore(ChatColor.GRAY + "状態："+GuardFlags.getState(region, GuardFlags.ENTITY_DAMAGE).name())
                .persistentData(key, new EnumDataType<>(GuardFlags.class), GuardFlags.ENTITY_DAMAGE)
                .build();

        inventory.setItem(0, breakFlag);
        inventory.setItem(2, placeFlag);
        inventory.setItem(4, interactFlag);
        inventory.setItem(6, pvpFlag);
        inventory.setItem(8, entityAttackFlag);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType().isAir()) return;

        NamespacedKey key = new NamespacedKey(PlayerGuard.getInstance(), "flag");
        PersistentDataContainer c = e.getCurrentItem().getItemMeta().getPersistentDataContainer();

        if (c.get(key, new EnumDataType<>(GuardFlags.class)) == null)
            return;

        GuardFlags flag = c.get(key, new EnumDataType<>(GuardFlags.class));

        switch (flag) {
            case BREAK:
            case PLACE:
            case INTERACT:
            case PVP:
            case ENTITY_DAMAGE: {
                GuardFlags.State state = GuardFlags.getState(region, flag);

                if (state == GuardFlags.State.SOME_CHANGED) {
                    for (StateFlag sff : flag.getFlags()) {
                        region.setFlag(sff, PGUtil.boolToState(flag.getDefaultValue()));
                    }
                }
                else {
                    boolean b = state == GuardFlags.State.ALLOW;
                    for (StateFlag sff : flag.getFlags()) {
                        region.setFlag(sff, PGUtil.boolToState(!b));
                    }
                }

                getPlayer().playSound(getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 10, 2);

                break;
            }
        }

        init();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() != this) return;

        clearAllAGUIListeners(((Player) e.getPlayer()));
    }
}
