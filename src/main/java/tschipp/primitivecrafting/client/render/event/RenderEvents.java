package tschipp.primitivecrafting.client.render.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tschipp.primitivecrafting.PrimitiveCrafting;
import tschipp.primitivecrafting.client.keybinds.PrimitiveKeybinds;
import tschipp.primitivecrafting.common.config.PrimitiveConfig;
import tschipp.primitivecrafting.common.crafting.IPrimitiveRecipe;
import tschipp.primitivecrafting.common.crafting.RecipeRegistry;
import tschipp.primitivecrafting.network.Craft;

public class RenderEvents
{

	public static List<IPrimitiveRecipe> cachedRecipes = new ArrayList<IPrimitiveRecipe>();
	public static IPrimitiveRecipe lastRecipe = null;
	public static IPrimitiveRecipe lastCrafted = null;
	public static int index = 0;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiClick(GuiScreenEvent.MouseInputEvent.Pre event)
	{
		GuiScreen gui = event.getGui();

		if (PrimitiveKeybinds.showRecipe.getKeyCode() > 0 && gui instanceof GuiContainer && Keyboard.isKeyDown(PrimitiveKeybinds.showRecipe.getKeyCode()) && Mouse.getEventButton() == 0)
		{
			GuiContainer container = (GuiContainer) gui;
			Slot slotBelow = container.getSlotUnderMouse();
			EntityPlayer player = Minecraft.getMinecraft().player;
			ItemStack held = player.inventory.getItemStack();

			if (slotBelow != null && slotBelow.getHasStack() && !slotBelow.getStack().isEmpty() && slotBelow.inventory == player.inventory && !held.isEmpty())
			{
				if (!cachedRecipes.isEmpty() && cachedRecipes.get(index).equals(lastRecipe))
				{
					IPrimitiveRecipe recipe = lastRecipe;

					if (hasStage(player, recipe.getTier()))
					{
						if (Mouse.isButtonDown(0))
						{
							recipe.craft(held, slotBelow.getStack(), player, held, slotBelow.getSlotIndex());
							PrimitiveCrafting.network.sendToServer(new Craft(slotBelow.getSlotIndex(), recipe));
							lastCrafted = recipe;
						}
						event.setCanceled(true);

						if (PrimitiveKeybinds.craftStack.getKeyCode() > 0 && Keyboard.isKeyDown(PrimitiveKeybinds.craftStack.getKeyCode()))
						{
							while (recipe.isValid(held, slotBelow.getStack()) && lastRecipe.equals(lastCrafted))
							{
								recipe.craft(held, slotBelow.getStack(), player, held, slotBelow.getSlotIndex());
								PrimitiveCrafting.network.sendToServer(new Craft(slotBelow.getSlotIndex(), recipe));
								lastCrafted = recipe;
							}
						}
					}
				}

			}
		}

		lastCrafted = null;

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyboard(GuiScreenEvent.KeyboardInputEvent.Pre event)
	{
		if (PrimitiveKeybinds.cycleRecipes.getKeyCode() > 0 && Keyboard.isKeyDown(PrimitiveKeybinds.cycleRecipes.getKeyCode()))
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

		if (gui instanceof GuiContainer && PrimitiveKeybinds.showRecipe.getKeyCode() > 0 && Keyboard.isKeyDown(PrimitiveKeybinds.showRecipe.getKeyCode()))
		{
			GuiContainer container = (GuiContainer) gui;
			Slot slotBelow = container.getSlotUnderMouse();
			ItemStack held = minecraft.player.inventory.getItemStack();

			if (slotBelow != null && slotBelow.getHasStack() && !slotBelow.getStack().isEmpty() && slotBelow.inventory == minecraft.player.inventory && !held.isEmpty())
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

					if (hasStage(minecraft.player, recipe.getTier()))
					{
						cachedRecipes = recipes;
						lastRecipe = recipe;

						int desiredLength = 76;
						int spaceLength = minecraft.fontRenderer.getStringWidth(" ");

						String s = "";

						for (int i = 0; i < 76; i += spaceLength)
							s += " ";

						List<String> tooltip = new ArrayList<String>();
						tooltip.add(s);
						tooltip.add(s);

						GlStateManager.pushMatrix();
						GuiUtils.drawHoveringText(ItemStack.EMPTY, tooltip, event.getMouseX(), event.getMouseY(), gui.width, gui.height, -1, minecraft.fontRenderer);

						if (recipes.size() > 1)
						{
							tooltip.clear();
							tooltip.add(I18n.translateToLocal("primitivecrafting.moreoptions"));
							tooltip.add(String.format(I18n.translateToLocal("primitivecrafting.cycle"), TextFormatting.GREEN + PrimitiveKeybinds.cycleRecipes.getDisplayName() + TextFormatting.RESET));
							GuiUtils.drawHoveringText(ItemStack.EMPTY, tooltip, event.getMouseX(), event.getMouseY() + 30, gui.width, gui.height, -1, minecraft.fontRenderer);

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
						ItemStack result = recipe.getResult();

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

						if (PrimitiveKeybinds.craftStack.getKeyCode() > 0 && Keyboard.isKeyDown(PrimitiveKeybinds.craftStack.getKeyCode()))
						{
							int amountCrafted = getTimesCrafted(held, stackUnder, recipe);
							a.setCount(a.getCount() * amountCrafted);
							b.setCount(b.getCount() * amountCrafted);
							result.setCount(result.getCount() * amountCrafted);

						}

						render.renderItemAndEffectIntoGUI(a, x, y);
						render.renderItemOverlayIntoGUI(minecraft.fontRenderer, a, x, y, null);

						render.renderItemAndEffectIntoGUI(b, x + 30, y);
						render.renderItemOverlayIntoGUI(minecraft.fontRenderer, b, x + 30, y, null);

						render.renderItemAndEffectIntoGUI(recipe.getResult(), x + 60, y);
						render.renderItemOverlayIntoGUI(minecraft.fontRenderer, result, x + 60, y, null);

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
						lastRecipe = null;
					}
				}

			} else
			{
				index = 0;
				cachedRecipes.clear();
				lastRecipe = null;
			}
		} else
		{
			index = 0;
			cachedRecipes.clear();
			lastRecipe = null;
		}

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event)
	{

		if (event.getGui() instanceof GuiInventory)
		{
			GuiInventory inv = (GuiInventory) event.getGui();
			ContainerPlayer container = (ContainerPlayer) inv.inventorySlots;
			for (int i = 0; i < container.inventorySlots.size(); i++)
			{
				Slot s = container.inventorySlots.get(i);

				if (s instanceof SlotCrafting)
				{
					if (PrimitiveConfig.Settings.disableInventoryCrafting && !hasStage(Minecraft.getMinecraft().player, "inventorycrafting"))
						s.yPos = -1000;
					else
						s.yPos = 28;
				}
			}

		}

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderBackground(GuiContainerEvent.DrawForeground event)
	{
		GuiContainer gui = event.getGuiContainer();
		if (PrimitiveConfig.Settings.disableInventoryCrafting && !hasStage(Minecraft.getMinecraft().player, "inventorycrafting"))
		{
			if (gui instanceof GuiInventory)
			{
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				
				Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(PrimitiveCrafting.MODID + ":textures/gui/locked.png"));
				GlStateManager.color(1, 1, 1, 1);
				Gui.drawModalRectWithCustomSizedTexture(154, 28, 0, 0, 16, 16, 16, 16);

				GlStateManager.popMatrix();
			}
		}
	}

	public static boolean hasStage(EntityPlayer player, String stage)
	{
		if (Loader.isModLoaded("gamestages"))
		{
			if (stage.isEmpty())
				return true;

			try
			{
				Class<?> gameStageHelper = Class.forName("net.darkhax.gamestages.GameStageHelper");
				Class<?> iStageData = Class.forName("net.darkhax.gamestages.data.IStageData");

				Method getPlayerData = ReflectionHelper.findMethod(gameStageHelper, "getPlayerData", null, EntityPlayer.class);
				Method hasStage = ReflectionHelper.findMethod(iStageData, "hasStage", null, String.class);

				Object stageData = getPlayerData.invoke(null, player);
				boolean has = (boolean) hasStage.invoke(stageData, stage);

				return has;
			} catch (Exception e)
			{
				try
				{
					Class<?> playerDataHandler = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler");
					Class<?> iStageData = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler$IStageData");

					Method getStageData = ReflectionHelper.findMethod(playerDataHandler, "getStageData", null, EntityPlayer.class);
					Method hasUnlockedStage = ReflectionHelper.findMethod(iStageData, "hasUnlockedStage", null, String.class);

					Object stageData = getStageData.invoke(null, player);
					boolean has = (boolean) hasUnlockedStage.invoke(stageData, stage);

					return has;
				} catch (Exception ex)
				{
					return false;
				}
			}
		}

		return false;
	}

	public static int getTimesCrafted(ItemStack stackA, ItemStack stackB, IPrimitiveRecipe recipe)
	{
		ItemStack a;
		ItemStack b;

		int aCount = recipe.getA().count;
		int bCount = recipe.getB().count;

		if (recipe.getA().test(stackA))
		{
			a = stackA.copy();
			b = stackB.copy();
		} else
		{
			a = stackB.copy();
			b = stackA.copy();
		}

		int amountA = a.getCount() / aCount;
		int amountB = b.getCount() / bCount;

		if (amountA > amountB)
			return amountB;
		return amountA;
	}

}
