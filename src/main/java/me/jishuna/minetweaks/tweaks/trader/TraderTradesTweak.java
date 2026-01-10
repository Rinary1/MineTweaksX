package me.jishuna.minetweaks.tweaks.trader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;

@RegisterTweak("replace_trades")
public class TraderTradesTweak extends Tweak {
	private int min;
	private int max;
	private List<MerchantRecipe> recipes = new ArrayList<>();

	public TraderTradesTweak(MineTweaks plugin, String name) {
		super(plugin, name);

		addEventHandler(CreatureSpawnEvent.class, EventPriority.HIGH, this::onSpawn);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/wandering_trader.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), true, false);
			SetName("Replace Trades");
			SetDescription("Replaces the default wandering trader traders with custom trades.");

			this.min = config.getInt(this.getName() + "min-trades", 5);
			this.max = config.getInt(this.getName() + "max-trades", 7);

			this.recipes.clear();

			for (String entry : config.getStringList(this.getName() + "custom-trades")) {
				String[] data = entry.split(",");

				if (data.length < 4)
					continue;

				Material buy = Material.getMaterial(data[0].toUpperCase());
				Material sell = Material.getMaterial(data[2].toUpperCase());

				if (buy == null || sell == null)
					continue;

				if (!StringUtils.isNumeric(data[1]) || !StringUtils.isNumeric(data[3]))
					continue;

				int buyCount = Integer.parseInt(data[1]);
				int sellCount = Integer.parseInt(data[3]);

				MerchantRecipe recipe = new MerchantRecipe(new ItemStack(sell, sellCount), 10);
				recipe.addIngredient(new ItemStack(buy, buyCount));

				recipes.add(recipe);
			}
		});
	}

	private void onSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled())
			return;
		
		if (event.getEntity()instanceof WanderingTrader trader) {
			List<MerchantRecipe> newRecipes = new ArrayList<>();
			Random random = ThreadLocalRandom.current();
			int count = random.nextInt(max - min) + min;

			for (int i = 0; i < count; i++) {
				MerchantRecipe recipe = this.recipes.get(random.nextInt(this.recipes.size()));

				if (!newRecipes.contains(recipe)) {
					newRecipes.add(recipe);
				}
			}

			trader.setRecipes(newRecipes);
		}
	}
}
