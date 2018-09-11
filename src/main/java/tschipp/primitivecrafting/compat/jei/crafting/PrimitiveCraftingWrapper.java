package tschipp.primitivecrafting.compat.jei.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import tschipp.primitivecrafting.common.crafting.IPrimitiveRecipe;

public class PrimitiveCraftingWrapper extends BlankRecipeWrapper
{

	protected List<List<ItemStack>> inputs;
	protected ItemStack output;
	
	public PrimitiveCraftingWrapper(IPrimitiveRecipe recipe)
	{
		inputs = new ArrayList<List<ItemStack>>();
		
		ItemStack[] inputsA = recipe.getA().ingredient.getMatchingStacks();
		for(ItemStack s : inputsA)
			s.setCount(recipe.getA().count);
		
		ItemStack[] inputsB = recipe.getB().ingredient.getMatchingStacks();
		for(ItemStack s : inputsB)
			s.setCount(recipe.getB().count);
		
		inputs.add(Arrays.asList(inputsA));
		inputs.add(Arrays.asList(inputsB));
		output = recipe.getResult();		
	}
	
	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, output);
		
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		
		int x = 0;
		int y = 0;
		
		minecraft.fontRenderer.drawStringWithShadow("+", x + 20, y + 5, 16777215);
		minecraft.fontRenderer.drawStringWithShadow("=", x + 50, y + 5, 16777215);
	}

}
