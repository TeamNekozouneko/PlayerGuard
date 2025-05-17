package net.nekozouneko.playerguard.task;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import net.nekozouneko.playerguard.PlayerGuard;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

import static java.lang.Math.max;

public abstract class TaskManager {
    public abstract void run();

    @Getter
    private BukkitTask bukkitTask;
    @Getter
    private ScheduledTask scheduledTask;

    public void runTaskTimer(Plugin plugin, long delay, long period){
        if(!PlayerGuard.isFolia){
            bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, this::run, delay, period);
        }else{
            Consumer<ScheduledTask> consumer = (x)-> run();
            scheduledTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, consumer, max(delay, 1), period);
        }
    }

    public void safetyTaskCancel(){
        if(bukkitTask != null && !bukkitTask.isCancelled()) bukkitTask.cancel();
        if(scheduledTask != null && !scheduledTask.isCancelled()) scheduledTask.cancel();
    }
}
