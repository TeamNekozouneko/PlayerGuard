package net.nekozouneko.playerguard.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitTaskScheduler extends TaskScheduler {

    public BukkitTaskScheduler(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void runTaskTimer(PluginTask task, long delayTicks, long periodTicks) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskTimer(
                plugin, task, delayTicks, periodTicks
        );

        task.setCancelProvider(new BukkitCancelProvider(bukkitTask));
    }

    @Override
    public void runTaskLater(PluginTask task, long delayTicks) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskLater(
                plugin, task, delayTicks
        );

        task.setCancelProvider(new BukkitCancelProvider(bukkitTask));
    }

    @Override
    public void run(PluginTask task) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTask(
                plugin, task
        );

        task.setCancelProvider(new BukkitCancelProvider(bukkitTask));
    }

    @Override
    public void runTaskTimer(Location location, PluginTask task, long delayTicks, long periodTicks) {
        runTaskTimer(task, delayTicks, periodTicks);
    }

    @Override
    public void runTaskLater(Location location, PluginTask task, long delayTicks) {
        runTaskLater(task, delayTicks);
    }

    @Override
    public void run(Location location, PluginTask task) {
        run(task);
    }

    @Override
    public void runTaskTimer(Entity entity, PluginTask task, long delayTicks, long periodTicks) {
        runTaskTimer(task, delayTicks, periodTicks);
    }

    @Override
    public void runTaskLater(Entity entity, PluginTask task, long delayTicks) {
        runTaskLater(task, delayTicks);
    }

    @Override
    public void run(Entity entity, PluginTask task) {
        run(task);
    }
}
