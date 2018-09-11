package tschipp.primitivecrafting.common.crafting;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class PrimitiveIngredient
{
	public Ingredient ingredient;
	public int count;

	public PrimitiveIngredient(Ingredient ingredient, int count)
	{
		this.ingredient = ingredient;

		this.count = count;
	}

	public boolean test(@Nullable ItemStack stack)
	{
		if (stack == null)
		{
			return false;
		} else
		{
			for (ItemStack itemstack : ingredient.getMatchingStacks())
			{
				boolean equal = PrimitiveRecipe.areStacksEqual(itemstack, stack);
				if (equal)
					return true;
			}

			return false;
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + ((ingredient == null) ? 0 : ingredient.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof PrimitiveIngredient))
			return false;

		PrimitiveIngredient other = (PrimitiveIngredient) obj;

		if (other.count != this.count)
			return false;

		ItemStack[] thisStack = this.ingredient.getMatchingStacks();
		ItemStack[] otherStack = other.ingredient.getMatchingStacks();

		boolean equal = true;

		if (thisStack.length != otherStack.length)
			return false;

		for (int i = 0; i < thisStack.length; i++)
		{
			if (!ItemStack.areItemStacksEqual(thisStack[i], otherStack[i]))
				equal = false;
		}

		return equal;
	}

}
