package tschipp.primitivecrafting.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import tschipp.primitivecrafting.common.crafting.RecipeRegistry;
import tschipp.primitivecrafting.compat.jei.crafting.PrimitiveCraftingCategory;

@JEIPlugin
public class JEIIntegration implements IModPlugin
{
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry)
	{
		registry.addRecipeCategories(new PrimitiveCraftingCategory(registry.getJeiHelpers().getGuiHelper()));
	}
	
	@Override
	public void register(IModRegistry registry)
	{
		registry.addRecipes(PrimitiveCraftingCategory.getRecipes(), "primitive_crafting");
	}
}
