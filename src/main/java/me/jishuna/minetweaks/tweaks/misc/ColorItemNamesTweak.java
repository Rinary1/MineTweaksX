package me.jishuna.minetweaks.tweaks.misc;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;
import net.md_5.bungee.api.ChatColor;

@RegisterTweak("color_item_names")
public class ColorItemNamesTweak extends Tweak {
	public ColorItemNamesTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		addEventHandler(PrepareAnvilEvent.class, this::onAnvil);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/misc.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), true);
			SetName("Colored Item Names");
			SetDescription("Allows players to color item names in an anvil using the & color codes.");
		});
	}

	private void onAnvil(PrepareAnvilEvent event) {
		ItemStack result = event.getResult();
		if (result == null || !result.hasItemMeta())
			return;

		ItemMeta meta = result.getItemMeta();
		if (!meta.hasDisplayName())
			return;
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', meta.getDisplayName()));
		result.setItemMeta(meta);
		event.setResult(result);
	}
}
