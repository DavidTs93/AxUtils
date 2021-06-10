package me.Aldreda.AxUtils.Utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class WorldGuardManager {
	public final FlagRegistry FlagRegistry;
	
	public WorldGuardManager() {
		FlagRegistry = WorldGuard.getInstance().getFlagRegistry();
	}
	
	public List<ProtectedRegion> getRegions(Location loc) {
		return sortRegionsByPriority(getRegionSet(loc));
	}
	
	public Map<String,ProtectedRegion> getRegions(World world) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(world)));
		return ((RegionManager) Objects.requireNonNull(regions)).getRegions();
	}
	
	public List<ProtectedRegion> getRegions(ProtectedRegion region, World world) {
		try {
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionManager regions = container.get(BukkitAdapter.adapt(world));
			return sortRegionsByPriority(regions.getApplicableRegions(region));
		} catch (Exception e) {}
		return null;
	}
	
	public ApplicableRegionSet getRegionSet(Location loc) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));
		return ((RegionManager) Objects.requireNonNull(regions)).getApplicableRegions(BlockVector3.at(loc.getX(),loc.getY(),loc.getZ()));
	}
	
	public List<ProtectedRegion> sortRegionsByPriority(ApplicableRegionSet regions) {
		return sortRegionsByPriority(new ArrayList<ProtectedRegion>(regions.getRegions()));
	}
	
	public List<ProtectedRegion> sortRegionsByPriority(List<ProtectedRegion> regions) {
		List<ProtectedRegion> regionList = new ArrayList<ProtectedRegion>();
		if (regions != null && regions.size() > 0) {
			regionList.addAll(regions);
			regionList.sort(Comparator.comparingInt(ProtectedRegion::getPriority));
		}
		return regionList;
	}
	
	public StateFlag newStateFlag(String name, boolean defaultValue) {
		try {
			StateFlag flag = new StateFlag(name,defaultValue);
			FlagRegistry.register(flag);
			return flag;
		} catch (Exception e) {
			Flag<?> flag = FlagRegistry.get(name);
			if (flag instanceof StateFlag) return (StateFlag) flag;
		}
		return null;
	}
	
	public StringFlag newStringFlag(String name, String defaultValue) {
		try {
			StringFlag flag = new StringFlag(name,defaultValue);
			FlagRegistry.register(flag);
			return flag;
		} catch (Exception e) {
			Flag<?> flag = FlagRegistry.get(name);
			if (flag instanceof StringFlag) return (StringFlag) flag;
		}
		return null;
	}
}