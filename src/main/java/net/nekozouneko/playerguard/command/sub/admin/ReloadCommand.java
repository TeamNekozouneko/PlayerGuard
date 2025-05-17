package net.nekozouneko.playerguard.command.sub.admin;

import net.nekozouneko.playerguard.PlayerGuard;
import net.nekozouneko.playerguard.command.sub.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class ReloadCommand extends SubCommand {
    @Override
    public boolean execute(CommandSender sender, Command command, String label, List<String> args) {
        PlayerGuard.getInstance().reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "コンフィグを再読み込みしました！");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String label, List<String> args) {
        return Collections.emptyList();
    }

    @Override
    public String getPermission() {
        return "playerguard.command.admin.reload";
    }
}
