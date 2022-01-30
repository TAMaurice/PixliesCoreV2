package net.pixlies.proxy.listeners;

import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.plugin.Listener;
import net.pixlies.proxy.Proxy;

public class ListenerManager {

    private final ImmutableList<Listener> listeners = ImmutableList.of(
            // LISTENERS
    );

    public void registerListeners() {
        listeners.forEach(listener -> Proxy.getInstance().getProxy().getPluginManager().registerListener(Proxy.getInstance(), listener));
    }

}
