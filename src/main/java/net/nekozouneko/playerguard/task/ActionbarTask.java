package net.nekozouneko.playerguard.task;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.nekozouneko.playerguard.PGUtil;
import net.nekozouneko.playerguard.PlayerGuard;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

public class ActionbarTask extends BukkitRunnable {

    private final WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform();

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (PlayerGuard.getInstance().getSelectionStorage().getSelection(p.getUniqueId()) != null)
                showSelectionUsage(p);
            else showInfo(p);
        });
    }

    private void showInfo(Player p) {
        ProtectedRegion region = PGUtil.getCurrentPositionRegion(p);

        if (region == null) {
            ItemStack mainHand = p.getInventory().getItemInMainHand();
            if (mainHand == null || mainHand.getType() != Material.GOLDEN_AXE) return;

            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                    ChatColor.YELLOW+"◆ "+ChatColor.AQUA+"金の斧を右クリックして1点目を選択します。"+ChatColor.YELLOW+" ◆"
            ));

            return;
        }

        String id = region.getId();
        String owner = region.getOwners().getUniqueIds().stream()
                .map(Bukkit::getOfflinePlayer)
                .map(OfflinePlayer::getName)
                .collect(Collectors.joining(", "));

        p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(String.format(ChatColor.YELLOW+"◆ "+ChatColor.AQUA+"%s (%s) "+ChatColor.YELLOW+"◆", id, owner))
        );
    }

    private void showSelectionUsage(Player p) {
        CuboidRegion select = PlayerGuard.getInstance().getSelectionStorage().getSelection(p.getUniqueId());

        String message;
        if (select.getPos1().equals(select.getPos2())) {
            message = ChatColor.YELLOW + "◆ "+ChatColor.AQUA+"2点目を指定または、/cancelで選択を解除します。 "+ChatColor.YELLOW+"◆";
        }
        else {
            message = ChatColor.YELLOW + "◆ "+ChatColor.AQUA+"/claimで保護、/cancelで選択を解除します。 "+ChatColor.YELLOW+"◆";
        }

        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

}
