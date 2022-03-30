package com.gmail.necnionch.myplugin.slponlinestatus.bungee;

import com.gmail.necnionch.myplugin.slponlinestatus.bungee.config.MainConfig;
import com.gmail.necnionch.myplugin.slponlinestatus.bungee.status.ServerStatusChecker;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.minecrell.serverlistplus.core.ServerListPlusCore;
import net.minecrell.serverlistplus.core.replacement.LiteralPlaceholder;
import net.minecrell.serverlistplus.core.replacement.ReplacementManager;

import java.util.Objects;
import java.util.Optional;

public final class SLPOnlineStatusPlugin extends Plugin implements Listener {
    private final MainConfig mainConfig = new MainConfig(this);
    private final LiteralPlaceholder slpPlaceholder = createSLPPlaceholder();
    private ServerStatusChecker checker;
    private ServerInfo targetServer;

    @Override
    public void onLoad() {
        ReplacementManager.getDynamic().add(slpPlaceholder);
    }

    @Override
    public void onEnable() {
        mainConfig.load();
        getProxy().getPluginManager().registerListener(this, this);
        startChecker();
    }

    @Override
    public void onDisable() {
        ReplacementManager.getDynamic().remove(slpPlaceholder);
        stopChecker();
    }


    private ServerInfo findFirstServer() {
        for (ListenerInfo listener : getProxy().getConfig().getListeners()) {
            Optional<ServerInfo> first = listener.getServerPriority().stream()
                    .map(name -> getProxy().getServerInfo(name))
                    .filter(Objects::nonNull)
                    .findFirst();
            if (first.isPresent())
                return first.get();
        }
        return null;
    }

    private LiteralPlaceholder createSLPPlaceholder() {
        return new LiteralPlaceholder("%server_status%") {
            @Override
            public String replace(ServerListPlusCore core, String s) {
                String value;
                if (targetServer == null) {
                    value = mainConfig.getStatusName(MainConfig.Status.UNKNOWN);
                } else if (checker.isOnline()) {
                    value = mainConfig.getStatusName(MainConfig.Status.ONLINE);
                } else {
                    value = mainConfig.getStatusName(MainConfig.Status.OFFLINE);
                }

                return this.replace(s, value);
            }
        };
    }


    // methods

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public ServerInfo getTargetServer() {
        return targetServer;
    }

    public boolean isOnline() {
        return checker != null && checker.isOnline();
    }

    public boolean startChecker() {
        ServerInfo firstServer = findFirstServer();
        if (firstServer == null) {
            getLogger().warning("Failed to found to default server. (listeners.priorities in config.yml by BungeeCord)");
            return false;
        }

        stopChecker();
        getLogger().info("Starting server checker: " + firstServer.getName() + " server");

        targetServer = firstServer;
        checker = new ServerStatusChecker(firstServer.getSocketAddress(), 10 * 1000);
        checker.start(this);
        return true;
    }

    public void stopChecker() {
        if (checker != null) {
            getLogger().info("Stopping checker");
            checker.stop();
        }
        targetServer = null;
    }


    // listeners

    @EventHandler
    public void onReload(ProxyReloadEvent event) {
        mainConfig.load();
        startChecker();
    }

}
