package tschipp.primitivecrafting.common.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

public class Configs {
	
	public static class Settings
	{
		@Config.RequiresMcRestart
		@Comment("Whether all registred crafting recipes that only need two components should also be registred as primitive recipes")
		public boolean useDefaultCraftingRecipes = true;
		
		@Config.RequiresMcRestart
		@Comment("Whether you can craft registred crafting recipes that have more than two components, as long as there are at most two different components.")
		public boolean recipesWithMultipleIngredients = false;
		
		@Comment("Whether the inventory crafting is disabled.")
		public boolean disableInventoryCrafting = false;
	}

}
