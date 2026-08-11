package net.nekozouneko.playerguard.scheduler;

import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
public abstract class TaskScheduler {

    protected final Plugin plugin;

    public abstract void runTaskTimer(PluginTask task, long delayTicks, long periodTicks);

    public abstract void runTaskLater(PluginTask task, long delayTicks);

    public abstract void run(PluginTask task);

    public abstract void runTaskTimer(Location location, PluginTask task, long delayTicks, long periodTicks);

    public abstract void runTaskLater(Location location, PluginTask task, long delayTicks);

    public abstract void run(Location location, PluginTask task);

    public abstract void runTaskTimer(Entity entity, PluginTask task, long delayTicks, long periodTicks);

    public abstract void runTaskLater(Entity entity, PluginTask task, long delayTicks);

    public abstract void run(Entity entity, PluginTask task);

}
