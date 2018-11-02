package tschipp.primitivecrafting.common.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IPrimitiveRecipe
{

	public ItemStack getResult();
	
	public PrimitiveIngredient getA();

	public PrimitiveIngredient getB();

	public boolean isValid(ItemStack a, ItemStack b);
	
	public void craft(ItemStack a, ItemStack b, EntityPlayer player, ItemStack hoverStack, int slot);	
	
	public void getCraftingResult(ItemStack a, ItemStack b, EntityPlayer player, boolean isAHoverStack, int slot);
	
	public String getTier();
	
	public void setTier(String s);

	public ResourceLocation getRegistryName();
}
