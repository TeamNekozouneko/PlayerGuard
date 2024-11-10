package net.nekozouneko.playerguard.command.sub.admin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatColor;
import net.nekozouneko.playerguard.PlayerGuard;
import net.nekozouneko.playerguard.command.sub.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LookupSectionCommand extends SubCommand {
    @Override
    public boolean execute(CommandSender sender, Command command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"このコマンドはプレイヤーからのみ実行できます。");
            return true;
        }

        Player p = (Player) sender;
        CuboidRegion select = PlayerGuard.getInstance().getSelectionStorage().getSelection(p.getUniqueId());

        if (select == null) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"金の斧で検索する範囲を指定してください。");
            return true;
        }

        RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(p.getWorld()));

        List<ProtectedRegion> result = rm.getApplicableRegions(new ProtectedCuboidRegion("__lookup", select.getPos1(), select.getPos2()))
                .getRegions().stream()
                .filter(pr -> !(pr instanceof GlobalProtectedRegion))
                .filter(pr -> StateFlag.test(pr.getFlag(PlayerGuard.getGuardRegisteredFlag())))
                .collect(Collectors.toList());

        sender.sendMessage(String.format(ChatColor.GOLD+"■ "+ChatColor.YELLOW+"結果 (%d)", result.size()));
        result.forEach(pr -> {
            sender.sendMessage(String.format("* %s (%s) / (%d, %d, %d) -> (%d, %d, %d)",
                    pr.getId(), pr.getOwners().getUniqueIds().stream()
                            .map(Bukkit::getOfflinePlayer)
                            .filter(Objects::nonNull)
                            .map(OfflinePlayer::getName)
                            .collect(Collectors.joining(", ")),
                    pr.getMinimumPoint().x(), pr.getMinimumPoint().y(), pr.getMinimumPoint().z(),
                    pr.getMaximumPoint().x(), pr.getMaximumPoint().y(), pr.getMaximumPoint().z()
            ));
            sender.sendMessage(ChatColor.GRAY + String.format("| メンバー: %s",
                    pr.getMembers().getUniqueIds().stream()
                            .map(Bukkit::getOfflinePlayer)
                            .filter(Objects::nonNull)
                            .map(OfflinePlayer::getName)
                            .collect(Collectors.joining(", "))
            ));
        });

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String label, List<String> args) {
        return Collections.emptyList();
    }

    @Override
    public String getPermission() {
        return "playerguard.command.admin.lookup";
    }
}
