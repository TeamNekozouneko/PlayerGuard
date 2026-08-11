package net.nekozouneko.playerguard.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class FoliaTaskScheduler extends TaskScheduler {

    @RequiredArgsConstructor
    private static class FoliaPluginTaskAdapter {

        private final PluginTask task;
        private boolean isFirst = true;

        public void run(ScheduledTask scheduled) {
            if (isFirst) {
                task.setCancelProvider(new FoliaCancelProvider(scheduled));
                isFirst = false;
            }

            task.run();
        }
    }

    public FoliaTaskScheduler(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void runTaskTimer(PluginTask task, long delayTicks, long periodTicks) {
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
                plugin, new FoliaPluginTaskAdapter(task)::run, delayTicks, periodTicks
        );
    }

    @Override
    public void runTaskLater(PluginTask task, long delayTicks) {
        plugin.getServer().getGlobalRegionScheduler().runDelayed(
                plugin, new FoliaPluginTaskAdapter(task)::run, delayTicks
        );
    }

    @Override
    public void run(PluginTask task) {
        plugin.getServer().getGlobalRegionScheduler().run(
                plugin, new FoliaPluginTaskAdapter(task)::run
        );
    }

    @Override
    public void runTaskTimer(Location location, PluginTask task, long delayTicks, long periodTicks) {
        plugin.getServer().getRegionScheduler().runAtFixedRate(
                plugin, location, new FoliaPluginTaskAdapter(task)::run, delayTicks, periodTicks
        );
    }

    @Override
    public void runTaskLater(Location location, PluginTask task, long delayTicks) {
        plugin.getServer().getRegionScheduler().runDelayed(
                plugin, location, new FoliaPluginTaskAdapter(task)::run, delayTicks
        );
    }

    @Override
    public void run(Location location, PluginTask task) {
        plugin.getServer().getRegionScheduler().run(
                plugin, location, new FoliaPluginTaskAdapter(task)::run
        );
    }

    @Override
    public void runTaskTimer(Entity entity, PluginTask task, long delayTicks, long periodTicks) {
        entity.getScheduler().runAtFixedRate(
                plugin, new FoliaPluginTaskAdapter(task)::run, null, delayTicks, periodTicks
        );
    }

    @Override
    public void runTaskLater(Entity entity, PluginTask task, long delayTicks) {
        entity.getScheduler().runDelayed(
                plugin, new FoliaPluginTaskAdapter(task)::run, null, delayTicks
        );
    }

    @Override
    public void run(Entity entity, PluginTask task) {
        entity.getScheduler().run(
                plugin,  new FoliaPluginTaskAdapter(task)::run, null
        );
    }
}
