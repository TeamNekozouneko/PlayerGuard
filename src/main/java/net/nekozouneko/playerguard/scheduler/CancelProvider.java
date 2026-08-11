package net.nekozouneko.playerguard.scheduler;

public interface CancelProvider {

    void cancel();

    boolean isCancelled();
}
