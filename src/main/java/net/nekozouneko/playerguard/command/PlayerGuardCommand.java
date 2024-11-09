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
import net.nekozouneko.playerguard.PGUtil;
import net.nekozouneko.playerguard.PlayerGuard;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

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
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"このコマンドはプレイヤーからのみ実行できます。");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"追加するプレイヤーを指定してください。");
                    return true;
                }

                addMemberCommand((Player) sender, args[1], args.length > 2 ? args[2] : null);
                break;
            }
            case "remove": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"このコマンドはプレイヤーからのみ実行できます。");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"削除するプレイヤーを指定してください。");
                    return true;
                }

                removeMemberCommand((Player) sender, args[1], args.length > 2 ? args[2] : null);
                break;
            }
            case "transfer": {
                break;
            }
            case "info": {
                regionInfoCommand(sender, args.length > 1 ? args[1] : null);
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
                                    .filter(pr -> StateFlag.test(pr.getFlag(PlayerGuard.getGuardRegisteredFlag())))
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
                                    .filter(pr -> StateFlag.test(pr.getFlag(PlayerGuard.getGuardRegisteredFlag())))
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

        Map<ProtectedRegion, World> protects = PGUtil.getPlayerRegions(player);

        player.sendMessage(String.format(ChatColor.GOLD+"■ "+ChatColor.YELLOW+"保護している領域 (%d)", protects.size()));

        protects.forEach((pr, w) ->
            player.sendMessage(String.format(ChatColor.WHITE+"* %s/%s "+ChatColor.GRAY+"(%d, %d %d) -> (%d, %d, %d)",
                    pr.getId(), w.getName(),
                    pr.getMinimumPoint().x(), pr.getMinimumPoint().y(), pr.getMinimumPoint().z(),
                    pr.getMaximumPoint().x(), pr.getMaximumPoint().y(), pr.getMaximumPoint().z()
            ))
        );

    }

    private void regionInfoCommand(CommandSender sender, String regionId) {
        ProtectedRegion region;
        World world;

        if (regionId == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"引数を入力してください。");
                return;
            }

            region = PGUtil.getCurrentPositionRegion((Player) sender);
            world = ((Player) sender).getWorld();
        }
        else {
            Map.Entry<ProtectedRegion, World> result = PGUtil.findPlayerGuardRegions(regionId);
            if (result == null) {
                region = null;
                world = null;
            }
            else {
                region = result.getKey();
                world = result.getValue();
            }
        }

        if (region == null) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当する保護領域がありません。");
            return;
        }

        sender.sendMessage(String.format(ChatColor.GOLD+"■ "+ChatColor.YELLOW+"%s", region.getId()));
        sender.sendMessage(String.format("(%d, %d, %d) -> (%d, %d, %d) | %s",
                region.getMinimumPoint().x(), region.getMinimumPoint().y(), region.getMinimumPoint().z(),
                region.getMaximumPoint().x(), region.getMaximumPoint().y(), region.getMaximumPoint().z(),
                world.getName()
        ));
        sender.sendMessage(String.format("所有者: %s", region.getOwners().getUniqueIds().stream()
                .map(Bukkit::getOfflinePlayer)
                .map(OfflinePlayer::getName)
                .collect(Collectors.joining(", ")))
        );
        String members = region.getMembers().getUniqueIds().stream()
                .map(Bukkit::getOfflinePlayer)
                .map(OfflinePlayer::getName)
                .collect(Collectors.joining(", "));
        sender.sendMessage("メンバー: %s", members.isEmpty() ? "-" : members);
    }

    private void addMemberCommand(Player sender, String p, String regionId) {
        Player player = Bukkit.getPlayer(p);

        ProtectedRegion region;

        if (regionId == null) {
            region = PGUtil.getCurrentPositionRegion(sender);
        }
        else {
            Map.Entry<ProtectedRegion, World> result = PGUtil.findPlayerGuardRegions(regionId);
            if (result == null) {
                region = null;
            }
            else {
                region = result.getKey();
            }
        }

        if (region == null || !region.getOwners().contains(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当する保護領域がありません。");
            return;
        }

        if (player == null) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当するプレイヤーはオンラインでないか、存在しません。");
            return;
        }

        if (region.getMembers().contains(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当するプレイヤーはすでに追加されています。");
            return;
        }

        region.getMembers().addPlayer(player.getUniqueId());

        sender.sendMessage(String.format(ChatColor.DARK_GREEN+"■ "+ChatColor.GREEN+"%sを追加しました。", player.getName()));
    }

    private void transferCommand(Player sender, String target, String regionId) {
        Player player = Bukkit.getPlayer(target);

        ProtectedRegion region;

        if (regionId == null) {
            region = PGUtil.getCurrentPositionRegion(sender);
        }
        else {
            Map.Entry<ProtectedRegion, World> result = PGUtil.findPlayerGuardRegions(regionId);
            if (result == null) {
                region = null;
            }
            else {
                region = result.getKey();
            }
        }

        if (region == null || !region.getOwners().contains(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当する保護領域がありません。");
            return;
        }

        if (player == null) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当するプレイヤーはオンラインでないか、存在しません。");
            return;
        }

        if (region.getMembers().contains(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当するプレイヤーはすでに追加されています。");
            return;
        }

        region.getMembers().clear();
        region.getOwners().clear();
        region.getOwners().addPlayer(player.getUniqueId());

        sender.sendMessage(String.format(ChatColor.DARK_GREEN+"■ "+ChatColor.GREEN+"%sを%sに移管しました。", region.getId(), player.getName()));
    }

    private void removeMemberCommand(Player sender, String p, String regionId) {
        Player player = Bukkit.getPlayer(p);

        ProtectedRegion region;

        if (regionId == null) {
            region = PGUtil.getCurrentPositionRegion(sender);
        }
        else {
            Map.Entry<ProtectedRegion, World> result = PGUtil.findPlayerGuardRegions(regionId);
            if (result == null) {
                region = null;
            }
            else {
                region = result.getKey();
            }
        }

        if (region == null || !region.getOwners().contains(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当する保護領域がありません。");
            return;
        }

        if (player == null) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当するプレイヤーはオンラインでないか、存在しません。");
            return;
        }

        if (!region.getMembers().contains(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_RED+"■ "+ChatColor.RED+"該当するプレイヤーはすでに削除されているか、追加していません。");
            return;
        }

        region.getMembers().removePlayer(player.getUniqueId());

        sender.sendMessage(String.format(ChatColor.DARK_GREEN+"■ "+ChatColor.GREEN+"%sを削除しました。", player.getName()));
    }
}
