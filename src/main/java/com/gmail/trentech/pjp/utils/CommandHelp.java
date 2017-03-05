package com.gmail.trentech.pjp.utils;

import com.gmail.trentech.helpme.help.Argument;
import com.gmail.trentech.helpme.help.Help;
import com.gmail.trentech.helpme.help.Usage;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;

public class CommandHelp {

    public static void init(ConfigurationNode modules) {
        if (Sponge.getPluginManager().isLoaded("helpme")) {
            Usage usagePortal = new Usage(Argument.of("<destination>", "Specifies a world or server if [-b] is supplied"))
                    .addArgument(Argument.of("[-b]", "Specifies that <destination> is a bungee connected server"))
                    .addArgument(Argument.of("[-c <x,y,z>]",
                            "Specifies the coordinates to set spawn to. x and z must fall within the range -30,000,000 to 30,000,000 (exclusive, "
                                    + "without the "
                                    + "commas), and y must be within the range -4096 to 4096 inclusive. This is ignored if [-b] is supplied"))
                    .addArgument(Argument.of("[-d <direction>]",
                            "Specifies the direction player will face upon teleporting. The following can be used: NORTH, NORTH_WEST, WEST, "
                                    + "SOUTH_WEST, SOUTH, SOUTH_EAST, EAST, NORTH_EAST"))
                    .addArgument(Argument.of("[-p <price>]", "Specifies a price player will be charged for using portal"));

            if (modules.getNode("buttons").getBoolean()) {
                Help button = new Help("button", "button", "Use this command to create a button that will teleport you to other worlds")
                        .setPermission("pjp.cmd.button")
                        .setUsage(usagePortal)
                        .addExample("/button MyWorld -c random")
                        .addExample("/button MyWorld -c -100,65,254 -d south")
                        .addExample("/button MyWorld -d southeast")
                        .addExample("/button MyWorld -c -100,65,254")
                        .addExample("/button MyWorld");

                Help.register(button);
            }
            if (modules.getNode("doors").getBoolean()) {
                Help door = new Help("door", "door", "Use this command to create a door that will teleport you to other worlds")
                        .setPermission("pjp.cmd.door")
                        .setUsage(usagePortal)
                        .addExample("/door MyWorld -c random")
                        .addExample("/door MyWorld -c -100,65,254 -d south")
                        .addExample("/door MyWorld -d southeast")
                        .addExample("/door MyWorld -c -100,65,254")
                        .addExample("/door MyWorld");

                Help.register(door);
            }
            if (modules.getNode("plates").getBoolean()) {
                Help plate = new Help("plate", "plate", "Use this command to create a pressure plate that will teleport you to other worlds")
                        .setPermission("pjp.cmd.plate")
                        .setUsage(usagePortal)
                        .addExample("/plate MyWorld -c random")
                        .addExample("/plate MyWorld -c -100,65,254 -d south")
                        .addExample("/plate MyWorld -d southeast")
                        .addExample("/plate MyWorld -c -100,65,254")
                        .addExample("/plate MyWorld");

                Help.register(plate);
            }
            if (modules.getNode("signs").getBoolean()) {
                Help sign = new Help("sign", "sign", "Use this command to create a sign that will teleport you to other worlds")
                        .setPermission("pjp.cmd.sign")
                        .setUsage(usagePortal)
                        .addExample("/sign MyWorld -c random")
                        .addExample("/sign MyWorld -c -100,65,254 -d south")
                        .addExample("/sign MyWorld -d southeast")
                        .addExample("/sign MyWorld -c -100,65,254")
                        .addExample("/sign MyWorld");

                Help.register(sign);
            }
            if (modules.getNode("levers").getBoolean()) {
                Help lever = new Help("lever", "lever", "Use this command to create a lever that will teleport you to other worlds")
                        .setPermission("pjp.cmd.lever")
                        .setUsage(usagePortal)
                        .addExample("/lever MyWorld -c random")
                        .addExample("/lever MyWorld -c -100,65,254 -d south")
                        .addExample("/lever MyWorld -d southeast")
                        .addExample("/lever MyWorld -c -100,65,254")
                        .addExample("/lever MyWorld");

                Help.register(lever);
            }
            if (modules.getNode("portals").getBoolean()) {
                Usage usageCreate = new Usage(Argument.of("<name>", "Specifies the name of the new portal"))
                        .addArgument(Argument.of("<destination>", "Specifies a world or server if argument [-b] is supplied"))
                        .addArgument(Argument.of("[-b]", "Specifies that <destination> is a bungee connected server"))
                        .addArgument(Argument.of("[-c <x,y,z>]",
                                "Specifies the coordinates to set spawn to. x and z must fall within the range -30,000,000 to 30,000,000 "
                                        + "(exclusive, without the "
                                        + "commas), and y must be within the range -4096 to 4096 inclusive. This is ignored if [-b] is supplied"))
                        .addArgument(Argument.of("[-d <direction>]",
                                "Specifies the direction player will face upon teleporting. The following can be used: NORTH, NORTH_WEST, WEST, "
                                        + "SOUTH_WEST, SOUTH, SOUTH_EAST, EAST, NORTH_EAST"))
                        .addArgument(Argument.of("[-e <particle> [color]]",
                                "Specifies a Particle and ParticleColor the portal will use. Colors are only compatible with REDSTONE_DUST"))
                        .addArgument(Argument.of("[-p <price>]", "Specifies a price player will be charged for using portal"));

                Help portalCreate = new Help("portal create", "create", "Use this command to create a portal that will teleport you to other worlds")
                        .setPermission("pjp.cmd.portal.create")
                        .setUsage(usageCreate)
                        .addExample("/portal create MyPortal MyWorld -c -100,65,254")
                        .addExample("/portal create MyPortal MyWorld -c random")
                        .addExample("/portal create MyPortal MyWorld -c -100,65,254 -d south")
                        .addExample("/portal create MyPortal MyWorld -d southeast")
                        .addExample("/portal create MyPortal MyWorld -p 50")
                        .addExample("/portal create MyPortal MyWorld -e REDSTONE_DUST BLUE")
                        .addExample("/portal create MyPortal MyWorld");

                Usage usageDestination = new Usage(Argument.of("<name>", "Specifies the name of the targetted portal"))
                        .addArgument(Argument.of("<destination>", "Specifies a world or server if is bungee portal"))
                        .addArgument(Argument.of("[x,y,z]",
                                "Specifies the coordinates to set spawn to. x and z must fall within the range -30,000,000 to 30,000,000 "
                                        + "(exclusive, without the "
                                        + "commas), and y must be within the range -4096 to 4096 inclusive. This is ignored if is bungee portal"));

                Help portalDestination = new Help("portal destination", "destination", "Change as existing portals destination")
                        .setPermission("pjp.cmd.portal.destination")
                        .setUsage(usageDestination)
                        .addExample("/portal destination Skyland 100,65,400")
                        .addExample("/portal destination Server1")
                        .addExample("/portal destination MyPortal DIM1");

                Help portalList = new Help("portal list", "list", "List all portals")
                        .setPermission("pjp.cmd.portal.list");

                Usage usageParticle = new Usage(Argument.of("<name>", "Specifies the name of the targetted portal"))
                        .addArgument(Argument.of("<particle>", "Specifies the Particle the portal will use."))
                        .addArgument(Argument.of("[color]",
                                "Specifies the color the Particles will be. Color currently only available for REDSTONE_DUST"));

                Help portalParticle = new Help("portal particle", "particle", "Change a portals particle effect.")
                        .setPermission("pjp.cmd.portal.particle")
                        .setUsage(usageParticle)
                        .addExample("/portal particle MyPortal REDSTONE_DUST BLUE")
                        .addExample("/portal particle MyPortal CRIT");

                Usage usagePrice = new Usage(Argument.of("<name>", "Specifies the name of the targetted portal"))
                        .addArgument(Argument.of("<price>", "Specifies a price player will be charged for using portal"));

                Help portalPrice = new Help("portal price", "price", "Charge players for using portals. 0 to disable")
                        .setPermission("pjp.cmd.portal.price")
                        .setUsage(usagePrice)
                        .addExample("/portal price MyPortal 0")
                        .addExample("/portal price MyPortal 50");

                Usage usageRemove = new Usage(Argument.of("<name>", "Specifies the name of the targetted portal"));

                Help portalRemove = new Help("portal remove", "remove", "Remove an existing portal")
                        .setPermission("pjp.cmd.portal.remove")
                        .setUsage(usageRemove)
                        .addExample("/portal remove MyPortal");

                Usage usageRename = new Usage(Argument.of("<oldName>", "Specifies the name of the targetted portal"))
                        .addArgument(Argument.of("<newName>", "Specifies the new name of the portal"));

                Help portalRename = new Help("portal rename", "rename", "Rename portal")
                        .setPermission("pjp.cmd.portal.rename")
                        .setUsage(usageRename)
                        .addExample("/portal rename MyPortal ThisPortal");

                Help portalSave = new Help("portal save", "save", "Saves generated portal")
                        .setPermission("pjp.cmd.portal.save");

                Help portal = new Help("portal", "portal", " Top level portal command")
                        .setPermission("pjp.cmd.portal")
                        .addChild(portalSave)
                        .addChild(portalRename)
                        .addChild(portalRemove)
                        .addChild(portalPrice)
                        .addChild(portalParticle)
                        .addChild(portalList)
                        .addChild(portalDestination)
                        .addChild(portalCreate);

                Help.register(portal);
            }

            if (modules.getNode("homes").getBoolean()) {
                Usage usageCreate = new Usage(Argument.of("<name>", "Specifies the name of the new home"));

                Help homeCreate = new Help("home create", "create", "Create a new home")
                        .setPermission("pjp.cmd.home.create")
                        .setUsage(usageCreate)
                        .addExample("/home create MyHome");

                Help homeList = new Help("home list", "list", "List all homes")
                        .setPermission("pjp.cmd.home.list");

                Usage usageRemove = new Usage(Argument.of("<name>", "Specifies the name of the targetted home"));

                Help homeRemove = new Help("home remove", "remove", "Remove an existing home")
                        .setPermission("pjp.cmd.home.remove")
                        .setUsage(usageRemove)
                        .addExample("/home remove OldHome");

                Usage usageRename = new Usage(Argument.of("<oldName>", "Specifies the name of the targetted home"))
                        .addArgument(Argument.of("<newName>", "Specifies the new name of the home"));

                Help homeRename = new Help("home rename", "rename", "Rename home")
                        .setPermission("pjp.cmd.home.rename")
                        .setUsage(usageRename)
                        .addExample("/home rename MyHome Castle");

                Help home = new Help("home", "home", " Top level home command")
                        .setPermission("pjp.cmd.home")
                        .addChild(homeRename)
                        .addChild(homeRemove)
                        .addChild(homeList)
                        .addChild(homeCreate);

                Help.register(home);
            }
            if (modules.getNode("warps").getBoolean()) {
                Usage usagecreate = new Usage(Argument.of("<name>", "Specifies the name of the new warp point"))
                        .addArgument(Argument.of("<destination>", "Specifies a world or server if [-b] is supplied"))
                        .addArgument(Argument.of("[-b]", "Specifies that <destination> is a bungee connected server"))
                        .addArgument(Argument.of("[-c <x,y,z>]",
                                "Specifies the coordinates to set spawn to. x and z must fall within the range -30,000,000 to 30,000,000 (exclusive, without the "
                                        + "commas), and y must be within the range -4096 to 4096 inclusive. This is ignored if [-b] is supplied"))
                        .addArgument(Argument.of("[-d <direction>]",
                                "Specifies the direction player will face upon teleporting. The following can be used: NORTH, NORTH_WEST, WEST, SOUTH_WEST, SOUTH, SOUTH_EAST, EAST, NORTH_EAST"))
                        .addArgument(Argument.of("[-p <price>]", "Specifies a price player will be charged for using this warp"));

                Help warpCreate = new Help("warp create", "create", "Use this command to create a warp that will teleport you to other worlds")
                        .setPermission("pjp.cmd.warp.create")
                        .setUsage(usagecreate)
                        .addExample("/warp create Lobby MyWorld")
                        .addExample("/warp create Lobby MyWorld -c -100,65,254")
                        .addExample("/warp create Random MyWorld -c random")
                        .addExample("/warp create Lobby MyWorld -c -100,65,254 -d south")
                        .addExample("/warp create Lobby MyWorld -d southeast")
                        .addExample("/warp create Lobby");

                Help warpList = new Help("warp list", "list", "List all warp points")
                        .setPermission("pjp.cmd.warp.list");

                Usage usagePrice = new Usage(Argument.of("<name>", "Specifies the name of the targetted warp point"))
                        .addArgument(Argument.of("<price>", "Specifies a price player will be charged for using this warp"));

                Help warpPrice = new Help("warp price", "price", "Charge players for using warps. 0 to disable")
                        .setPermission("pjp.cmd.warp.price")
                        .setUsage(usagePrice)
                        .addExample("/warp price Lobby 0")
                        .addExample("/warp price Lobby 50");

                Usage usageRemove = new Usage(Argument.of("<name>", "Specifies the name of the targetted warp point"));

                Help warpRemove = new Help("warp remove", "remove", "Remove an existing  warp point")
                        .setPermission("pjp.cmd.warp.remove")
                        .setUsage(usageRemove)
                        .addExample("/warp remove OldSpawn");

                Usage usageRename = new Usage(Argument.of("<oldName>", "Specifies the name of the targetted warp point"))
                        .addArgument(Argument.of("<newName>", "Specifies the new name of the warp point"));

                Help warpRename = new Help("warp rename", "rename", "Rename warp")
                        .setPermission("pjp.cmd.warp.rename")
                        .setUsage(usageRename)
                        .addExample("/warp rename Spawn Lobby");

                Help warp = new Help("warp", "warp", " Top level warp command")
                        .setPermission("pjp.cmd.warp")
                        .addChild(warpRename)
                        .addChild(warpRemove)
                        .addChild(warpPrice)
                        .addChild(warpList)
                        .addChild(warpCreate);

                Help.register(warp);
            }
        }
    }
}
