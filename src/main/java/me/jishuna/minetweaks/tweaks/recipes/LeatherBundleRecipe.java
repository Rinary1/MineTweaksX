package me.jishuna.minetweaks.tweaks.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.minetweaks.MineTweaks;
import me.jishuna.minetweaks.api.RecipeManager;
import me.jishuna.minetweaks.api.RegisterTweak;
import me.jishuna.minetweaks.api.tweak.Tweak;

@RegisterTweak("leather_bundle")
public class LeatherBundleRecipe extends Tweak {

	public LeatherBundleRecipe(MineTweaks plugin, String name) {
		super(plugin, name);
		setMaxVersion(20);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/recipes.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), false);
			SetName("Leather Bundles");
			SetDescription("Allows crafting bundles with leather and string.");
			
			if (!isEnabled())
				return;

			ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(getPlugin(), "leather_bundle"),
					new ItemStack(Material.BUNDLE));
			recipe.shape("121", "202", "222");
			recipe.setIngredient('1', Material.STRING);
			recipe.setIngredient('2', Material.LEATHER);
			RecipeManager.getInstance().addRecipe(getPlugin(), recipe);
		});

	}
}
