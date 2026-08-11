package net.nekozouneko.playerguard.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FoliaCancelProvider implements CancelProvider {

    private final ScheduledTask task;

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
    }
}
