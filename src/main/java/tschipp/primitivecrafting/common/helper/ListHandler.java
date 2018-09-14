package tschipp.primitivecrafting.common.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.crafting.IRecipe;
import tschipp.primitivecrafting.common.config.PrimitiveConfig;

public class ListHandler
{
	public static List<String> FORBIDDEN_RECIPES;
	public static List<String> ALLOWED_RECIPES;

	public static boolean isForbidden(IRecipe recipe)
	{
		String name = recipe.getRegistryName().toString();
		
		if(name == null)
			return false;
		
		if (FORBIDDEN_RECIPES.contains(name))
			return true;
		else
		{
			boolean contains = false;
			for (String s : FORBIDDEN_RECIPES)
			{
				if (s.contains("*"))
				{
					if(name.contains(s.replace("*", "")))
						contains = true;
				}
			}
			
			return contains;
		}
	}

	public static boolean isAllowed(IRecipe recipe)
	{
		String name = recipe.getRegistryName().toString();
		
		if(name == null)
			return true;
		
		if (ALLOWED_RECIPES.contains(name))
			return true;
		else
		{
			boolean contains = false;
			for (String s : ALLOWED_RECIPES)
			{
				if (s.contains("*"))
				{
					if(name.contains(s.replace("*", "")))
						contains = true;
				}
			}
			return contains;
		}

	}

	public static void initFilters()
	{
		String[] forbidden = PrimitiveConfig.Blacklist.forbiddenRecipes;
		FORBIDDEN_RECIPES = new ArrayList<String>();

		for (int i = 0; i < forbidden.length; i++)
		{
			FORBIDDEN_RECIPES.add(forbidden[i]);
		}

		String[] allowedRecipes = PrimitiveConfig.Whitelist.allowedRecipes;
		ALLOWED_RECIPES = new ArrayList<String>();
		for (int i = 0; i < allowedRecipes.length; i++)
		{
			ALLOWED_RECIPES.add(allowedRecipes[i]);
		}
	}

}
