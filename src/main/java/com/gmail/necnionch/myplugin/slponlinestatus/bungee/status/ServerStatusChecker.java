package com.gmail.necnionch.myplugin.slponlinestatus.bungee.status;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class ServerStatusChecker implements Runnable {
    private final SocketAddress address;
    private final int checkInterval;
    private boolean online;

    private ScheduledTask task;

    public ServerStatusChecker(SocketAddress address, int checkInterval) {
        this.address = address;
        this.checkInterval = checkInterval;
    }

    public boolean isOnline() {
        return online;
    }


    public void start(Plugin owner) {
        stop();
        task = owner.getProxy().getScheduler()
                .schedule(owner, this, 0, checkInterval, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void run() {
        boolean newStatus = isReachable();
        if (online != newStatus)
            onChange(newStatus);
        this.online = newStatus;
    }

    public void onChange(boolean online) {
    }


    private boolean isReachable() {
        try (Socket socket = new Socket()) {
            socket.connect(address, 2000);
            return true;

        } catch (IOException ignored) {
            return false;
        }
    }

}
