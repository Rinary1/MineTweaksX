package me.jishuna.minetweaks.tweaks.blocks;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;

@RegisterTweak("sign_editing")
public class SignEditTweak extends Tweak {

	public SignEditTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		setMaxVersion(19);
		addEventHandler(PlayerInteractEvent.class, EventPriority.HIGH, this::onInteract);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/blocks.yml" + this.getName() + ".yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), true, false);
			SetName("Sign Editing");
			SetDescription("Allows players to edit signs by sneak-right-clicking them.");
		});
	}

	private void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!player.isSneaking() ||  event.useInteractedBlock() == Result.DENY || event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Block block = event.getClickedBlock();
		BlockState state = block.getState();

		if (!(state instanceof Sign sign))
			return;

		player.openSign(sign);
	}
}
