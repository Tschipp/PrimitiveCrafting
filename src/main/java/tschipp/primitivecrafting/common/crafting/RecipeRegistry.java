package tschipp.primitivecrafting.common.crafting;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import tschipp.primitivecrafting.common.config.PrimitiveConfig;
import tschipp.primitivecrafting.common.helper.ListHandler;
import tschipp.primitivecrafting.common.helper.StageHelper;

public class RecipeRegistry
{
	private static Set<IPrimitiveRecipe> registry = new HashSet<IPrimitiveRecipe>();
	private static Map<Integer, IPrimitiveRecipe> hashRegistry = new HashMap<Integer, IPrimitiveRecipe>();
	private static HashMap<ResourceLocation, IPrimitiveRecipe> resourceRegistry = new HashMap<ResourceLocation, IPrimitiveRecipe>();

	public static void registerRecipe(IPrimitiveRecipe recipe)
	{
		registry.add(recipe);
		hashRegistry.put(recipe.hashCode(), recipe);
		resourceRegistry.put(recipe.getRegistryName(), recipe);
	}

	public static void registerRecipe(ItemStack a, ItemStack b, ItemStack result, ResourceLocation loc)
	{
		IPrimitiveRecipe recipe = new PrimitiveRecipe(result, get(a), get(b), loc);
		registerRecipe(recipe);
	}

	public static void registerRecipe(Ingredient a, Ingredient b, ItemStack result, ResourceLocation loc)
	{
		IPrimitiveRecipe recipe = new PrimitiveRecipe(result, new PrimitiveIngredient(a, 1), new PrimitiveIngredient(b, 1), loc);
		registerRecipe(recipe);
	}

	public static void registerRecipe(PrimitiveIngredient a, PrimitiveIngredient b, ItemStack result, ResourceLocation loc)
	{
		IPrimitiveRecipe recipe = new PrimitiveRecipe(result, a, b, loc);
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
				remove(r);
			}
		}
	}
	
	public static void remove(IPrimitiveRecipe recipe)
	{
		registry.remove(recipe);
		hashRegistry.remove(recipe.hashCode());
		resourceRegistry.remove(recipe.getRegistryName());
	}

	public static PrimitiveIngredient get(ItemStack stack)
	{
		return new PrimitiveIngredient(Ingredient.fromStacks(stack), stack.getCount());
	}

	public static PrimitiveIngredient get(Ingredient ing)
	{
		return new PrimitiveIngredient(ing, 1);
	}

	public static List<IPrimitiveRecipe> getValidRecipes(ItemStack a, ItemStack b, EntityPlayer player)
	{
		List<IPrimitiveRecipe> valids = new ArrayList<IPrimitiveRecipe>();

		for (IPrimitiveRecipe r : registry)
		{
			if (r.isValid(a, b) && StageHelper.hasStage(player, r.getTier()))
				valids.add(r);
		}

		valids.sort((r1, r2) -> r1.getResult().getItem().getRegistryName().toString().compareTo(r2.getResult().getItem().getRegistryName().toString()));

		return valids;
	}
	
	public static IPrimitiveRecipe getRecipe(ResourceLocation name)
	{
		return resourceRegistry.get(name);
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
				if (PrimitiveConfig.Settings.useWhitelist ? !ListHandler.isAllowed(recipe) : ListHandler.isForbidden(recipe))
					continue;

				NonNullList<Ingredient> ingredients = recipe.getIngredients();

//				ItemStack output = recipe.getRecipeOutput();
//				if(!output.isEmpty() && output.getItem() == Item.getByNameOrId("minecraft:bed") && output.getItemDamage() == 0)
//					System.out.println("OOF");
				
				
				if (ingredients.size() == 2)
				{
					if (!recipe.getRecipeOutput().isEmpty())
						registerRecipe(ingredients.get(0), ingredients.get(1), recipe.getRecipeOutput(), recipe.getRegistryName());
				} else if (ingredients.size() > 1)
				{
					if (PrimitiveConfig.Settings.recipesWithMultipleIngredients)
					{
						List<PrimitiveIngredient> sameIngredients = new ArrayList<PrimitiveIngredient>();
						for (Ingredient i : ingredients)
						{
							if (i.getMatchingStacks().length == 0)
								continue;

							if (sameIngredients.isEmpty())
							{
								sameIngredients.add(new PrimitiveIngredient(i, 1, false));
								continue;
							}

							boolean newIngredient = true;

							for (int k = 0; k < sameIngredients.size(); k++)
							{
								PrimitiveIngredient pI = sameIngredients.get(k);
								if (areIngredientsEqual(pI.ingredient, i))
								{
									pI.count++;
									newIngredient = false;
									break;
								}
							}

							if (newIngredient)
								sameIngredients.add(new PrimitiveIngredient(i, 1));
						}

						for (PrimitiveIngredient ing : sameIngredients)
							ing.initTransformData();

						if (sameIngredients.size() == 2)
						{
							if (!recipe.getRecipeOutput().isEmpty())
								registerRecipe(sameIngredients.get(0), sameIngredients.get(1), recipe.getRecipeOutput(), recipe.getRegistryName());
						} else if (sameIngredients.size() == 1)
						{
							int amount1 = sameIngredients.get(0).count / 2;
							int amount2 = sameIngredients.get(0).count - amount1;

							if (!recipe.getRecipeOutput().isEmpty())
								registerRecipe(new PrimitiveIngredient(sameIngredients.get(0).ingredient, amount1), new PrimitiveIngredient(sameIngredients.get(0).ingredient, amount2), recipe.getRecipeOutput(), recipe.getRegistryName());
						}
					}

				}
			}
		}
	}

	public static List<IPrimitiveRecipe> getRecipes()
	{
		return new ArrayList<IPrimitiveRecipe>(registry);
	}

	public static boolean areIngredientsEqual(Ingredient a, Ingredient b)
	{
		ItemStack[] thisStack = a.getMatchingStacks();
		ItemStack[] otherStack = b.getMatchingStacks();

		boolean equal = true;

		if (thisStack.length != otherStack.length)
			return false;

		for (int i = 0; i < thisStack.length; i++)
		{
			if (!PrimitiveRecipe.areStacksEqual(thisStack[i], otherStack[i]))
				equal = false;
		}

		return equal;
	}

	public static String getTierIfStaged(IRecipe recipe)
	{

		if (Loader.isModLoaded("recipestages"))
		{
			try
			{
				Class clazz = Class.forName("com.blamejared.recipestages.recipes.RecipeStage");

				if (clazz.isInstance(recipe))
				{
					Method getTier = ReflectionHelper.findMethod(clazz, "getTier", null);
					String tier = (String) getTier.invoke(recipe);

					return tier;
				}

			} catch (Exception e)
			{
				return "";
			}
		}

		return "";
	}

	public static void initStagedRecipes()
	{
		if (Loader.isModLoaded("recipestages"))
		{
			for (IPrimitiveRecipe r : registry)
			{
				if(!r.getTier().isEmpty())
					continue;
					
				IRecipe parent = ForgeRegistries.RECIPES.getValue(r.getRegistryName());
				if (parent == null)
					continue;
				
				String tier = getTierIfStaged(parent);
				
				r.setTier(tier);
			}
		}
	}
}
