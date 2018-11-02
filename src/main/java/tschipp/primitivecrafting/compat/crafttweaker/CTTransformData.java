package tschipp.primitivecrafting.compat.crafttweaker;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.ItemStackUnknown;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import tschipp.primitivecrafting.common.crafting.PrimitiveRecipe;
import tschipp.primitivecrafting.common.crafting.TransformData;

public class CTTransformData extends TransformData
{
	private IIngredient ingredient;
	private int count;

	public CTTransformData(IIngredient ing, int count)
	{
		this.ingredient = ing;
		this.count = count;
	}

	@Override
	public void transformStack(ItemStack toTransform, EntityPlayer player, boolean isHoverStack, int slot)
	{
		IItemStack transform = CraftTweakerMC.getIItemStack(toTransform);
		IPlayer iPlayer = CraftTweakerMC.getIPlayer(player);

		if (ingredient.hasNewTransformers())
		{
			IItemStack remaining = ingredient.applyNewTransform(transform);
			if (remaining != ItemStackUnknown.INSTANCE)
			{
				ItemStack remainingStack = CraftTweakerMC.getItemStack(remaining);

				if (isHoverStack)
					player.inventory.setItemStack(remainingStack);
				else
					player.inventory.setInventorySlotContents(slot, remainingStack);

			}
		} else if (ingredient.hasTransformers())
		{
			toTransform.shrink(count);
		} else
		{
			ItemStack container = ForgeHooks.getContainerItem(toTransform);
			if (container != ItemStack.EMPTY)
			{
				if (isHoverStack)
					player.inventory.setItemStack(container);
				else
					player.inventory.setInventorySlotContents(slot, container);

			} else
				toTransform.shrink(count);
		}
	}
}
