package tschipp.primitivecrafting.common.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreIngredient;
import tschipp.primitivecrafting.common.config.PrimitiveConfig;

public class RecipeRegistry
{
	private static Set<IPrimitiveRecipe> registry = new HashSet<IPrimitiveRecipe>();
	private static Map<Integer, IPrimitiveRecipe> hashRegistry = new HashMap<Integer, IPrimitiveRecipe>();

	public static void registerRecipe(IPrimitiveRecipe recipe)
	{
		registry.add(recipe);

		hashRegistry.put(recipe.hashCode(), recipe);
	}

	public static void registerRecipe(ItemStack a, ItemStack b, ItemStack result)
	{
		IPrimitiveRecipe recipe = new PrimitiveRecipe(result, get(a), get(b));
		registerRecipe(recipe);
	}

	public static void registerRecipe(Ingredient a, Ingredient b, ItemStack result)
	{
		IPrimitiveRecipe recipe = new PrimitiveRecipe(result, new PrimitiveIngredient(a, 1), new PrimitiveIngredient(b, 1));
		registerRecipe(recipe);
	}

	public static void registerRecipe(PrimitiveIngredient a, PrimitiveIngredient b, ItemStack result)
	{
		IPrimitiveRecipe recipe = new PrimitiveRecipe(result, a, b);
		registerRecipe(recipe);
	}

	public static void removeRecipe(PrimitiveIngredient a, PrimitiveIngredient b, ItemStack result)
	{
		List<IPrimitiveRecipe> recipes = new ArrayList<IPrimitiveRecipe>(registry);
		for (int i = 0; i < recipes.size(); i++)
		{
			IPrimitiveRecipe r = recipes.get(i);
			if (((r.getA().equals(a) && r.getB().equals(b)) || (r.getA().equals(b) && r.getB().equals(a))) && result.areItemStacksEqual(result, r.getResult()))
			{
				registry.remove(r);
			}
		}
	}

	public static PrimitiveIngredient get(ItemStack stack)
	{
		return new PrimitiveIngredient(Ingredient.fromStacks(stack), stack.getCount());
	}

	
	public static List<IPrimitiveRecipe> getValidRecipes(ItemStack a, ItemStack b)
	{
		List<IPrimitiveRecipe> valids = new ArrayList<IPrimitiveRecipe>();

		for (IPrimitiveRecipe r : registry)
		{
			if (r.isValid(a, b))
				valids.add(r);
		}

		valids.sort((r1, r2) -> r1.getResult().getItem().getRegistryName().toString().compareTo(r2.getResult().getItem().getRegistryName().toString()));

		return valids;
	}

	@Nullable
	public static IPrimitiveRecipe getFromHash(int hash)
	{
		return hashRegistry.get(hash);
	}

	public static void regRecipes()
	{
		if (PrimitiveConfig.Settings.useDefaultCraftingRecipes)
		{
			for (IRecipe recipe : ForgeRegistries.RECIPES)
			{
				NonNullList<Ingredient> ingredients = recipe.getIngredients();
				if (ingredients.size() == 2)
				{
					registerRecipe(ingredients.get(0), ingredients.get(1), recipe.getRecipeOutput());
				}
			}
		}
	}

	public static List<IPrimitiveRecipe> getRecipes()
	{
		return new ArrayList<IPrimitiveRecipe>(registry);
	}
}
