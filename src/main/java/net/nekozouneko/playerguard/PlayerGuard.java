package net.nekozouneko.playerguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.papermc.paper.ServerBuildInfo;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.nekozouneko.playerguard.command.*;
import net.nekozouneko.playerguard.command.sub.playerguard.ConfirmCommand;
import net.nekozouneko.playerguard.flag.GuardIgnoredFlag;
import net.nekozouneko.playerguard.flag.GuardRegisteredFlag;
import net.nekozouneko.playerguard.listener.PlayerChangedWorldListener;
import net.nekozouneko.playerguard.listener.PlayerInteractListener;
import net.nekozouneko.playerguard.scheduler.BukkitTaskScheduler;
import net.nekozouneko.playerguard.scheduler.FoliaTaskScheduler;
import net.nekozouneko.playerguard.scheduler.PluginTask;
import net.nekozouneko.playerguard.scheduler.TaskScheduler;
import net.nekozouneko.playerguard.selection.SelectionStorage;
import net.nekozouneko.playerguard.task.ActionbarTask;
import net.nekozouneko.playerguard.task.SelectionRenderTask;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerGuard extends JavaPlugin {

    @Getter
    private static PlayerGuard instance;
    @Getter
    private static StateFlag guardRegisteredFlag;
    @Getter
    private static StateFlag guardIgnoredFlag;
    @Getter
    private static TaskScheduler taskScheduler;

    private static final int PROTECTION_LIMIT_BASE_VALUE = 30000;

    @Getter
    private SelectionStorage selectionStorage;
    private ActionbarTask regionActionbarTask;
    private SelectionRenderTask selectionRenderTask;

    private static Boolean isFolia = null;

    public static boolean isFolia() {
        if (isFolia != null) return isFolia;

        boolean result = PGUtil.classExists("io.papermc.paper.threadedregions.RegionizedServer");

        if (PGUtil.classExists("io.papermc.paper.ServerBuildInfo")) {
            result = result || ServerBuildInfo.buildInfo().isBrandCompatible(Key.key("papermc", "folia"));
        }

        isFolia = result;

        return result;
    }

    @Override
    public void onLoad() {
        getLogger().info("Registering worldguard flag...");
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            guardRegisteredFlag = new GuardRegisteredFlag();
            registry.register(guardRegisteredFlag);
        }
        catch (FlagConflictException fce) {
            Flag<?> alreadyRegistered = registry.get("pguard-registered");
            if (alreadyRegistered instanceof GuardRegisteredFlag) {
                guardRegisteredFlag = (GuardRegisteredFlag) alreadyRegistered;
            }
            else throw fce;
        }

        try {
            guardIgnoredFlag = new GuardIgnoredFlag();
            registry.register(guardIgnoredFlag);
        }
        catch (FlagConflictException fce) {
            Flag<?> alreadyRegistered = registry.get("pguard-ignored");
            if (alreadyRegistered instanceof GuardIgnoredFlag) {
                guardIgnoredFlag = (GuardIgnoredFlag) alreadyRegistered;
            }
            else throw fce;
        }
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        PGConfig.setConfig(getConfig());

        selectionStorage = new SelectionStorage();

        getServer().getPluginManager().registerEvents(new PlayerChangedWorldListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);

        taskScheduler = isFolia() ? new FoliaTaskScheduler(this) : new BukkitTaskScheduler(this);

        regionActionbarTask = new ActionbarTask();
        taskScheduler.runTaskTimer(regionActionbarTask, 1, 20);
        selectionRenderTask = new SelectionRenderTask();
        taskScheduler.runTaskTimer(selectionRenderTask, 1, 10);

        getCommand("cancel-claim").setExecutor(new CancelCommand());
        getCommand("claim").setExecutor(new ClaimCommand());
        getCommand("disclaim").setExecutor(new DisclaimCommand());
        getCommand("flags").setExecutor(new FlagsCommand());
        getCommand("playerguard").setExecutor(new PlayerGuardCommand());
        getCommand("playerguard-admin").setExecutor(new PlayerGuardAdminCommand());
    }

    @Override
    public void onDisable() {
        instance = null;

        safetyTaskCancel(regionActionbarTask);
        regionActionbarTask = null;
        safetyTaskCancel(selectionRenderTask);
        selectionRenderTask = null;

        ConfirmCommand.clearConfirms();
    }

    public void reload() {
        reloadConfig();
        PGConfig.setConfig(getConfig());
    }

    public long getProtectLimit(Player player) {
        int days = (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60 / 60 / 24;
        long limit = PGConfig.getLimit(days);

        NamespacedKey key = new NamespacedKey(this, "limit-extends");
        Long extend = player.getPersistentDataContainer().get(key, PersistentDataType.LONG);

        if (extend != null && limit != -1) {
            return limit + extend;
        }

        return limit;
    }

    public long getProtectionUsed(Player player) {
        long used = 0;

        for (ProtectedRegion region : PGUtil.getPlayerRegions(player).keySet())
            used += region.volume();

        return used;
    }

    public void resetAllRegions() {
        RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        rc.getLoaded().forEach(rm ->
                rm.getRegions().values().stream()
                        .filter(pr -> !(pr instanceof GlobalProtectedRegion))
                        .filter(pr -> StateFlag.test(pr.getFlag(PlayerGuard.getGuardRegisteredFlag())))
                        .forEach(pr -> rm.removeRegion(pr.getId()))
        );
    }

    private void safetyTaskCancel(PluginTask task) {
        if (task == null || task.isCancelled()) return;

        task.cancel();
    }
}
