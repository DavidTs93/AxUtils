package me.Aldreda.AxUtils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.bukkit.Location;

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

public class WorldGuardManager {
	public final FlagRegistry FlagRegistry;
	
	public WorldGuardManager() {
		FlagRegistry = WorldGuard.getInstance().getFlagRegistry();
	}
	
	public List<ProtectedRegion> getRegions(Location loc) {
		try {
			return Arrays.asList(sortRegionsByPriority(getRegionSet(loc)));
		} catch (Exception e) {}
		return null;
	}
	
	private ApplicableRegionSet getRegionSet(Location loc) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));
		return ((RegionManager) Objects.requireNonNull(regions)).getApplicableRegions(BlockVector3.at(loc.getX(),loc.getY(),loc.getZ()));
	}
	
	private ProtectedRegion[] sortRegionsByPriority(ApplicableRegionSet regset) {
		ProtectedRegion[] regionArray = new ProtectedRegion[0];
		List<ProtectedRegion> regionList = new ArrayList<ProtectedRegion>();
		if (regset.size() == 0) return regionArray;
		else if (regset.size() == 1) {
			regionArray = new ProtectedRegion[1];
			return (ProtectedRegion[]) regset.getRegions().toArray(regionArray);
		} else {
			Iterator<ProtectedRegion> iterator = regset.iterator();
			while (iterator.hasNext()) regionList.add((ProtectedRegion) iterator.next());
			regionList.sort(Comparator.comparingInt(ProtectedRegion::getPriority));
			return (ProtectedRegion[]) regionList.toArray(regionArray);
		}
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