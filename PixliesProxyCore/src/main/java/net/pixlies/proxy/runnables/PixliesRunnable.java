package net.pixlies.proxy.runnables;

import lombok.AllArgsConstructor;
import net.pixlies.proxy.Proxy;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public abstract class PixliesRunnable implements Runnable {

    private static final Proxy instance = Proxy.getInstance();

    private final long delay;
    private final long repeat;
    private final TimeUnit timeUnit;

    public void execute() {
        instance.getProxy().getScheduler().schedule(instance, this, delay, repeat, timeUnit);
    }

}
