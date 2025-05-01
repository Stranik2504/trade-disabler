package com.abbydiode.tradedisabler.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.abbydiode.tradedisabler.App;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import javax.xml.stream.events.Namespace;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

public class PlayerInteractEntityListener implements Listener {
	private App _plugin;
	
	public PlayerInteractEntityListener(App plugin) {
		_plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void crafterCraftEvent(CrafterCraftEvent event) {
		for (String material : _plugin.getConfig().getStringList("disabledMaterials")) {
			if (event.getResult().getType() == Material.matchMaterial(material)) {
				event.setCancelled(true);
				var result = event.getResult().clone();
				result.setType(Material.DIRT);
				event.setResult(result);
			}
		}
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if (!_plugin.getConfig().getBoolean("enable"))
			return;
		
		Entity entity = e.getRightClicked();
		
		if (
			entity.getType() == EntityType.VILLAGER ||
			entity.getType() == EntityType.WANDERING_TRADER
		) {
			var villager = (AbstractVillager) entity;
			var recipes = new ArrayList<>(villager.getRecipes());
			
			for (var i = 0; i < recipes.size(); i++) {
				if (isNeedRemove(recipes.get(i))) {
					recipes.remove(i);
					i--;
				}
			}

			villager.setRecipes(recipes);
		}
	}
	
	private boolean isNeedRemove(MerchantRecipe recipe) {
		for (String material : _plugin.getConfig().getStringList("disabledMaterials")) {
			if (recipe.getResult().getType() == Material.matchMaterial(material)) {
				return true;
			}
		}

		if (recipe.getResult().getType() == Material.ENCHANTED_BOOK) {
			for (String enchantment : _plugin.getConfig().getStringList("disabledEnchantments")) {
				String enchantName = enchantment.split("\\|")[0];
				
				for (var pair : recipe.getResult().getEnchantments().entrySet()) {
					var enchant = pair.getKey();
					var level = pair.getValue();

					if (enchant.getKey().toString().equals(enchantName)) {
						if (enchantment.split("\\|").length == 2) {
							int enchantLevel = Integer.parseInt(enchantment.split("\\|")[1]);

							if (level == enchantLevel)
								return true;
						}
						else if (level > 0) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
}
