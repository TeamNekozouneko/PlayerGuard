package net.nekozouneko.playerguard.gui;

import com.sk89q.worldguard.protection.flags.RegionGroup;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            inventory = Bukkit.createInventory(this, 9, "■ 権限を管理");
        inventory.clear();

        NamespacedKey key = new NamespacedKey(PlayerGuard.getInstance(), "flag");

        ItemStack breakFlag = ItemStackBuilder.of(Material.IRON_PICKAXE)
                .name(ChatColor.WHITE + "ブロックの破壊")
                .lore(ChatColor.GRAY + "状態："+stateToJapanese(GuardFlags.getState(region, GuardFlags.BREAK)))
                .persistentData(key, new EnumDataType<>(GuardFlags.class), GuardFlags.BREAK)
                .build();
        ItemStack placeFlag = ItemStackBuilder.of(Material.CRAFTING_TABLE)
                .name(ChatColor.WHITE + "ブロックの設置")
                .lore(ChatColor.GRAY + "状態："+stateToJapanese(GuardFlags.getState(region, GuardFlags.PLACE)))
                .persistentData(key, new EnumDataType<>(GuardFlags.class), GuardFlags.PLACE)
                .build();
        ItemStack interactFlag = ItemStackBuilder.of(Material.REDSTONE)
                .name(ChatColor.WHITE + "アイテムの使用、チェストを開く")
                .lore(ChatColor.GRAY + "状態："+stateToJapanese(GuardFlags.getState(region, GuardFlags.INTERACT)))
                .persistentData(key, new EnumDataType<>(GuardFlags.class), GuardFlags.INTERACT)
                .build();
        ItemStack pvpFlag = ItemStackBuilder.of(Material.IRON_SWORD)
                .name(ChatColor.WHITE + "PvP (プレイヤー同士のダメージ)")
                .lore(ChatColor.GRAY + "状態："+stateToJapanese(GuardFlags.getState(region, GuardFlags.PVP)))
                .persistentData(key, new EnumDataType<>(GuardFlags.class), GuardFlags.PVP)
                .build();
        ItemStack entityAttackFlag = ItemStackBuilder.of(Material.TRIDENT)
                .name(ChatColor.WHITE + "エンティティへのダメージ")
                .lore(ChatColor.GRAY + "状態："+stateToJapanese(GuardFlags.getState(region, GuardFlags.ENTITY_DAMAGE)))
                .persistentData(key, new EnumDataType<>(GuardFlags.class), GuardFlags.ENTITY_DAMAGE)
                .build();
        ItemStack regionEntryFlag = ItemStackBuilder.of(Material.BARRIER)
                .name(ChatColor.WHITE + "メンバー以外の侵入")
                .lore(ChatColor.GRAY + "状態："+stateToJapanese(GuardFlags.getState(region, GuardFlags.ENTRY)))
                .persistentData(key, new EnumDataType<>(GuardFlags.class), GuardFlags.ENTRY)
                .build();
        ItemStack pistonsFlag = ItemStackBuilder.of(Material.PISTON)
                .name(ChatColor.WHITE + "ピストンの使用")
                .lore(ChatColor.GRAY + "状態："+stateToJapanese(GuardFlags.getState(region, GuardFlags.PISTONS)))
                .persistentData(key, new EnumDataType<>(GuardFlags.class), GuardFlags.PISTONS)
                .build();

        inventory.setItem(1, breakFlag);
        inventory.setItem(2, placeFlag);
        inventory.setItem(3, interactFlag);
        inventory.setItem(4, pvpFlag);
        inventory.setItem(5, entityAttackFlag);
        inventory.setItem(6, pistonsFlag);
        inventory.setItem(7, regionEntryFlag);
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
            case ENTITY_DAMAGE:
            case ENTRY:
            case PISTONS: {
                GuardFlags.State state = GuardFlags.getState(region, flag);

                List<GuardFlags.State> states = Arrays.asList(GuardFlags.State.values());

                int index = states.indexOf(state) + 1;
                if (index >= states.size()) {
                    index = 0;
                }

                GuardFlags.State nextState = states.get(index);
                if (nextState == GuardFlags.State.SOME_CHANGED) {
                    nextState = GuardFlags.State.ALLOW;
                }

                if (nextState == GuardFlags.State.UNSET) {
                    for (StateFlag sff : flag.getFlags()) {
                        region.setFlag(sff, null);
                        region.setFlag(sff.getRegionGroupFlag(), RegionGroup.NONE);
                    }
                }
                else {
                    boolean b = state == GuardFlags.State.ALLOW;
                    for (StateFlag sff : flag.getFlags()) {
                        region.setFlag(sff, PGUtil.boolToState(!b));
                        region.setFlag(sff.getRegionGroupFlag(), flag == GuardFlags.PVP ? null : RegionGroup.NON_MEMBERS);
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

    private String stateToJapanese(GuardFlags.State state) {
        switch (state) {
            case ALLOW:
                return "許可";
            case DENY:
                return "拒否";
            case SOME_CHANGED:
                return "管理者により変更されています";
            case UNSET:
                return "設定解除";
        }

        return "不明";
    }
}
