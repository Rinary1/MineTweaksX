package me.jishuna.minetweaks.tweaks.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;

@RegisterTweak("inventory_crafting_table")
public class InventoryCraftingTweak extends Tweak {

	private boolean require_permission;

	private String permission;

	public InventoryCraftingTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		addEventHandler(InventoryClickEvent.class, EventPriority.HIGH, this::onClick);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/items.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), true, false);
			SetName("Use Crafting Table in Inventory");
			SetDescription("Allows players to use crafting tables from their inventory by pressing F on them.");

			this.require_permission = config.getBoolean(this.getName() + ".require_permission", false);
			this.permission = config.getString(this.getName() + ".permission", "minetweaks.items.inventory_crafting");
		});
	}

	private void onClick(InventoryClickEvent event) {
		if (event.isCancelled())
			return;

		if (this.require_permission && !event.getWhoClicked().hasPermission(this.permission))
			return;
		
		if (event.getInventory().getType() != InventoryType.CRAFTING || event.getClick() != ClickType.SWAP_OFFHAND)
			return;

		ItemStack item = event.getCurrentItem();
		if (item == null || item.getType().isAir())
			return;

		if (item.getType() == Material.CRAFTING_TABLE) {
			event.setCancelled(true);
			Bukkit.getScheduler().runTask(getPlugin(), () -> event.getWhoClicked().openWorkbench(null, true));
		}
	}
}
