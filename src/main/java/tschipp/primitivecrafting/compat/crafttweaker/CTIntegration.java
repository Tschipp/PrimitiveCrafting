package tschipp.primitivecrafting.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import tschipp.primitivecrafting.common.crafting.PrimitiveIngredient;
import tschipp.primitivecrafting.common.crafting.RecipeRegistry;

@ZenRegister
@ZenClass("mods.primitivecrafting")
public class CTIntegration
{

	@ZenMethod
	public static void addRecipe(IIngredient a, IIngredient b, IItemStack output)
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

					RecipeRegistry.registerRecipe(pA, pB, stackOutput);
				}
			}
		}
	}

	@ZenMethod
	public static void removeRecipe(IIngredient a, IIngredient b, IItemStack output)
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
