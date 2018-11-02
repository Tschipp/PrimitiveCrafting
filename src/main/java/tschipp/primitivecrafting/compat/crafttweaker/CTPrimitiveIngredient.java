package tschipp.primitivecrafting.compat.crafttweaker;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import tschipp.primitivecrafting.common.crafting.PrimitiveIngredient;
import tschipp.primitivecrafting.common.crafting.TransformData;

public class CTPrimitiveIngredient extends PrimitiveIngredient
{
	
	private CTTransformData transformData;
	
	public CTPrimitiveIngredient(IIngredient ingredient, int count)
	{
		this.count = count;
		this.ingredient = CraftTweakerMC.getIngredient(ingredient);
		this.transformData = new CTTransformData(ingredient, count);
	}

	@Override
	public TransformData getTransformForStack(ItemStack stack)
	{
		return transformData;
	}
}
