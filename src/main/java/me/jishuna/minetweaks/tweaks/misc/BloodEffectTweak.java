package me.jishuna.minetweaks.tweaks.misc;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;

@RegisterTweak("blood_effect")
public class BloodEffectTweak extends Tweak {
	private BlockData data;
	private int count;

	public BloodEffectTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		addEventHandler(EntityDamageByEntityEvent.class, EventPriority.HIGH, this::onDamage);
	}

	@Override
	public boolean isToggleable() {
		return true;
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/misc.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), true);
			SetName("Blood Effect");
			SetDescription("Allows players to see a blood effect when attacking enemies. Suggested by: LordZimbo");

			this.count = config.getInt(this.getName() + ".particle-count", 10);

			Material material = Material.getMaterial(config.getString(this.getName() + ".particle-material").toUpperCase());
			this.data = material == null ? Material.RED_CONCRETE.createBlockData() : material.createBlockData();
		});
	}

	private void onDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;

		if (!(event.getEntity() instanceof LivingEntity living) || event.getEntityType() == EntityType.ARMOR_STAND
				|| !(event.getDamager() instanceof Player player) || isDisabled(player))
			return;

		double halfHeight = living.getHeight() / 2;
		double width = living.getWidth() / 4;

		Particle.FALLING_DUST.builder().location(living.getLocation().add(0, halfHeight, 0)).count(this.count).offset(width, halfHeight / 2, width).data(this.data).spawn();
	}
}
