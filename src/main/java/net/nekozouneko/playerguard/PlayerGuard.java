package net.nekozouneko.playerguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import net.nekozouneko.playerguard.command.*;
import net.nekozouneko.playerguard.command.sub.playerguard.ConfirmCommand;
import net.nekozouneko.playerguard.flag.GuardRegisteredFlag;
import net.nekozouneko.playerguard.listener.PlayerChangedWorldListener;
import net.nekozouneko.playerguard.listener.PlayerInteractListener;
import net.nekozouneko.playerguard.selection.SelectionStorage;
import net.nekozouneko.playerguard.task.ActionbarTask;
import net.nekozouneko.playerguard.task.SelectionRenderTask;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class PlayerGuard extends JavaPlugin {

    @Getter
    private static PlayerGuard instance;
    @Getter
    private static StateFlag guardRegisteredFlag;
    private static final int PROTECTION_LIMIT_BASE_VALUE = 30000;

    @Getter
    private SelectionStorage selectionStorage;
    private ActionbarTask regionActionbarTask;
    private SelectionRenderTask selectionRenderTask;

    @Override
    public void onLoad() {
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

    }

    @Override
    public void onEnable() {
        instance = this;

        selectionStorage = new SelectionStorage();

        getServer().getPluginManager().registerEvents(new PlayerChangedWorldListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);

        regionActionbarTask = new ActionbarTask();
        regionActionbarTask.runTaskTimer(this, 0, 20);
        selectionRenderTask = new SelectionRenderTask();
        selectionRenderTask.runTaskTimer(this, 0, 10);

        getCommand("cancel-claim").setExecutor(new CancelCommand());
        getCommand("claim").setExecutor(new ClaimCommand());
        getCommand("disclaim").setExecutor(new DisclaimCommand());
        getCommand("flags").setExecutor(new FlagsCommand());
        getCommand("playerguard").setExecutor(new PlayerGuardCommand());
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

    public long getProtectLimit(Player player) {
        int days = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60 / 60 / 24;
        long limit = (long) Math.max((PROTECTION_LIMIT_BASE_VALUE * Math.cbrt(Math.max(days, 10))), 64000);

        NamespacedKey key = new NamespacedKey(this, "limit-extends");
        Long extend = player.getPersistentDataContainer().get(key, PersistentDataType.LONG);

        if (extend != null) {
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

    private void safetyTaskCancel(BukkitRunnable task) {
        if (task == null || task.isCancelled()) return;

        task.cancel();
    }
}
