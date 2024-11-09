package net.nekozouneko.playerguard.command.sub.playerguard;

import net.md_5.bungee.api.ChatColor;
import net.nekozouneko.playerguard.command.sub.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ConfirmCommand extends SubCommand {

    private static Map<UUID, Runnable> confirms = new HashMap<>();
    private static Map<UUID, Long> timeouts = new HashMap<>();

    public static void addConfirm(UUID player, Runnable runnable) {
        confirms.put(player, runnable);
    }

    public static void removeConfirm(UUID player) {
        confirms.remove(player);
    }

    public static Map<UUID, Runnable> getConfirms() {
        return new HashMap<>(confirms);
    }

    public static void clearConfirms() {
        confirms.clear();
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"このコマンドはプレイヤーからのみ実行できます。");
            return true;
        }

        Player p = (Player) sender;

        if (!confirms.containsKey(p.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"処理を行うためのデータがありませんでした。");
            return true;
        }

        confirms.remove(p.getUniqueId()).run();
        sender.sendMessage(ChatColor.DARK_GREEN+"■ "+ChatColor.GREEN+"操作を続行しました。");

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String label, List<String> args) {
        return Collections.emptyList();
    }
}
