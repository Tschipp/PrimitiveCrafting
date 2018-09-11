package tschipp.primitivecrafting.client.render.event;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tschipp.primitivecrafting.PrimitiveCrafting;
import tschipp.primitivecrafting.common.crafting.IPrimitiveRecipe;
import tschipp.primitivecrafting.common.crafting.RecipeRegistry;
import tschipp.primitivecrafting.network.Craft;

public class RenderEvents
{

	public static List<IPrimitiveRecipe> cachedRecipes = new ArrayList<IPrimitiveRecipe>();
	public static int index = 0;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiLeftClick(GuiScreenEvent.MouseInputEvent.Pre event)
	{
		GuiScreen gui = event.getGui();
		
		if ((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && Mouse.getEventButton() == 0  && gui instanceof GuiContainer)
		{
			GuiContainer container = (GuiContainer) gui;
			EntityPlayer player = Minecraft.getMinecraft().player;
			Slot slotBelow = container.getSlotUnderMouse();
			ItemStack held = player.inventory.getItemStack();

			if (slotBelow != null && slotBelow.getHasStack() && !slotBelow.getStack().isEmpty() && slotBelow.inventory == player.inventory && !held.isEmpty() )
			{
				if (!cachedRecipes.isEmpty())
				{
					IPrimitiveRecipe recipe = cachedRecipes.get(index);

					if (Mouse.isButtonDown(0))
					{
						recipe.craft(held, slotBelow.getStack(), player);
						PrimitiveCrafting.network.sendToServer(new Craft(slotBelow.getSlotIndex(), recipe));
					}
					event.setCanceled(true);
				}

			}
		}

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyboard(GuiScreenEvent.KeyboardInputEvent.Pre event)
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU))
		{
			int size = cachedRecipes.size();
			if (size > 0)
			{
				index++;
				if (index == size)
				{
					index = 0;
				}
			}
			
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderGui(DrawScreenEvent.Post event)
	{
		GuiScreen gui = event.getGui();
		Minecraft minecraft = Minecraft.getMinecraft();
		
		if (gui instanceof GuiContainer && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) )
		{
			GuiContainer container = (GuiContainer) gui;
			Slot slotBelow = container.getSlotUnderMouse();
			ItemStack held = minecraft.player.inventory.getItemStack();

			if (slotBelow != null && slotBelow.getHasStack() && !slotBelow.getStack().isEmpty() && slotBelow.inventory == minecraft.player.inventory && !held.isEmpty() )
			{
				ItemStack stackUnder = slotBelow.getStack();

				List<IPrimitiveRecipe> recipes;
				if (cachedRecipes.isEmpty())
					recipes = RecipeRegistry.getValidRecipes(held, stackUnder);
				else
					recipes = cachedRecipes;

				if (!recipes.isEmpty() && recipes.get(0).isValid(slotBelow.getStack(), held))
				{
					IPrimitiveRecipe recipe = recipes.get(index);

					cachedRecipes = recipes;

					int desiredLength = 76;
					int spaceLength = minecraft.fontRenderer.getStringWidth(" ");
					
					String s = "";
					
					for(int i = 0; i < 76; i+=spaceLength)
						s += " ";
					
					List<String> tooltip = new ArrayList<String>();
					tooltip.add(s);
					tooltip.add(s);

					GlStateManager.pushMatrix();
					GuiUtils.drawHoveringText(held, tooltip, event.getMouseX(), event.getMouseY(), gui.width, gui.height, -1, minecraft.fontRenderer);

					if(recipes.size() > 1)
					{
						tooltip.clear();
						tooltip.add(I18n.translateToLocal("primitivecrafting.moreoptions"));
						tooltip.add(String.format(I18n.translateToLocal("primitivecrafting.cycle"), TextFormatting.GREEN + "ALT" + TextFormatting.RESET));
						GuiUtils.drawHoveringText(held, tooltip, event.getMouseX(), event.getMouseY() + 30, gui.width, gui.height, -1, minecraft.fontRenderer);

					}
					
					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();
					RenderHelper.enableGUIStandardItemLighting();
					RenderItem render = minecraft.getRenderItem();

					GlStateManager.enableRescaleNormal();
					render.zLevel = 600;

					int x = event.getMouseX() + 11;
					int y = event.getMouseY() - 10;

					ItemStack a;
					ItemStack b;

					if (recipe.getA().test(held))
					{
						a = held.copy();
						a.setCount(recipe.getA().count);
						b = slotBelow.getStack().copy();
						b.setCount(recipe.getB().count);
					} else
					{
						a = slotBelow.getStack().copy();
						a.setCount(recipe.getA().count);
						b = held.copy();
						b.setCount(recipe.getB().count);
					}

					render.renderItemAndEffectIntoGUI(a, x, y);
					render.renderItemOverlayIntoGUI(minecraft.fontRenderer, a, x, y, null);

					render.renderItemAndEffectIntoGUI(b, x + 30, y);
					render.renderItemOverlayIntoGUI(minecraft.fontRenderer, b, x + 30, y, null);

					render.renderItemAndEffectIntoGUI(recipe.getResult(), x + 60, y);
					render.renderItemOverlayIntoGUI(minecraft.fontRenderer, recipe.getResult(), x + 60, y, null);

					RenderHelper.disableStandardItemLighting();
					GlStateManager.popMatrix();

					render.zLevel = 600;

					GlStateManager.disableDepth();
					minecraft.fontRenderer.drawStringWithShadow("+", x + 20, y + 5, -1);
					minecraft.fontRenderer.drawStringWithShadow("=", x + 50, y + 5, -1);
					GlStateManager.enableDepth();
				} else
				{
					index = 0;
					cachedRecipes.clear();
				}

			} else
			{
				index = 0;
				cachedRecipes.clear();
			}
		} else
		{
			index = 0;
			cachedRecipes.clear();
		}
	}
}
