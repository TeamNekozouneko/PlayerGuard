package net.nekozouneko.playerguard.command;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.md_5.bungee.api.ChatColor;
import net.nekozouneko.commons.spigot.command.TabCompletes;
import net.nekozouneko.playerguard.PlayerGuard;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerGuardCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                return false;
            }

            myInfoCommand((Player) sender);
            return true;
        }

        switch (args[0]) {
            case "add": {
                break;
            }
            case "remove": {
                break;
            }
            case "transfer": {
                break;
            }
            case "info": {
                break;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return TabCompletes.sorted(args[0], "add", "remove", "transfer", "info");
        }
        else if (args.length == 2) {
            switch (args[0]) {
                case "add":
                case "remove":
                case "transfer": {
                    return TabCompletes.players(args[1], Bukkit.getOnlinePlayers());
                }
                case "info": {
                    RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    Set<String> regions = new HashSet<>();
                    rc.getLoaded().forEach(rm ->
                            rm.getRegions().values().stream()
                                    .filter(pr -> StateFlag.test(pr.getFlag(PlayerGuard.getGUARD_REGISTERED_FLAG())))
                                    .filter(pr -> !(pr instanceof GlobalProtectedRegion))
                                    .filter(pr -> {
                                        if (sender instanceof Player) {
                                            return pr.getOwners().contains(((Player) sender).getUniqueId());
                                        }
                                        else return true;
                                    })
                                    .map(ProtectedRegion::getId)
                                    .forEach(regions::add)
                    );

                    return TabCompletes.sorted(args[1], regions);
                }
            }
        }
        else if (args.length == 3) {
            switch (args[0]) {
                case "add":
                case "remove":
                case "transfer": {
                    RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    Set<String> regions = new HashSet<>();
                    rc.getLoaded().forEach(rm ->
                            rm.getRegions().values().stream()
                                    .filter(pr -> StateFlag.test(pr.getFlag(PlayerGuard.getGUARD_REGISTERED_FLAG())))
                                    .filter(pr -> !(pr instanceof GlobalProtectedRegion))
                                    .filter(pr -> {
                                        if (sender instanceof Player) {
                                            return pr.getOwners().contains(((Player) sender).getUniqueId());
                                        }
                                        else return true;
                                    })
                                    .map(ProtectedRegion::getId)
                                    .forEach(regions::add)
                    );

                    return TabCompletes.sorted(args[1], regions);
                }
            }
        }

        return Collections.emptyList();
    }

    private void myInfoCommand(Player player) {
        /*
        ■ あなたの情報 (Taitaitatata547)
        保護制限: u/l
        ■ 保護している領域 (16)
        * abcdef123/world (x, y, z) -> (x, y, z)
         */

        PlayerGuard pg = PlayerGuard.getInstance();

        player.sendMessage(String.format(ChatColor.GOLD+"■ "+ChatColor.YELLOW+"あなたの情報 (%s)", player.getName()));
        player.sendMessage(String.format(ChatColor.GRAY+"保護制限: "+ChatColor.WHITE+"%d/%d"
                , pg.getProtectionUsed(player), pg.getProtectLimit(player)));

        RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        Map<ProtectedRegion, World> protects = new HashMap<>();
        Bukkit.getWorlds().forEach(w -> {
            RegionManager rm = rc.get(BukkitAdapter.adapt(w));
            if (rm == null) return;

            rm.getRegions().values().stream()
                    .filter(pr -> StateFlag.test(pr.getFlag(PlayerGuard.getGUARD_REGISTERED_FLAG())))
                    .filter(pr -> !(pr instanceof GlobalProtectedRegion))
                    .filter(pr -> pr.getOwners().contains(player.getUniqueId()))
                    .forEach(pr -> protects.put(pr, w));
        });

        player.sendMessage(String.format(ChatColor.GOLD+"■ "+ChatColor.YELLOW+"保護している領域 (%d)", protects.size()));

        protects.forEach((pr, w) ->
            player.sendMessage(String.format(ChatColor.WHITE+"* %s/%s "+ChatColor.GRAY+"(%d, %d %d) -> (%d, %d, %d)",
                    pr.getId(), w.getName(),
                    pr.getMinimumPoint().x(), pr.getMinimumPoint().y(), pr.getMinimumPoint().z(),
                    pr.getMaximumPoint().x(), pr.getMaximumPoint().y(), pr.getMaximumPoint().z()
            ))
        );

    }
}
