package me.jishuna.minetweaks.tweaks.armorstand;

import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;

@RegisterTweak("armorstand_offhand")
public class ArmorStandOffhandTweak extends Tweak {

	public ArmorStandOffhandTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		addEventHandler(PlayerInteractAtEntityEvent.class, EventPriority.HIGH, this::onEntityInteract);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/armorstand.yml").ifPresent(config -> {
			loadDefaults(config, this.getName().split("_", 2)[1], true, false);
			SetName("Armor Stand Offhand Items");
			SetDescription("Allows placing items in an armor stand's offhand when holding them in your offhand.");
		});
	}

	private void onEntityInteract(PlayerInteractAtEntityEvent event) {
		if (event.isCancelled())
			return;

		if (event.getPlayer().getEquipment().getItemInMainHand().getType().isAir()
				&& event.getRightClicked()instanceof ArmorStand stand) {

			ItemStack current = stand.getEquipment().getItemInOffHand();
			if (current.getType().isAir()) {
				ItemStack item = event.getPlayer().getEquipment().getItemInOffHand();
				if (item.getType().isAir())
					return;

				ItemStack toHold = item.clone();
				toHold.setAmount(1);

				item.setAmount(item.getAmount() - 1);

				stand.getEquipment().setItemInOffHand(toHold);
			} else {
				event.getPlayer().getInventory().addItem(current);
				stand.getEquipment().setItemInOffHand(null);
			}
			event.setCancelled(true);
		}
	}

}
