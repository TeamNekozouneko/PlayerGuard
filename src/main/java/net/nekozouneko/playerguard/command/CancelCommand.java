package net.nekozouneko.playerguard.command;

import net.md_5.bungee.api.ChatColor;
import net.nekozouneko.playerguard.PlayerGuard;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CancelCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"このコマンドはプレイヤーからのみ実行できます。");
            return true;
        }

        PlayerGuard.getInstance().getSelectionStorage().clear(((Player) sender).getUniqueId());
        sender.sendMessage(ChatColor.DARK_GREEN+"■ "+ChatColor.GREEN+"選択を解除しました。");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command label, String s, String[] args) {
        return Collections.emptyList();
    }
}
