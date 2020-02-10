package tschipp.primitivecrafting.client.render.event;

import java.util.ArrayList;
import java.util.HashSet;
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
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tschipp.primitivecrafting.PrimitiveCrafting;
import tschipp.primitivecrafting.client.crafting.CraftingAction;
import tschipp.primitivecrafting.client.keybinds.PrimitiveKeybinds;
import tschipp.primitivecrafting.common.config.PrimitiveConfig;
import tschipp.primitivecrafting.common.crafting.IPrimitiveRecipe;
import tschipp.primitivecrafting.common.crafting.RecipeRegistry;
import tschipp.primitivecrafting.common.helper.StageHelper;

public class RenderEvents
{
	private static CraftingAction craftingAction;
	private static CraftingAction lastCrafted;
	private static boolean crafted = false;
	
	// Cycle
	private static boolean hasSelected = false;
	private static IPrimitiveRecipe nextRecipe = null;
	private static boolean cycle = false;

	// Handles Crafting
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiClick(GuiScreenEvent.MouseInputEvent.Pre event)
	{
		GuiScreen gui = event.getGui();

		if (gui instanceof GuiContainer && isPressed(PrimitiveKeybinds.showRecipe) && Mouse.getEventButton() == 0 && Mouse.getEventButtonState())
		{
			GuiContainer container = (GuiContainer) gui;
			Slot slotBelow = container.getSlotUnderMouse();
			EntityPlayer player = Minecraft.getMinecraft().player;
			ItemStack held = player.inventory.getItemStack();

			if (slotBelow != null && slotBelow.getHasStack() && !slotBelow.getStack().isEmpty() && slotBelow.inventory == player.inventory && !held.isEmpty())
			{
				if (craftingAction != null && craftingAction.getSlot() == slotBelow && !cycle && !hasSelected)
				{
					boolean craftAll = false;

					if (isPressed(PrimitiveKeybinds.craftStack))
						craftAll = true;

					craftingAction.craft(held, slotBelow.getStack(), craftAll, slotBelow.getSlotIndex());
					event.setCanceled(true);
					crafted = true;
					return;
				}
			}
		}

		if (hasSelected || crafted)
		{
			event.setCanceled(true);
			crafted = false;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyboard(GuiScreenEvent.KeyboardInputEvent.Pre event)
	{
		if (isPressed(PrimitiveKeybinds.cycleRecipes))
			cycle = true;
		else
			cycle = false;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderGui(DrawScreenEvent.Post event)
	{
		GuiScreen gui = event.getGui();
		Minecraft minecraft = Minecraft.getMinecraft();
		EntityPlayer player = minecraft.player;

		if (gui instanceof GuiContainer && isPressed(PrimitiveKeybinds.showRecipe))
		{
			GuiContainer container = (GuiContainer) gui;
			Slot slotBelow = container.getSlotUnderMouse();
			ItemStack held = minecraft.player.inventory.getItemStack();

			if (slotBelow != null && slotBelow.getHasStack() && !slotBelow.getStack().isEmpty() && slotBelow.inventory == minecraft.player.inventory && !held.isEmpty())
			{
				ItemStack stackUnder = slotBelow.getStack();
				boolean regenerateRecipe = true;

				if ((craftingAction != null && craftingAction.isSame(held, stackUnder)) || cycle || hasSelected)
				{
					regenerateRecipe = false;
				}

				if (regenerateRecipe)
				{
					if (craftingAction != null)
						lastCrafted = craftingAction;

					List<IPrimitiveRecipe> recipes = RecipeRegistry.getValidRecipes(held, stackUnder, player);
					if (!recipes.isEmpty())
						craftingAction = new CraftingAction(recipes, recipes.get(0), held, stackUnder, slotBelow, lastCrafted);
					else if (!cycle && !hasSelected)
						craftingAction = null;
				}

			} else
			{
				if (!cycle && !hasSelected)
				{
					if (craftingAction != null)
						lastCrafted = craftingAction;
					craftingAction = null;
				}
			}

			if (craftingAction != null)
			{
				if (slotBelow != null && craftingAction.getSlot() == slotBelow)
					hasSelected = false;

				IPrimitiveRecipe recipe = craftingAction.getCurrentRecipe();
				ItemStack stackUnder = craftingAction.getSlot().getStack();

				boolean sizeFlag = craftingAction.getValidRecipes().size() > 1;

				if (!(sizeFlag && cycle))
				{
					int desiredLength = 76;
					int spaceLength = minecraft.fontRenderer.getStringWidth(" ");

					String s = "";

					for (int i = 0; i < 76; i += spaceLength)
						s += " ";

					List<String> tooltip = new ArrayList<String>();
					tooltip.add(s);
					tooltip.add(s);

					int renderX = hasSelected ? container.getGuiLeft() + craftingAction.getSlot().xPos + 8 : event.getMouseX();
					int renderY = hasSelected ? container.getGuiTop() + craftingAction.getSlot().yPos + 8 : event.getMouseY();

					GlStateManager.pushMatrix();
					GuiUtils.drawHoveringText(ItemStack.EMPTY, tooltip, renderX, renderY, gui.width, gui.height, -1, minecraft.fontRenderer);

					if (sizeFlag)
					{
						tooltip.clear();
						tooltip.add(I18n.translateToLocal("primitivecrafting.moreoptions"));
						tooltip.add(String.format(I18n.translateToLocal("primitivecrafting.cycle"), TextFormatting.GREEN + PrimitiveKeybinds.cycleRecipes.getDisplayName() + TextFormatting.RESET));

						GuiUtils.drawHoveringText(ItemStack.EMPTY, tooltip, renderX, renderY + 30, gui.width, gui.height, -1, minecraft.fontRenderer);
					}

					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();
					RenderHelper.enableGUIStandardItemLighting();
					RenderItem render = minecraft.getRenderItem();

					GlStateManager.enableRescaleNormal();
					render.zLevel = 600;

					int x = renderX + 11;
					int y = renderY - 10;

					ItemStack a;
					ItemStack b;
					ItemStack result = recipe.getResult();

					if (recipe.getA().test(held))
					{
						a = held.copy();
						a.setCount(recipe.getA().count);
						b = stackUnder.copy();
						b.setCount(recipe.getB().count);
					} else
					{
						a = stackUnder.copy();
						a.setCount(recipe.getA().count);
						b = held.copy();
						b.setCount(recipe.getB().count);
					}

					if (isPressed(PrimitiveKeybinds.craftStack))
					{
						int[] amountCrafted = CraftingAction.getTimesCrafted(held, stackUnder, recipe);
						a.setCount(amountCrafted[1]);
						b.setCount(amountCrafted[2]);
						result.setCount(result.getCount() * amountCrafted[0]);

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
				}

			}

			if (craftingAction != null && cycle)
			{
				if (craftingAction.getValidRecipes().size() > 1)
				{
					GlStateManager.pushMatrix();
					GlStateManager.translate(0, 0, 400);
					GlStateManager.enableAlpha();
					GlStateManager.enableBlend();

					Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(PrimitiveCrafting.MODID + ":textures/gui/blur.png"));
					GlStateManager.color(1, 1, 1, 1);
					Gui.drawModalRectWithCustomSizedTexture(container.getGuiLeft() + craftingAction.getSlot().xPos - 40, container.getGuiTop() + craftingAction.getSlot().yPos - 40, 0, 0, 96, 96, 96, 96);
					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();

					GlStateManager.translate(0, 0, 820);
					RenderHelper.enableGUIStandardItemLighting();

					double angle = (Math.PI * 2) / (craftingAction.getValidRecipes().size() - 1);

					RenderItem render = minecraft.getRenderItem();

					int angleIndex = 0;
					hasSelected = true;
					boolean anyInBounds = false;

					for (int i = 0; i < craftingAction.getValidRecipes().size(); i++)
					{
						IPrimitiveRecipe recipe = craftingAction.getValidRecipes().get(i);

						if (recipe == craftingAction.getCurrentRecipe())
						{
							continue;
						}

						int rotX = (int) (Math.cos(angle * angleIndex) * 35 + craftingAction.getSlot().xPos);
						int rotY = (int) (Math.sin(angle * angleIndex) * 35 + craftingAction.getSlot().yPos);

						if (isInBounds(event.getMouseX(), event.getMouseY(), container.getGuiLeft() + rotX, container.getGuiTop() + rotY))
						{
							rotX = (int) (Math.cos(angle * angleIndex) * 43 + craftingAction.getSlot().xPos);
							rotY = (int) (Math.sin(angle * angleIndex) * 43 + craftingAction.getSlot().yPos);
							nextRecipe = recipe;
							anyInBounds = true;
						}

						render.renderItemAndEffectIntoGUI(recipe.getResult(), container.getGuiLeft() + rotX, container.getGuiTop() + rotY);

						angleIndex++;
					}

					int extra = isInBounds(event.getMouseX(), event.getMouseY(), container.getGuiLeft() + craftingAction.getSlot().xPos, container.getGuiTop() + craftingAction.getSlot().yPos) ? 5 : 0;

					render.renderItemAndEffectIntoGUI(craftingAction.getCurrentRecipe().getResult(), container.getGuiLeft() + craftingAction.getSlot().xPos, container.getGuiTop() + craftingAction.getSlot().yPos - extra);

					if (!anyInBounds)
						nextRecipe = craftingAction.getCurrentRecipe();

					RenderHelper.disableStandardItemLighting();
					GlStateManager.popMatrix();
				}
			}

			if (hasSelected && !cycle)
			{
				if (craftingAction != null)
					craftingAction.setCurrentRecipe(nextRecipe);
			}
		} else
		{
			if (craftingAction != null)
				lastCrafted = craftingAction;
			craftingAction = null;
			hasSelected = false;
			nextRecipe = null;
		}

	}

	private static boolean isPressed(KeyBinding key)
	{
		return key.getKeyCode() > 0 && Keyboard.isKeyDown(key.getKeyCode());
	}

	private static boolean isInBounds(int mouseX, int mouseY, int x, int y)
	{
		return mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16;
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
					if (PrimitiveConfig.Settings.disableInventoryCrafting && !StageHelper.hasStage(Minecraft.getMinecraft().player, "inventorycrafting"))
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
		if (PrimitiveConfig.Settings.disableInventoryCrafting && !StageHelper.hasStage(Minecraft.getMinecraft().player, "inventorycrafting"))
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

}
