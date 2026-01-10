package me.jishuna.minetweaks.tweaks.mobs;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;

@RegisterTweak("disable_enderman_griefing")
public class EndermanGriefingTweak extends Tweak {

	public EndermanGriefingTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		addEventHandler(EntityChangeBlockEvent.class, EventPriority.HIGH, this::onBlockChange);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/mobs.yml").ifPresent(config -> {
			var configName = this.getName().split("_", 2)[1];
			loadDefaults(config, configName, true);
			SetName("Disable Enderman Griefing");
			SetDescription("Stops endermen from picking up blocks.");
		});
	}

	private void onBlockChange(EntityChangeBlockEvent event) {
		if (event.getEntityType() == EntityType.ENDERMAN) {
			event.setCancelled(true);
		}
	}
}
