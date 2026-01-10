package me.jishuna.minetweaks.tweaks.mobs;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;

@RegisterTweak("bedrock_wither_health")
public class WitherHealthTweak extends Tweak {
	public WitherHealthTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		addEventHandler(CreatureSpawnEvent.class, EventPriority.HIGH, this::onSpawn);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/mobs.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), false);
			SetName("Bedrock Wither Health");
			SetDescription("Sets the wither's health to 600 (instead of 300) to match bedrock edition.");
		});
	}

	private void onSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled())
			return;
		
		if (event.getEntityType() == EntityType.WITHER) {
			LivingEntity entity = event.getEntity();
			AttributeInstance instance = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			instance.setBaseValue(600);
			entity.setHealth(600);
		}
	}
}
