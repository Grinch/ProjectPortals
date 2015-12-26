package com.gmail.trentech.pjp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private File file;
	private CommentedConfigurationNode config;
	private ConfigurationLoader<CommentedConfigurationNode> loader;
	
	public ConfigManager(String configName) {
		String folder = "config/" + Resource.NAME + "/";
        if (!new File(folder).isDirectory()) {
        	new File(folder).mkdirs();
        }
		file = new File(folder + configName);
		
		create();
		load();
		init();
	}
	
	public ConfigManager() {
		String folder = "config/" + Resource.NAME + "/";
        if (!new File(folder).isDirectory()) {
        	new File(folder).mkdirs();
        }
		file = new File(folder, "config.conf");
		
		create();
		load();
		init();
	}
	
	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getConfig() {
		return config;
	}

	public void save(){
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}
	
	private void init() {
		if(file.getName().equalsIgnoreCase("config.conf")){
			if(config.getNode("Options", "Cube", "Size").getString() == null) {
				config.getNode("Options", "Cube", "Size").setValue(100).setComment("Maximum portal region size");
			}
			if(config.getNode("Options", "Cube", "Replace-Frame").getString() == null) {
				config.getNode("Options", "Cube", "Replace-Frame").setValue(true);
			}
			if(config.getNode("Options", "Show-Particles").getString() == null) {
				config.getNode("Options", "Show-Particles").setValue(true).setComment("Display particle effects on portal creation and teleporting");
			}
		}else if(file.getName().equalsIgnoreCase("portals.conf")){
			if(config.getNode("Buttons").getString() == null) {
				config.getNode("Buttons").setComment("DO NOT EDIT THIS FILE");
			}
		}
		save();
	}

	private void create(){
		if(!file.exists()) {
			try {
				Main.getLog().info("Creating new " + file.getName() + " file...");
				file.createNewFile();		
			} catch (IOException e) {				
				Main.getLog().error("Failed to create new config file");
				e.printStackTrace();
			}
		}
	}
	
	private void load(){
		loader = HoconConfigurationLoader.builder().setFile(file).build();
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}

	public boolean removeCuboidLocation(String locationName){
		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Cuboids").getChildrenMap().entrySet()){
			String uuid = node.getKey().toString();
			List<String> list = config.getNode("Cuboids", uuid, "Locations").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
	    	
			for(String loc : list){
				if(loc.equalsIgnoreCase(locationName)){
					config.getNode("Cuboids", uuid).setValue(null);
					save();
					return true;
				}
			}
		}
		return false;
	}

	public Location<World> getCuboid(String locationName){
		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Cuboids").getChildrenMap().entrySet()){
			String uuid = node.getKey().toString();

	    	List<String> list = config.getNode("Cuboids", uuid, "Locations").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());

			if(list != null){
				for(String loc : list){
					if(!loc.equalsIgnoreCase(locationName)){						
						continue;
					}

					String worldName = config.getNode("Cuboids", uuid, "World").getString();
					
					if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
						continue;
					}
					World world = Main.getGame().getServer().getWorld(worldName).get();
					
					int x = world.getSpawnLocation().getBlockX();
					int y = world.getSpawnLocation().getBlockY();
					int z = world.getSpawnLocation().getBlockZ();
					
					if(config.getNode("Cuboids", uuid, "X").getString() != null && config.getNode("Cuboids", uuid, "Y").getString() != null && config.getNode("Cuboids", uuid, "Z").getString() != null){
						x = config.getNode("Cuboids", uuid, "X").getInt();
						y = config.getNode("Cuboids", uuid, "Y").getInt();
						z = config.getNode("Cuboids", uuid, "Z").getInt();
					}

					return Main.getGame().getServer().getWorld(worldName).get().getLocation(x, y, z);
				}
			}
		}
		return null;
	}

}
