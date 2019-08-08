package tschipp.primitivecrafting.common.crafting;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import tschipp.primitivecrafting.PrimitiveCrafting;
import tschipp.primitivecrafting.network.AddItem;

public class PrimitiveRecipe implements IPrimitiveRecipe
{

	private ItemStack result;
	private PrimitiveIngredient a;
	private PrimitiveIngredient b;

	private String tier = "";
	
	private ResourceLocation registryName;
	
	public PrimitiveRecipe(ItemStack result, PrimitiveIngredient a, PrimitiveIngredient b, ResourceLocation loc)
	{
		this.result = result.copy();

		this.a = a;
		this.b = b;
		
		this.registryName = loc;
	}

	@Override
	public ItemStack getResult()
	{
		return result.copy();
	}

	@Override
	public PrimitiveIngredient getA()
	{
		return a;
	}

	@Override
	public PrimitiveIngredient getB()
	{
		return b;
	}

	@Override
	public boolean isValid(ItemStack a, ItemStack b)
	{
		boolean bool = (this.a.test(a) && this.b.test(b) && this.a.count <= a.getCount() && this.b.count <= b.getCount()) || (this.a.test(b) && this.b.test(a) && this.a.count <= b.getCount() && this.b.count <= a.getCount());

		return bool;
	}

	@Override
	public void craft(ItemStack a, ItemStack b, EntityPlayer player, ItemStack hoverStack, int slot)
	{
		ItemStack newA = sort(a, b, true);
		ItemStack newB = sort(a, b, false);

		if (newA != null && newB != null && !newA.isEmpty() && !newB.isEmpty())
		{	
			getCraftingResult(newA, newB, player, PrimitiveRecipe.areStacksEqual(newA, hoverStack) ? true : false, slot);
			
			addItem(player, getResult());
		}
	}

	
	@Override
	public void getCraftingResult(ItemStack a, ItemStack b, EntityPlayer player, boolean isAHoverStack, int slot)
	{
		this.getA().getTransformForStack(a).transformStack(a, player, isAHoverStack, slot);
		this.getB().getTransformForStack(b).transformStack(b, player, !isAHoverStack, slot);
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.getItem().hashCode() + this.result.getMetadata() + (this.result.hasTagCompound() ? this.result.getTagCompound().hashCode() : 0));
		return result;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof PrimitiveRecipe)
		{
			PrimitiveRecipe r = (PrimitiveRecipe) other;
			if(r.a.equals(this.a) && r.b.equals(this.b) && areStacksEqual(result, r.result) && r.registryName.equals(this.registryName))
				return  true;
		}
		
		return false;
	}

	public ItemStack sort(ItemStack a, ItemStack b, boolean getA)
	{
		if (this.getA().test(a))
		{
			if (getA)
				return a;
			else
				return b;
		}

		if (this.getB().test(a))
		{
			if (getA)
				return b;
			else
				return a;
		}

		return null;
	}

	public static int compareStacks(ItemStack a, ItemStack b)
	{
		String aS = a.getItem().getRegistryName().toString();
		String bS = b.getItem().getRegistryName().toString();
		int i = aS.compareTo(bS);

		if (i < 0)
		{
			return -1;
		} else if (i > 0)
		{
			return 1;
		} else
		{
			if (a.getMetadata() < b.getMetadata())
			{
				return -1;
			} else if (b.getMetadata() < a.getMetadata())
			{
				return 1;
			} else
			{
				if (a.hasTagCompound() && !b.hasTagCompound())
					return 1;
				else if (b.hasTagCompound() && !a.hasTagCompound())
					return -1;
				else if (a.hasTagCompound() && b.hasTagCompound())
				{
					int k = a.getTagCompound().toString().compareTo(b.getTagCompound().toString());
					if (k < 0)
						return -1;
					else if (k > 0)
						return 1;
					else
						return 0;
				} else
					return 0;
			}
		}

	}

	public static boolean areStacksEqual(ItemStack one, ItemStack other)
	{
		if (one.getItem() != other.getItem())
		{
			return false;
		} else if (one.getMetadata() != other.getMetadata())
		{
			return false;
		} else if (one.getTagCompound() != other.getTagCompound() )
		{
			return false;
		}
		
		return true;
	}

	public static void addItem(EntityPlayer player, ItemStack stack)
	{
		if (player instanceof EntityPlayerSP)
		{
			PrimitiveCrafting.network.sendToServer(new AddItem(stack));
		}
	}

	@Override
	public String getTier()
	{
		return tier;
	}

	@Override
	public void setTier(String s)
	{
		this.tier = s;
	}

	@Override
	public ResourceLocation getRegistryName()
	{
		return registryName;
	}

	

}
