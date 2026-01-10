package me.jishuna.minetweaks.tweaks.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rotatable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.profile.PlayerProfile;

import me.jishuna.commonlib.items.ItemBuilder;
import me.jishuna.commonlib.items.ItemUtils;
import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;
import net.md_5.bungee.api.ChatColor;

@RegisterTweak("pumpkin_carving")
public class PumpkinCarvingTweak extends Tweak {
	private static final ChatColor ORANGE = ChatColor.of("#ff9838");
	private Map<String, String> textureMap = new TreeMap<>();
	private String[] textures;
	private Inventory inventory;

	private final Map<InventoryView, CarvingData> carvingMap = new HashMap<>();

	public PumpkinCarvingTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		addEventHandler(PlayerInteractEvent.class, EventPriority.HIGH, this::onInteract);
		addEventHandler(InventoryCloseEvent.class, this::onClose);
		addEventHandler(InventoryClickEvent.class, EventPriority.HIGH, this::onClick);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/blocks.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), true, false);
			SetName("Pumpkin Carving");
			SetDescription("Shift clicking a pumpkin with shears will allow you to carve special player head pumpkin designs.");

			ConfigurationSection section = config.getConfigurationSection(this.getName() + ".textures");

			if (section == null)
				return;

			this.textureMap.clear();

			for (String key : section.getKeys(false)) {
				this.textureMap.put(key, section.getString(key));
			}

			this.textures = this.textureMap.values().toArray(new String[0]);

			int size = (int) (9 * (Math.ceil(textureMap.size() / 9d)));
			this.inventory = Bukkit.createInventory(null, size, config.getString(this.getName() + ".gui-name", "Pumpkin Carving"));

			for (Entry<String, String> entry : this.textureMap.entrySet()) {
				ItemStack item = new ItemBuilder(Material.PLAYER_HEAD).skullTexture(entry.getValue())
						.name(ORANGE + StringUtils.capitalize(entry.getKey().replace("-", " "))).build();
				this.inventory.addItem(item);
			}
		});
	}

	private void onInteract(PlayerInteractEvent event) {
		if (event.useInteractedBlock() == Result.DENY || event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		Block block = event.getClickedBlock();
		ItemStack item = event.getItem();

		if (item == null)
			return;

		Player player = event.getPlayer();

		if (block.getType() == Material.PUMPKIN && item.getType() == Material.SHEARS && player.isSneaking()) {
			InventoryView view = player.openInventory(this.inventory);
			this.carvingMap.put(view, new CarvingData(event.getItem(), block, event.getHand()));
		}
	}

	private void onClick(InventoryClickEvent event) {
		if (event.getClickedInventory() == null)
			return;

		CarvingData carvingData = this.carvingMap.get(event.getView());

		if (carvingData == null)
			return;

		event.setCancelled(true);
		int slot = event.getRawSlot();

		if (slot < this.textures.length) {
			String texture = this.textures[slot];
			Player player = (Player) event.getWhoClicked();
			Block target = carvingData.block;

			target.setType(Material.PLAYER_HEAD);

			Rotatable data = (Rotatable) target.getBlockData();
			data.setRotation(player.getFacing());
			target.setBlockData(data);

			setSkullTexture(target, texture);

			player.playSound(player.getLocation(), Sound.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1f, 1f);

			if (player.getGameMode() != GameMode.CREATIVE)
				ItemUtils.reduceDurability(player, carvingData.item, carvingData.hand);

			this.carvingMap.remove(event.getView());
			player.closeInventory();
		}
	}

	private void setSkullTexture(Block block, String texture) {
		BlockState state = block.getState();
		if (!(state instanceof Skull skull))
			return;

		PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
		try {
			URL url = new URL("http://textures.minecraft.net/texture/" + texture);
			profile.getTextures().setSkin(url);
		} catch (MalformedURLException ex) {
			return;
		}

		skull.setOwnerProfile(profile);
		skull.update(false, false);
	}

	private void onClose(InventoryCloseEvent event) {
		this.carvingMap.remove(event.getView());
	}

	public static record CarvingData(ItemStack item, Block block, EquipmentSlot hand) {
	}
}
