package tschipp.primitivecrafting.client.keybinds;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PrimitiveKeybinds
{

	public static KeyBinding cycleRecipes;
	public static KeyBinding showRecipe;
	public static KeyBinding craftStack;
	public static KeyBinding craftItem;


	@SideOnly(Side.CLIENT)
	public static void init()
	{
		showRecipe = new KeyBinding("key.primitive.showrecipe.desc", Keyboard.KEY_LSHIFT, "primitivecrafting.title");
		cycleRecipes = new KeyBinding("key.primitive.cyclerecipes.desc", Keyboard.KEY_LMENU, "primitivecrafting.title");
		craftStack = new KeyBinding("key.primitive.craftstack.desc", Keyboard.KEY_SPACE, "primitivecrafting.title");

		showRecipe.setKeyConflictContext(KeyConflictContext.GUI);
		cycleRecipes.setKeyConflictContext(KeyConflictContext.GUI);
		craftStack.setKeyConflictContext(KeyConflictContext.GUI);

		ClientRegistry.registerKeyBinding(cycleRecipes);
		ClientRegistry.registerKeyBinding(showRecipe);
		ClientRegistry.registerKeyBinding(craftStack);

	}
	
}
