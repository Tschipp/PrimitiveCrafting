package tschipp.primitivecrafting.common.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

public class Configs {
	
	public static class Settings
	{
		@Config.RequiresMcRestart
		@Comment("If all registred crafting recipes that only need two components should also be registred as primitive recipes")
		public boolean useDefaultCraftingRecipes = true;
	}

}
