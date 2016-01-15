package com.gmail.trentech.pjp.portals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjp.Main;
import com.gmail.trentech.pjp.utils.ConfigManager;
import com.gmail.trentech.pjp.utils.Utils;

import ninja.leaping.configurate.ConfigurationNode;

public class Portal {

	private final String name;
	private final String destination;
	private final List<String> region;

	public Portal(String uuid, String destination, List<String> region) {
		this.name = uuid;
		this.destination = destination;
		this.region = region;
	}
	
	public String getName() {
		return name;
	}

	public Optional<Location<World>> getDestination() {
		String[] args = destination.split(":");
		
		if(!Main.getGame().getServer().getWorld(args[0]).isPresent()){
			return Optional.empty();
		}
		World world = Main.getGame().getServer().getWorld(args[0]).get();
		
		if(args[1].equalsIgnoreCase("random")){
			return Optional.of(Utils.getRandomLocation(world));
		}else if(args[1].equalsIgnoreCase("spawn")){
			return Optional.of(world.getSpawnLocation());
		}else{
			String[] coords = args[1].split("\\.");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			
			return Optional.of(world.getLocation(x, y, z));	
		}
	}

	public List<Location<World>> getRegion() {
		List<Location<World>> list = new ArrayList<>();
		
		for(String loc : region){
			String[] args = loc.split(":");
			
			if(!Main.getGame().getServer().getWorld(args[0]).isPresent()){
				continue;
			}
			World world = Main.getGame().getServer().getWorld(args[0]).get();

			String[] coords = args[1].split("\\.");

			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			
			list.add(world.getLocation(x, y, z));	
		}
		return list;
	}

	public static Optional<Portal> get(Location<World> location){
		String locationName = location.getExtent().getName() + ":" + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
		
		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();
		
		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Portals").getChildrenMap().entrySet()){
			String name = node.getKey().toString();

	    	List<String> list = config.getNode("Portals", name, "Region").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());

	    	if(!list.contains(locationName)){
	    		continue;
	    	}
	    	
	    	String destination = config.getNode("Portals", name, "Destination").getString();

	    	return Optional.of(new Portal(name, destination, list));
		}
		return Optional.empty();
	}
	
	public static Optional<Portal> getByName(String name){
		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();
		if(config.getNode("Portals", name, "Region").getString() != null){
			String destination = config.getNode("Portals", name, "Destination").getString();
			List<String> list = config.getNode("Portals", name, "Region").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());

		    return Optional.of(new Portal(name, destination, list));
		}
		return Optional.empty();
	}
	
	public static void remove(String name){
		ConfigManager configManager = new ConfigManager("portals.conf");
		ConfigurationNode config = configManager.getConfig();
		
		config.getNode("Portals").removeChild(name);
		configManager.save();
	}
	
	public static void save(Portal portal){
		ConfigManager configManager = new ConfigManager("portals.conf");
		ConfigurationNode config = configManager.getConfig();

		config.getNode("Portals", portal.getName(), "Destination").setValue(portal.destination);
		config.getNode("Portals", portal.getName(), "Region").setValue(portal.region);

		configManager.save();
	}
	
	public static List<Portal> list(){
		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();
		
		List<Portal> list = new ArrayList<>();
		
		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Portals").getChildrenMap().entrySet()){
			String name = node.getKey().toString();
			if(config.getNode("Portals", name, "Region").getString() != null){
				String destination = config.getNode("Portals", name, "Destination").getString();
				List<String> region = config.getNode("Portals", name, "Region").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
				list.add(new Portal(name, destination, region));
			}
		}

		return list;
	}

}