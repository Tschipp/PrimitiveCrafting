package tschipp.primitivecrafting.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import tschipp.primitivecrafting.PrimitiveCrafting;
import tschipp.primitivecrafting.common.crafting.IPrimitiveRecipe;
import tschipp.primitivecrafting.common.crafting.PrimitiveIngredient;
import tschipp.primitivecrafting.common.crafting.PrimitiveRecipe;
import tschipp.primitivecrafting.common.crafting.RecipeRegistry;

@ZenRegister
@ZenClass("mods.primitivecrafting")
public class CTIntegration
{

	private static int recipeCount = 0;
	
	@ZenMethod
	public static void addRecipe(IItemStack output, IIngredient a, IIngredient b, String registryName, String gamestage)
	{
		if (a != null && b != null && output != null)
		{
			if (!(a instanceof ILiquidStack) && !(b instanceof ILiquidStack))
			{
				ItemStack stackOutput = CraftTweakerMC.getItemStack(output);

				int countA = a.getAmount();
				int countB = b.getAmount();

				if (!stackOutput.isEmpty())
				{
					CTPrimitiveIngredient pA;
					CTPrimitiveIngredient pB;

					pA = new CTPrimitiveIngredient(a, countA);
					pB = new CTPrimitiveIngredient(b, countB);

					IPrimitiveRecipe recipe = new PrimitiveRecipe(stackOutput, pA, pB, new ResourceLocation(registryName));
					recipe.setTier(gamestage);
					
					RecipeRegistry.registerRecipe(recipe);
				}
			}
		}
	}
	
	@ZenMethod
	public static void addRecipe(IItemStack output, IIngredient a, IIngredient b)
	{
		addRecipe(output, a, b, PrimitiveCrafting.MODID + ":primitive_crafttweaker_recipe_" + recipeCount, "");
		recipeCount++;
	}
	
	@ZenMethod
	public static void addRecipe(IItemStack output, IIngredient a, IIngredient b, String registryName)
	{
		addRecipe(output, a, b, registryName, "");
		recipeCount++;
	}
	

	@ZenMethod
	public static void removeRecipe(IItemStack output, IIngredient a, IIngredient b)
	{
		if (a != null && b != null && output != null)
		{
			if (!(a instanceof ILiquidStack) && !(b instanceof ILiquidStack))
			{
				Ingredient ingA = CraftTweakerMC.getIngredient(a);
				Ingredient ingB = CraftTweakerMC.getIngredient(b);
				ItemStack stackOutput = CraftTweakerMC.getItemStack(output);

				int countA = a.getAmount();
				int countB = b.getAmount();

				if (ingA != null && ingB != null && !stackOutput.isEmpty())
				{
					PrimitiveIngredient pA = new PrimitiveIngredient(ingA, countA);
					PrimitiveIngredient pB = new PrimitiveIngredient(ingB, countB);

					RecipeRegistry.removeRecipe(pA, pB, stackOutput);
				}
			}
		}
	}

}
