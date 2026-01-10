package me.jishuna.minetweaks.tweaks.mobs;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;

@RegisterTweak("chicken_plucking")
public class ChickenPluckingTweak extends Tweak {

	private boolean damage;

	private int damage_amount;

	private double drop_chance;

	public ChickenPluckingTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		addEventHandler(PlayerInteractEntityEvent.class, EventPriority.HIGH, this::onInteract);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/mobs.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), true);
			SetName("Chicken Plucking");
			SetDescription("Allows players to pluck feathers from a chicken, dealing half a heart of damage in the process.");

			this.damage = config.getBoolean(this.getName() + ".damage", true);
			this.damage_amount = config.getInt(this.getName() + ".damage-amount", 1);
			this.drop_chance = config.getDouble(this.getName() + ".drop-chance", 100);
		});
	}

	private void onInteract(PlayerInteractEntityEvent event) {
		if (event.isCancelled() || event.getHand() != EquipmentSlot.HAND || !event.getPlayer().isSneaking())
			return;
		ItemStack item = event.getPlayer().getEquipment().getItemInMainHand();

		if (!item.getType().isAir())
			return;

		if (event.getRightClicked()instanceof Chicken chicken && !chicken.isDead()) {

			if (this.damage)
			{
				chicken.damage(this.damage_amount);
				chicken.setNoDamageTicks(0);
			}

			if (ThreadLocalRandom.current().nextDouble() * 100 >= this.drop_chance)
				return;

			chicken.getWorld().dropItem(chicken.getLocation(), new ItemStack(Material.FEATHER));
		}
	}
}
