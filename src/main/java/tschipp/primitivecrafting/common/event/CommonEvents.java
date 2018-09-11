package tschipp.primitivecrafting.common.event;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tschipp.primitivecrafting.common.crafting.RecipeRegistry;

public class CommonEvents
{

	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void registerRecipes(RegistryEvent.Register<IRecipe> event)
	{
		RecipeRegistry.regRecipes();
	}

}
