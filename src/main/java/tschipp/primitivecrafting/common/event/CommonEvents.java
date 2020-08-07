package tschipp.primitivecrafting.common.event;

import org.lwjgl.input.Mouse;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tschipp.primitivecrafting.common.crafting.RecipeRegistry;
import tschipp.primitivecrafting.common.helper.ListHandler;

public class CommonEvents
{

	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void registerRecipes(RegistryEvent.Register<IRecipe> event)
	{
		ListHandler.initFilters();
	}

}
 