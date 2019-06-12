package tschipp.primitivecrafting.compat.gamestages;

import net.darkhax.gamestages.event.StagesSyncedEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tschipp.primitivecrafting.common.crafting.IPrimitiveRecipe;
import tschipp.primitivecrafting.common.crafting.RecipeRegistry;
import tschipp.primitivecrafting.common.helper.StageHelper;
import tschipp.primitivecrafting.compat.jei.JEIIntegration;
import tschipp.primitivecrafting.compat.jei.crafting.PrimitiveCraftingWrapper;

public class GamestageEvents
{
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGamestageSync(StagesSyncedEvent event)
	{
		if (Loader.isModLoaded("jei"))
			syncJEI(event.getEntityPlayer());
	}

	private void syncJEI(EntityPlayer player)
	{
		if (JEIIntegration.reg != null)
		{
			if (player == null)
				return;

			for (PrimitiveCraftingWrapper wrap : JEIIntegration.allRecipes)
			{
				if (!StageHelper.hasStage(player, wrap.getGamestage()))
					JEIIntegration.reg.hideRecipe(wrap, "primitive_crafting");
				else
					JEIIntegration.reg.unhideRecipe(wrap, "primitive_crafting");
			}

		}
	}
}
