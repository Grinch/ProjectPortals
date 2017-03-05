package com.gmail.trentech.pjp.utils;

import com.gmail.trentech.pjp.Main;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {

    private Path path;
    private CommentedConfigurationNode config;
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    private static ConcurrentHashMap<String, ConfigManager> configManagers = new ConcurrentHashMap<>();

    private ConfigManager(String configName) {
        try {
            path = Main.instance().getPath().resolve(configName + ".conf");

            if (!Files.exists(path)) {
                Files.createFile(path);
                Main.instance().getLog().info("Creating new " + path.getFileName() + " file...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        load();
    }

    public static ConfigManager get(String configName) {
        return configManagers.get(configName);
    }

    public static ConfigManager get() {
        return configManagers.get("config");
    }

    public static ConfigManager init() {
        return init("config");
    }

    public static ConfigManager init(String configName) {
        ConfigManager configManager = new ConfigManager(configName);
        CommentedConfigurationNode config = configManager.getConfig();

        if (configName.equalsIgnoreCase("config")) {
            if (config.getNode("options", "portal", "size").isVirtual()) {
                config.getNode("options", "portal", "size").setValue(100).setComment("Maximum number of blocks a portal can use");
            }
            if (config.getNode("options", "portal", "legacy_builder").isVirtual()) {
                config.getNode("options", "portal", "legacy_builder").setValue(true).setComment("Use legacy portal builder");
            }
            if (config.getNode("options", "portal", "teleport_item").isVirtual()) {
                config.getNode("options", "portal", "teleport_item").setValue(true).setComment("Toggle if portals can teleport items");
            }
            if (config.getNode("options", "portal", "teleport_mob").isVirtual()) {
                config.getNode("options", "portal", "teleport_mob").setValue(true).setComment("Toggle if portals can teleport mobs");
            }
            if (config.getNode("options", "homes").isVirtual()) {
                config.getNode("options", "homes").setValue(5).setComment("Default number of homes a player can have");
            }
            if (config.getNode("options", "particles").isVirtual()) {
                config.getNode("options", "particles").setComment("Particle effect settings");
                config.getNode("options", "particles", "enable").setValue(true).setComment("Enable particle effects");
                config.getNode("options", "particles", "portal", "type").setValue("PORTAL2").setComment("Default particle type for portals");
                config.getNode("options", "particles", "portal", "color").setValue("NONE")
                        .setComment("Default Color of Particle if supported, otherwise set \"NONE\"");
                config.getNode("options", "particles", "teleport", "type").setValue("REDSTONE_DUST")
                        .setComment("Default particle type when teleporting");
                config.getNode("options", "particles", "teleport", "color").setValue("RAINBOW")
                        .setComment("Default Color of Particle if supported, otherwise set \"NONE\"");
                config.getNode("options", "particles", "creation", "type").setValue("WITCH_SPELL")
                        .setComment("Default particle type when creating any kind of portal");
                config.getNode("options", "particles", "creation", "color").setValue("NONE")
                        .setComment("Default Color of Particle if supported, otherwise set \"NONE\"");
            }
            //FIX
            if (config.getNode("options", "particles", "creation", "type").getString().equals("SPELL_WITCH")) {
                config.getNode("options", "particles", "creation", "type").setValue("WITCH_SPELL");
            }
            if (config.getNode("options", "particles", "teleport", "type").getString().equals("REDSTONE")) {
                config.getNode("options", "particles", "teleport", "type").setValue("REDSTONE_DUST");
            }
            if (config.getNode("options", "random_spawn_radius").isVirtual()) {
                config.getNode("options", "random_spawn_radius").setValue(5000).setComment("World radius for random spawn portals.");
            }
            if (config.getNode("options", "teleport_message").isVirtual()) {
                config.getNode("options", "teleport_message").setComment("Set message that displays when player teleports.");
                // UPDATE CONFIG
                if (config.getNode("options", "teleport_message", "enable").isVirtual()) {
                    config.getNode("options", "teleport_message", "enable").setValue(true);
                }
                config.getNode("options", "teleport_message", "title").setValue("&2%WORLD%");
                config.getNode("options", "teleport_message", "sub_title").setValue("&bx: %X%, y: %Y%, z: %Z%");
            }
            if (config.getNode("options", "advanced_permissions").isVirtual()) {
                config.getNode("options", "advanced_permissions").setValue(false).setComment(
                        "Require permission node for each portal. ex. 'pjp.portal.<name>', 'pjp.button.<world_x_y_z>'. If false use 'pjp.portal"
                                + ".interact' instead");
            }
            if (config.getNode("settings", "modules").isVirtual()) {
                config.getNode("settings", "modules").setComment("Toggle on and off specific features");
            }
            if (config.getNode("settings", "modules", "portals").isVirtual()) {
                config.getNode("settings", "modules", "portals").setValue(true);
            }
            if (config.getNode("settings", "modules", "buttons").isVirtual()) {
                config.getNode("settings", "modules", "buttons").setValue(false);
            }
            if (config.getNode("settings", "modules", "doors").isVirtual()) {
                config.getNode("settings", "modules", "doors").setValue(false);
            }
            if (config.getNode("settings", "modules", "plates").isVirtual()) {
                config.getNode("settings", "modules", "plates").setValue(false);
            }
            if (config.getNode("settings", "modules", "levers").isVirtual()) {
                config.getNode("settings", "modules", "levers").setValue(false);
            }
            if (config.getNode("settings", "modules", "signs").isVirtual()) {
                config.getNode("settings", "modules", "signs").setValue(false);
            }
            if (config.getNode("settings", "modules", "warps").isVirtual()) {
                config.getNode("settings", "modules", "warps").setValue(false);
            }
            if (config.getNode("settings", "modules", "homes").isVirtual()) {
                config.getNode("settings", "modules", "homes").setValue(false);
            }
            if (config.getNode("settings", "modules", "back").isVirtual()) {
                config.getNode("settings", "modules", "back").setValue(true);
            }
            if (config.getNode("settings", "sql").isVirtual()) {
                config.getNode("settings", "sql", "enable").setValue(false);
                config.getNode("settings", "sql", "prefix").setValue("NONE");
                config.getNode("settings", "sql", "url").setValue("localhost:3306/database");
                config.getNode("settings", "sql", "username").setValue("root");
                config.getNode("settings", "sql", "password").setValue("password");
            }
        }

        configManager.save();

        configManagers.put(configName, configManager);

        return configManager;
    }

    public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
        return loader;
    }

    public CommentedConfigurationNode getConfig() {
        return config;
    }

    private void load() {
        loader = HoconConfigurationLoader.builder().setPath(path).build();
        try {
            config = loader.load();
        } catch (IOException e) {
            Main.instance().getLog().error("Failed to load config");
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            loader.save(config);
        } catch (IOException e) {
            Main.instance().getLog().error("Failed to save config");
            e.printStackTrace();
        }
    }
}
