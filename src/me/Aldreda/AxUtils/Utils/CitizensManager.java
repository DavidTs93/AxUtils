package me.Aldreda.AxUtils.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;

public class CitizensManager {
	List<TraitInfo> newTraits;
	
	public CitizensManager() {
		newTraits = new ArrayList<TraitInfo>();
	}
	
	public void registerTrait(Class<? extends Trait> clazz, String name) {
		TraitInfo trait = TraitInfo.create(clazz).withName(name);
		newTraits.add(trait);
		CitizensAPI.getTraitFactory().registerTrait(trait);
	}
	
	public boolean isNPC(Entity entity) {
		return CitizensAPI.getNPCRegistry().isNPC(entity);
	}
}