package me.jishuna.minetweaks.tweaks.items;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import me.jishuna.commonlib.items.ItemBuilder;
import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;

@RegisterTweak("infinite_water_bucket")
public class InfiniteWaterBucketTweak extends Tweak {
	private static final ItemStack BUCKET = new ItemBuilder(Material.WATER_BUCKET)
			.enchantment(Enchantment.INFINITY, 1).build();
	private int cost;

	public InfiniteWaterBucketTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		addEventHandler(PlayerBucketEmptyEvent.class, EventPriority.HIGH, this::onBucket);
		addEventHandler(PrepareAnvilEvent.class, this::onAnvil);
		addEventHandler(BlockDispenseEvent.class, EventPriority.HIGH, this::onDispense);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/items.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), true, false);
			SetName("Infinity Water Bucket");
			SetDescription("Allows the creation of infinite water buckets using an infinity enchanted book.");

			this.cost = config.getInt(this.getName() + ".anvil-level-cost", 20);
		});
	}

	private void onAnvil(PrepareAnvilEvent event) {
		AnvilView view = event.getView();
		ItemStack first = view.getItem(0);
		ItemStack second = view.getItem(1);

		if (first == null || first.getType() != Material.WATER_BUCKET || second == null
				|| second.getType() != Material.ENCHANTED_BOOK)
			return;

		if (first.containsEnchantment(Enchantment.INFINITY))
			return;

		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) second.getItemMeta();
		if (meta.hasStoredEnchant(Enchantment.INFINITY)) {
			event.setResult(ItemBuilder.modifyItem(BUCKET.clone()).name(view.getRenameText()).build());
			view.setRepairCost(this.cost);
		}
	}

	private void onBucket(PlayerBucketEmptyEvent event) {
		if (event.isCancelled())
			return;

		EntityEquipment equipment = event.getPlayer().getEquipment();
		for (ItemStack item : new ItemStack[] { equipment.getItemInMainHand(), equipment.getItemInOffHand() }) {
			if (item == null || item.getType() != Material.WATER_BUCKET)
				continue;

			if (item.containsEnchantment(Enchantment.INFINITY)) {
				event.setItemStack(item);
				break;
			}
		}
	}

	private void onDispense(BlockDispenseEvent event) {
		if (event.isCancelled() || event.getBlock().getType() != Material.DISPENSER)
			return;

		ItemStack item = event.getItem();
		Directional directional = (Directional) event.getBlock().getBlockData();
		BlockFace face = directional.getFacing();
		Block target = event.getBlock().getRelative(face);

		if (item.getType() == Material.WATER_BUCKET && item.containsEnchantment(Enchantment.INFINITY)) {
			event.setCancelled(true);
			if (target.getType().isAir())
				target.setType(Material.WATER);
		}
	}
}
