package com.gmail.trentech.pjp;

import com.gmail.trentech.pjp.commands.CMDBack;
import com.gmail.trentech.pjp.commands.CommandManager;
import com.gmail.trentech.pjp.data.immutable.ImmutableHomeData;
import com.gmail.trentech.pjp.data.immutable.ImmutableSignPortalData;
import com.gmail.trentech.pjp.data.mutable.HomeData;
import com.gmail.trentech.pjp.data.mutable.SignPortalData;
import com.gmail.trentech.pjp.listeners.ButtonListener;
import com.gmail.trentech.pjp.listeners.DoorListener;
import com.gmail.trentech.pjp.listeners.LegacyListener;
import com.gmail.trentech.pjp.listeners.LeverListener;
import com.gmail.trentech.pjp.listeners.PlateListener;
import com.gmail.trentech.pjp.listeners.PortalListener;
import com.gmail.trentech.pjp.listeners.SignListener;
import com.gmail.trentech.pjp.listeners.TeleportListener;
import com.gmail.trentech.pjp.portal.LocationSerializable;
import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Properties;
import com.gmail.trentech.pjp.utils.CommandHelp;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Resource;
import com.gmail.trentech.pjp.utils.SQLUtils;
import com.gmail.trentech.pjp.utils.Timings;
import com.google.inject.Inject;
import me.flibio.updatifier.Updatifier;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR,
        url = Resource.URL,
        dependencies = {@Dependency(id = "Updatifier", optional = true), @Dependency(id = "helpme", version = "0.2.3", optional = false)})
public class Main {

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path path;

    @Inject
    private Logger log;

    private static PluginContainer plugin;
    private static Main instance;

    @Listener
    public void onPreInitializationEvent(GamePreInitializationEvent event) {
        plugin = Sponge.getPluginManager().getPlugin(Resource.ID).get();
        instance = this;

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        ConfigManager configManager = ConfigManager.init();
        ConfigurationNode config = configManager.getConfig();

        Timings timings = new Timings();

        Sponge.getDataManager().registerBuilder(LocationSerializable.class, new LocationSerializable.Builder());
        Sponge.getDataManager().registerBuilder(Properties.class, new Properties.Builder());
        Sponge.getDataManager().registerContentUpdater(Portal.Local.class, new Portal.Local.Update1());
        Sponge.getDataManager().registerBuilder(Portal.Local.class, new Portal.Local.Builder());
        Sponge.getDataManager().registerBuilder(Portal.Server.class, new Portal.Server.Builder());

        Sponge.getEventManager().registerListeners(this, new TeleportListener(timings));

        Sponge.getCommandManager().register(this, new CommandManager().cmdPJP, "pjp");

        ConfigurationNode modules = config.getNode("settings", "modules");

        if (modules.getNode("back").getBoolean()) {
            Sponge.getCommandManager().register(this, new CMDBack().cmdBack, "back");
        }
        if (modules.getNode("buttons").getBoolean()) {
            Sponge.getEventManager().registerListeners(this, new ButtonListener(timings));
            Sponge.getCommandManager().register(this, new CommandManager().cmdButton, "button", "b");

            getLog().info("Button module activated");
        }
        if (modules.getNode("doors").getBoolean()) {
            Sponge.getEventManager().registerListeners(this, new DoorListener(timings));
            Sponge.getCommandManager().register(this, new CommandManager().cmdDoor, "door", "d");

            getLog().info("Door module activated");
        }
        if (modules.getNode("plates").getBoolean()) {
            Sponge.getEventManager().registerListeners(this, new PlateListener(timings));
            Sponge.getCommandManager().register(this, new CommandManager().cmdPlate, "plate", "pp");

            getLog().info("Pressure plate module activated");
        }
        if (modules.getNode("signs").getBoolean()) {
            Sponge.getDataManager().register(SignPortalData.class, ImmutableSignPortalData.class, new SignPortalData.Builder());
            Sponge.getEventManager().registerListeners(this, new SignListener(timings));
            Sponge.getCommandManager().register(this, new CommandManager().cmdSign, "sign", "s");

            getLog().info("Sign module activated");
        }
        if (modules.getNode("levers").getBoolean()) {
            Sponge.getEventManager().registerListeners(this, new LeverListener(timings));
            Sponge.getCommandManager().register(this, new CommandManager().cmdLever, "lever", "l");

            getLog().info("Lever module activated");
        }

        if (modules.getNode("portals").getBoolean()) {
            Sponge.getEventManager().registerListeners(this, new PortalListener(timings));
            Sponge.getCommandManager().register(this, new CommandManager().cmdPortal, "portal", "p");

            if (config.getNode("options", "portal", "legacy_builder").getBoolean()) {
                Sponge.getEventManager().registerListeners(this, new LegacyListener(timings));
            }

            getLog().info("Portal module activated");
        }

        if (modules.getNode("homes").getBoolean()) {
            Sponge.getDataManager().register(HomeData.class, ImmutableHomeData.class, new HomeData.Builder());
            Sponge.getCommandManager().register(this, new CommandManager().cmdHome, "home", "h");

            getLog().info("Home module activated");
        }
        if (modules.getNode("warps").getBoolean()) {
            Sponge.getCommandManager().register(this, new CommandManager().cmdWarp, "warp", "w");

            getLog().info("Warp module activated");
        }

        CommandHelp.init(modules);

        SQLUtils.createTables();
    }

    @Listener
    public void onStartedServer(GameStartedServerEvent event) {
        Portal.init();
    }

    @Listener
    public void onReloadEvent(GameReloadEvent event) {
        Sponge.getEventManager().unregisterPluginListeners(getPlugin());

        for (CommandMapping mapping : Sponge.getCommandManager().getOwnedBy(getPlugin())) {
            Sponge.getCommandManager().removeMapping(mapping);
        }

        ConfigManager configManager = ConfigManager.init();
        ConfigurationNode config = configManager.getConfig();

        Timings timings = new Timings();

        Sponge.getEventManager().registerListeners(this, new TeleportListener(timings));

        Sponge.getCommandManager().register(this, new CMDBack().cmdBack, "back");
        Sponge.getCommandManager().register(this, new CommandManager().cmdPJP, "pjp");

        ConfigurationNode modules = config.getNode("settings", "modules");

        if (modules.getNode("portals").getBoolean()) {
            Sponge.getEventManager().registerListeners(this, new PortalListener(timings));
            Sponge.getCommandManager().register(this, new CommandManager().cmdPortal, "portal", "p");

            if (config.getNode("options", "portal", "legacy_builder").getBoolean()) {
                Sponge.getEventManager().registerListeners(this, new LegacyListener(timings));
            }

            getLog().info("Portal module activated");
        }
        if (modules.getNode("buttons").getBoolean()) {
            Sponge.getEventManager().registerListeners(this, new ButtonListener(timings));
            Sponge.getCommandManager().register(this, new CommandManager().cmdButton, "button", "b");

            getLog().info("Button module activated");
        }
        if (modules.getNode("doors").getBoolean()) {
            Sponge.getEventManager().registerListeners(this, new DoorListener(timings));
            Sponge.getCommandManager().register(this, new CommandManager().cmdDoor, "door", "d");

            getLog().info("Door module activated");
        }
        if (modules.getNode("plates").getBoolean()) {
            Sponge.getEventManager().registerListeners(this, new PlateListener(timings));
            Sponge.getCommandManager().register(this, new CommandManager().cmdPlate, "plate", "pp");

            getLog().info("Pressure plate module activated");
        }
        if (modules.getNode("signs").getBoolean()) {
            Sponge.getEventManager().registerListeners(this, new SignListener(timings));
            Sponge.getCommandManager().register(this, new CommandManager().cmdSign, "sign", "s");

            getLog().info("Sign module activated");
        }
        if (modules.getNode("levers").getBoolean()) {
            Sponge.getEventManager().registerListeners(this, new LeverListener(timings));
            Sponge.getCommandManager().register(this, new CommandManager().cmdLever, "lever", "l");

            getLog().info("Lever module activated");
        }
        if (modules.getNode("homes").getBoolean()) {
            Sponge.getCommandManager().register(this, new CommandManager().cmdHome, "home", "h");

            getLog().info("Home module activated");
        }
        if (modules.getNode("warps").getBoolean()) {
            Sponge.getCommandManager().register(this, new CommandManager().cmdWarp, "warp", "w");

            getLog().info("Warp module activated");
        }

        Portal.init();
    }

    public Logger getLog() {
        return log;
    }

    public Path getPath() {
        return path;
    }

    public static PluginContainer getPlugin() {
        return plugin;
    }

    public static Main instance() {
        return instance;
    }

}