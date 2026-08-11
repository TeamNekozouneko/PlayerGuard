package net.nekozouneko.playerguard.scheduler;

import lombok.AllArgsConstructor;
import org.bukkit.scheduler.BukkitTask;

@AllArgsConstructor
public class BukkitCancelProvider implements CancelProvider {

    private final BukkitTask task;

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }
}
