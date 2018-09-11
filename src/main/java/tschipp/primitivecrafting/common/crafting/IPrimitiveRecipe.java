package tschipp.primitivecrafting.common.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IPrimitiveRecipe
{

	public ItemStack getResult();
	
	public PrimitiveIngredient getA();

	public PrimitiveIngredient getB();

	public boolean isValid(ItemStack a, ItemStack b);
	
	public void craft(ItemStack a, ItemStack b, EntityPlayer player);	
}
