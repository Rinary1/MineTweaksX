package me.jishuna.minetweaks.tweaks.mobs;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityInteractEvent;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;

@RegisterTweak("no_trampling_farmland")
public class MobFarmlandTrampleTweak extends Tweak {

	private boolean ignore_players;

	public MobFarmlandTrampleTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		addEventHandler(EntityInteractEvent.class, this::onEntityTrample);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/mobs.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), true);
			SetName("No Mob Farmland Trampling");
			SetDescription("Stops non-players from trampling farmland.");

			this.ignore_players = config.getBoolean(this.getName() + ".ignore-players", false);
		});
	}

	private void onEntityTrample(EntityInteractEvent event) {
		if (!(this.ignore_players && event.getEntityType() == EntityType.PLAYER) && event.getBlock().getType() == Material.FARMLAND) {
			event.setCancelled(true);
		}
	}
}
