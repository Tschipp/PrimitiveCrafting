package tschipp.primitivecrafting.client.crafting;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import tschipp.primitivecrafting.PrimitiveCrafting;
import tschipp.primitivecrafting.common.crafting.IPrimitiveRecipe;
import tschipp.primitivecrafting.common.crafting.PrimitiveRecipe;
import tschipp.primitivecrafting.network.Craft;

public class CraftingAction
{
	private List<IPrimitiveRecipe> validRecipes;
	private IPrimitiveRecipe currentRecipe;

	private ItemStack creationHeld;
	private ItemStack creationInv;

	private Slot inventorySlot;

	public CraftingAction(List<IPrimitiveRecipe> validRecipes, IPrimitiveRecipe currentRecipe, ItemStack creationHeld, ItemStack creationInv, Slot invSlot, @Nullable CraftingAction old)
	{
		this.validRecipes = validRecipes;
		this.currentRecipe = currentRecipe;
		this.creationHeld = creationHeld;
		this.creationInv = creationInv;
		this.inventorySlot = invSlot;
		if (old != null)
		{
			if (validRecipes.contains(old.currentRecipe))
				this.currentRecipe = old.currentRecipe;
		}
	}

	public void craft(ItemStack held, ItemStack inventoryStack, boolean craftMax, int slot)
	{
		int craftAmount = 1;
		if (craftMax)
			craftAmount = getTimesCrafted(held, inventoryStack, currentRecipe)[0];

		for (int i = 0; i < craftAmount; i++)
		{
			if (currentRecipe.isValid(held, inventoryStack))
			{
				// Server
				PrimitiveCrafting.network.sendToServer(new Craft(held, currentRecipe, slot));
				
				// Client
				currentRecipe.craft(held, inventoryStack, Minecraft.getMinecraft().player, held, slot);
			} else
				break;
		}

	}

	public List<IPrimitiveRecipe> getValidRecipes()
	{
		return validRecipes;
	}

	public void setCurrentRecipe(IPrimitiveRecipe recipe)
	{
		this.currentRecipe = recipe;
	}

	public IPrimitiveRecipe getCurrentRecipe()
	{
		return this.currentRecipe;
	}

	public Slot getSlot()
	{
		return inventorySlot;
	}

	public boolean isSame(ItemStack held, ItemStack inventory)
	{
		return held == creationHeld && inventory == creationInv;
	}

	public static int[] getTimesCrafted(ItemStack stackA, ItemStack stackB, IPrimitiveRecipe recipe)
	{
		ItemStack a = stackA.copy();
		ItemStack b = stackB.copy();

		int craftAmount = 0;
		for (int i = 0; i < 64; i++)
		{
			if (recipe.isValid(a, b))
			{
				recipe.craft(a, b, null, a, 0);
				craftAmount++;
			}
			else
				break;
		}
		
		int aCount = stackA.getCount() - a.getCount();
		int bCount = stackB.getCount() - b.getCount();
		
		return new int[] {craftAmount, aCount == 0 ? 1 : aCount, bCount == 0 ? 1 : bCount};
	}
}