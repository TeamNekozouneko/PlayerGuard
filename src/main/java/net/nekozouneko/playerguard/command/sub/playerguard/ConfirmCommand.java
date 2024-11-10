package net.nekozouneko.playerguard.command.sub.playerguard;

import net.md_5.bungee.api.ChatColor;
import net.nekozouneko.playerguard.command.sub.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ConfirmCommand extends SubCommand {

    private final static Map<UUID, Runnable> confirms = new HashMap<>();
    private final static Map<UUID, Long> timeouts = new HashMap<>();

    public static void addConfirm(UUID player, Runnable runnable) {
        confirms.put(player, runnable);
        timeouts.put(player, System.currentTimeMillis() + 60000);
    }

    public static void removeConfirm(UUID player) {
        confirms.remove(player);
        timeouts.remove(player);
    }

    public static Runnable getConfirm(UUID player) {
        Runnable task = confirms.get(player);
        Long timeout = timeouts.get(player);

        if (timeout == null || System.currentTimeMillis() > timeout) {
            removeConfirm(player);
            return null;
        }

        return task;
    }

    public static Map<UUID, Runnable> getConfirms() {
        new HashSet<>(confirms.keySet()).forEach(uuid -> {
            Long timeout = timeouts.get(uuid);
            if (timeout == null || System.currentTimeMillis() > timeout) {
                removeConfirm(uuid);
            }
        });

        return new HashMap<>(confirms);
    }

    public static void clearConfirms() {
        confirms.clear();
        timeouts.clear();
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"このコマンドはプレイヤーからのみ実行できます。");
            return true;
        }

        Player p = (Player) sender;
        Runnable confirm = getConfirm(p.getUniqueId());

        if (confirm == null) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"処理を行うためのデータがありませんでした。");
            return true;
        }

        confirm.run();
        removeConfirm(p.getUniqueId());
        sender.sendMessage(ChatColor.DARK_GREEN+"■ "+ChatColor.GREEN+"操作を続行しました。");

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String label, List<String> args) {
        return Collections.emptyList();
    }

    @Override
    public String getPermission() {
        return "playerguard.command.playerguard";
    }
}
