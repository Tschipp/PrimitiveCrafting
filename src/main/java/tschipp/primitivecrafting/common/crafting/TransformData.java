package tschipp.primitivecrafting.common.crafting;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class TransformData
{

	private int shrinkAmount = 0;
	private ItemStack giveBack = ItemStack.EMPTY;
	private ItemStack replace = ItemStack.EMPTY;
	private int damageApplied = 0;
	private boolean consumeEntire = false;

	public TransformType type;

	protected TransformData()
	{
	}

	public void transformStack(ItemStack toTransform, @Nullable EntityPlayer player, boolean isHoverStack, int slot)
	{
		try
		{
			switch (type)
			{
			case SHRINK:
				toTransform.shrink(shrinkAmount);
				break;
			case REUSE:
				break;
			case GIVE_BACK:
				if (player != null)
				{
					if (giveBack.isEmpty())
						PrimitiveRecipe.addItem(player, toTransform.copy());
					else
						PrimitiveRecipe.addItem(player, giveBack.copy());
				}
				toTransform.shrink(1);
				break;
			case REPLACE:
				if (player != null)
				{
					if (isHoverStack)
						player.inventory.setItemStack(replace.copy());
					else
						player.inventory.setInventorySlotContents(slot, replace.copy());
				}
				break;
			case DAMAGE:
				toTransform.damageItem(damageApplied, player);
				break;
			case CONSUME:
				toTransform.setCount(0);
				break;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static TransformData getTransformData(TransformType type, Object... params)
	{
		TransformData data = new TransformData();

		data.type = type;

		switch (type)
		{
		case SHRINK:
			if (params.length > 0)
				data.shrinkAmount = (int) params[0];
			else
				data.shrinkAmount = 1;
			break;
		case REUSE:
			break;
		case GIVE_BACK:
			if (params.length > 0)
				data.giveBack = ((ItemStack) params[0]).copy();
			data.shrinkAmount = 1;
			break;
		case REPLACE:
			if (params.length > 0)
				data.replace = ((ItemStack) params[0]).copy();
			break;
		case DAMAGE:
			if (params.length > 0)
				data.damageApplied = (int) params[0];
			else
				data.damageApplied = 1;
			break;
		case CONSUME:
			data.consumeEntire = true;
			break;
		}

		return data;
	}

	public static enum TransformType
	{
		SHRINK, REUSE, GIVE_BACK, REPLACE, DAMAGE, CONSUME;
	}

}
