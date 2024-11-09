package net.nekozouneko.playerguard.command;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.md_5.bungee.api.ChatColor;
import net.nekozouneko.playerguard.PlayerGuard;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DisclaimCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"このコマンドはプレイヤーからのみ実行できます。");
            return true;
        }
        Player p = (Player) sender;
        RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();

        RegionManager rm = rc.get(BukkitAdapter.adapt(p.getWorld()));
        ApplicableRegionSet ars = rm.getApplicableRegions(BukkitAdapter.asBlockVector(p.getLocation()));
        Optional<ProtectedRegion> prOptional = ars.getRegions().stream()
                .filter(pr2 -> !(pr2 instanceof GlobalProtectedRegion))
                .filter(pr2 -> StateFlag.test(pr2.getFlag(PlayerGuard.getGUARD_REGISTERED_FLAG())))
                .findFirst();

        if (!prOptional.isPresent()) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"ここにはあなたが削除できる保護領域がありません。");
            return true;
        }

        ProtectedRegion pr = prOptional.get();

        if (!pr.getOwners().contains(p.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"ここにはあなたが削除できる保護領域がありません。");
            return true;
        }

        rm.removeRegion(pr.getId());

        sender.sendMessage(String.format(ChatColor.DARK_GREEN+"■ "+ChatColor.GREEN+"保護領域「%s」を削除しました。", pr.getId()));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
