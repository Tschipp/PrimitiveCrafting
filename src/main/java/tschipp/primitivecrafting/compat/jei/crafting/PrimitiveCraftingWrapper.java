package tschipp.primitivecrafting.compat.jei.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tschipp.primitivecrafting.common.crafting.IPrimitiveRecipe;

public class PrimitiveCraftingWrapper extends BlankRecipeWrapper
{

	protected List<List<ItemStack>> inputs;
	protected ItemStack output;
	
	public PrimitiveCraftingWrapper(IPrimitiveRecipe recipe)
	{
		inputs = new ArrayList<List<ItemStack>>();
		
		
		ItemStack[] iA = recipe.getA().ingredient.getMatchingStacks().clone();
		for(int i = 0; i < iA.length; i++)
		{
			ItemStack s = iA[i].copy();
			s.setCount(recipe.getA().count);
			iA[i] = s;
		}
		
		ItemStack[] iB = recipe.getB().ingredient.getMatchingStacks().clone();
		for(int i = 0; i < iB.length; i++)
		{
			ItemStack s = iB[i].copy();
			s.setCount(recipe.getB().count);
			iB[i] = s;
		}
		
		inputs.add(Arrays.asList(iA));
		inputs.add(Arrays.asList(iB));
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
