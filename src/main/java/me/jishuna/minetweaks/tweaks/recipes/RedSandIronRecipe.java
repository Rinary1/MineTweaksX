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

@RegisterTweak("red_sand_iron")
public class RedSandIronRecipe extends Tweak {

	public RedSandIronRecipe(MineTweaks plugin, String name) {
		super(plugin, name);
	}

	@Override
	public void reload() {
		FileUtils.loadResource(getPlugin(), "Tweaks/recipes.yml").ifPresent(config -> {
			loadDefaults(config, this.getName(), true);
			SetName("Red Sand from Iron");
			SetDescription("Allows crafting red sand with sand and iron nuggets.");
			
			if (!isEnabled())
				return;

			ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(getPlugin(), "redden_sand"),
					new ItemStack(Material.RED_SAND, 8));
			recipe.setGroup("redden_sand");
			recipe.shape("111", "101", "111");
			recipe.setIngredient('1', Material.SAND);
			recipe.setIngredient('0', new ItemStack(Material.IRON_NUGGET, config.getInt(this.getName() + ".amount", 1)));
			RecipeManager.getInstance().addRecipe(getPlugin(), recipe);
		});

	}
}
