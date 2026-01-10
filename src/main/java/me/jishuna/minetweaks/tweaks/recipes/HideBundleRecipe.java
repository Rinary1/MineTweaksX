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

@RegisterTweak("hide_bundle")
public class HideBundleRecipe extends Tweak {

	public HideBundleRecipe(MineTweaks plugin, String name) {
		super(plugin, name);
		setMaxVersion(20);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/recipes.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), true);
			SetName("Hide Bundles");
			SetDescription("Allows crafting bundles with rabbit hide and string.");
			
			if (!isEnabled())
				return;

			ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(getPlugin(), "hide_bundle"),
					new ItemStack(Material.BUNDLE));
			recipe.shape("121", "202", "222");
			recipe.setIngredient('1', Material.STRING);
			recipe.setIngredient('2', Material.RABBIT_HIDE);
			RecipeManager.getInstance().addRecipe(getPlugin(), recipe);
		});

	}
}
