package tschipp.primitivecrafting.common.crafting;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import tschipp.primitivecrafting.common.crafting.TransformData.TransformType;

public class PrimitiveIngredient
{
	public Ingredient ingredient;
	protected TransformData[] transformData;
	public int count;

	protected PrimitiveIngredient()
	{
	}
	
	public PrimitiveIngredient(Ingredient ingredient, int count)
	{
		this(ingredient, count, true);
	}
	
	public PrimitiveIngredient(Ingredient ingredient, int count, boolean init)
	{
		this.ingredient = ingredient;

		ItemStack[] stacks = ingredient.getMatchingStacks();
		transformData = new TransformData[stacks.length];
		this.count = count;
		
		if(init)
			initTransformData();
	}
	
	public void initTransformData()
	{
		ItemStack[] stacks = ingredient.getMatchingStacks();
		

		
		
		for (int i = 0; i < stacks.length; i++)
		{
			ItemStack stack = stacks[i];

			TransformData data;
			if (stack.getItem().hasContainerItem(stack))
			{
				ItemStack container = stack.getItem().getContainerItem(stack);
				if (stack.getItem() == container.getItem())
					data = TransformData.getTransformData(TransformType.DAMAGE, container.getItemDamage() - stack.getItemDamage());
				else
					data = TransformData.getTransformData(TransformType.GIVE_BACK, container);
			} else
				data = TransformData.getTransformData(TransformType.SHRINK, count);
			
			transformData[i] = data;
		}
	}

	public TransformData getTransformForStack(ItemStack stack)
	{
		ItemStack[] matchingStacks = ingredient.getMatchingStacks();
		for (int i = 0; i < matchingStacks.length; i++)
		{
			ItemStack itemstack = matchingStacks[i];

			boolean equal = PrimitiveRecipe.areStacksEqual(itemstack, stack) ;
			boolean matches = ingredient.apply(stack);
			
			if (matches && equal)
				return transformData[i];
			
		}
		return null;
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
				boolean matches = ingredient.apply(stack);

				if (matches)
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
