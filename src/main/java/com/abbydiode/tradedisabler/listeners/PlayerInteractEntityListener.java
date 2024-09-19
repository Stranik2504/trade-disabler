package com.abbydiode.tradedisabler.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.abbydiode.tradedisabler.App;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import javax.xml.stream.events.Namespace;
import java.util.List;

public class PlayerInteractEntityListener implements Listener {
	private App plugin;
	
	public PlayerInteractEntityListener(App plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if (!plugin.getConfig().getBoolean("enable"))
			return;
		
		Entity entity = e.getRightClicked();
		
		if (entity.getType() == EntityType.VILLAGER) {
			Villager villager = (Villager) entity;
			villager.getRecipes().forEach(recipe -> {
				for (String material : plugin.getConfig().getStringList("disabledMaterials")) {
					if (recipe.getResult().getType() == Material.matchMaterial(material)) {
						recipe.setUses(recipe.getMaxUses());
					} 
				}

				if (recipe.getResult().getType() == Material.ENCHANTED_BOOK) {
					for (String enchantment : plugin.getConfig().getStringList("disabledEnchantments")) {
						String enchantName = enchantment.split("\\|")[0];

						recipe.getResult().getEnchantments().forEach((enchant, level) -> {
							App.getInstance().getLogger().info(enchantName);
							
							if (enchant.getKey().toString().equals(enchantName)) {
								if (enchantment.split("\\|").length == 2) {
									int enchantLevel = Integer.parseInt(enchantment.split("\\|")[1]);

									App.getInstance().getLogger().info(String.valueOf(enchantLevel));
									
									if (level == enchantLevel)
										recipe.setUses(recipe.getMaxUses());
								}
								else if (level > 0) {
									recipe.setUses(recipe.getMaxUses());
								}
							}
						});
					}
				}
			});
		}
	}
}
