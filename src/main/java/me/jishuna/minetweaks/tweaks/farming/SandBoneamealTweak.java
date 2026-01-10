package me.jishuna.minetweaks.tweaks.farming;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;
import me.jishuna.minetweaks.api.util.FarmingUtils;

@RegisterTweak("sand_bonemealing")
public class SandBoneamealTweak extends Tweak {
	private boolean sand;
	private boolean redSand;

	public SandBoneamealTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		addEventHandler(PlayerInteractEvent.class, EventPriority.HIGH, this::onInteract);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/farming.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), true, false);
			SetName("Sand Bonemealing");
			SetDescription("Allows players to bonemeal sand for dead bushes.");

			this.sand = config.getBoolean(this.getName() + ".allow-normal-sand", true);
			this.redSand = config.getBoolean(this.getName() + ".allow-red-sand", true);
		});
	}

	private void onInteract(PlayerInteractEvent event) {
		if (event.useInteractedBlock() == Result.DENY || event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Block block = event.getClickedBlock();
		ItemStack item = event.getItem();

		if (item != null && item.getType() == Material.BONE_MEAL) {
			if (block.getType() == Material.SAND && sand) {
				FarmingUtils.handleSand(item, block, event.getPlayer().getGameMode());
			} else if (block.getType() == Material.RED_SAND && redSand) {
				FarmingUtils.handleSand(item, block, event.getPlayer().getGameMode());
			}
		}
	}
}
