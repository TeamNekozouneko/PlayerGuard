package net.nekozouneko.playerguard.scheduler;

public abstract class PluginTask implements Runnable {

    private CancelProvider cancelProvider = null;

    void setCancelProvider(CancelProvider provider) {
        if (cancelProvider != null)
            throw new RuntimeException(new IllegalAccessException("Cancel provider is already set"));

        this.cancelProvider = provider;
    }

    public boolean isCancelled() {
        return cancelProvider.isCancelled();
    }

    public void cancel() {
        cancelProvider.cancel();
    }

}
