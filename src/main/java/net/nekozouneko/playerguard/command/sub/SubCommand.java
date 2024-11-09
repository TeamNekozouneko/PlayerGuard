package net.nekozouneko.playerguard.command.sub;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    public abstract boolean execute(CommandSender sender, Command command, String label, List<String> args);

    public abstract List<String> tabComplete(CommandSender sender, Command command, String label, List<String> args);
}
