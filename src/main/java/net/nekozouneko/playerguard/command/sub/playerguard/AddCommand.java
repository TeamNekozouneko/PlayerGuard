package net.nekozouneko.playerguard.command.sub.playerguard;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatColor;
import net.nekozouneko.playerguard.PGUtil;
import net.nekozouneko.playerguard.command.sub.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddCommand extends SubCommand {
    @Override
    public boolean execute(CommandSender sender, Command command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"このコマンドはプレイヤーからのみ実行できます。");
            return true;
        }

        Player player = (Player) sender;

        if (args.isEmpty()) {
            return true;
        }

        Player add = Bukkit.getPlayer(args.get(0));

        ProtectedRegion region;

        if (args.size() < 2) {
            region = PGUtil.getCurrentPositionRegion(player);
        }
        else {
            Map.Entry<ProtectedRegion, World> result = PGUtil.findPlayerGuardRegions(args.get(1));
            if (result == null) {
                region = null;
            }
            else {
                region = result.getKey();
            }
        }

        if (region == null || !region.getOwners().contains(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当する保護領域がありません。");
            return true;
        }

        if (add == null) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当するプレイヤーはオンラインでないか、存在しません。");
            return true;
        }

        if (region.getMembers().contains(add.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当するプレイヤーはすでに追加されています。");
            return true;
        }

        region.getMembers().addPlayer(add.getUniqueId());

        sender.sendMessage(String.format(ChatColor.DARK_GREEN+"■ "+ChatColor.GREEN+"%sを追加しました。", add.getName()));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String label, List<String> args) {
        return Collections.emptyList();
    }
}
