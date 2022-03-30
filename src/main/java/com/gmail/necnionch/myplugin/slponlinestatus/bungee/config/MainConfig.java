package com.gmail.necnionch.myplugin.slponlinestatus.bungee.config;

import com.gmail.necnionch.myplugin.slponlinestatus.common.BungeeConfigDriver;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.Locale;
import java.util.Map;

public class MainConfig extends BungeeConfigDriver {
    private final Map<Status, String> statusNames = Maps.newHashMap();
    private final Plugin plugin;

    public MainConfig(Plugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onLoaded(Configuration config) {
        if (super.onLoaded(config)) {
            statusNames.clear();

            Configuration section = config.getSection("status-names");
            if (section != null) {
                for (String statusName : section.getKeys()) {
                    String display = section.getString(statusName);
                    display = (display == null) ? "" : ChatColor.translateAlternateColorCodes('&', display);

                    try {
                        Status status = Status.valueOf(statusName.toUpperCase(Locale.ROOT));
                        statusNames.put(status, display);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Unknown status: " + statusName + " (ignored it!)");
                    }
                }
            }

            return true;
        }
        return false;
    }

    public String getStatusName(Status status) {
        return statusNames.getOrDefault(status, "");
    }


    public enum Status {
        ONLINE, OFFLINE, UNKNOWN
    }
}
