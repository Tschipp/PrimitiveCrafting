package tschipp.primitivecrafting.compat.jei.crafting;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import tschipp.primitivecrafting.PrimitiveCrafting;
import tschipp.primitivecrafting.common.crafting.IPrimitiveRecipe;
import tschipp.primitivecrafting.common.crafting.RecipeRegistry;

public class PrimitiveCraftingCategory implements IRecipeCategory<PrimitiveCraftingWrapper>
{

	public IGuiHelper helper;
	
	public PrimitiveCraftingCategory(IGuiHelper h)
	{
		helper = h;
	}
	
	@Override
	public String getUid()
	{
		return "primitive_crafting";
	}

	@Override
	public String getTitle()
	{
		return I18n.translateToLocal("primitivecrafting.title");
	}

	@Override
	public String getModName()
	{
		return PrimitiveCrafting.NAME;
	}

	@Override
	public IDrawable getBackground()
	{
		return helper.createBlankDrawable(80, 19);		
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, PrimitiveCraftingWrapper recipeWrapper, IIngredients ingredients)
	{
		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);
		
		IGuiItemStackGroup guiStacks = recipeLayout.getItemStacks();
		guiStacks.init(0, true, 0, 0);
		guiStacks.init(1, true, 0 + 30, 0);
		guiStacks.init(2, false, 0 + 60, 0);

		guiStacks.set(0, inputs.get(0));
		guiStacks.set(1, inputs.get(1));
		guiStacks.set(2, outputs.get(0));
		
	}
	
	public static List<PrimitiveCraftingWrapper> getRecipes()
	{
		List<IPrimitiveRecipe> recipes = RecipeRegistry.getRecipes();
		List<PrimitiveCraftingWrapper> wrappers = new ArrayList<PrimitiveCraftingWrapper>();
				
		recipes.sort((r1, r2) -> r1.getResult().getItem().getRegistryName().toString().compareTo(r2.getResult().getItem().getRegistryName().toString()));
		
		for(IPrimitiveRecipe r : recipes)
			wrappers.add(new PrimitiveCraftingWrapper(r));
		
		return wrappers;
	}
	
	@Override
	public IDrawable getIcon()
	{
		
		return helper.createDrawable(new ResourceLocation(PrimitiveCrafting.MODID + ":textures/gui/icon.png"), 0, 0, 16, 16, 16, 16);
	}
	
	
	

}
