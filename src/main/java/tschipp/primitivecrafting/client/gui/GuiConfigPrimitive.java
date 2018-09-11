package tschipp.primitivecrafting.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import tschipp.primitivecrafting.PrimitiveCrafting;
import tschipp.primitivecrafting.common.config.PrimitiveConfig;

public class GuiConfigPrimitive extends GuiConfig
{
	private static final String LANG_PREFIX = PrimitiveCrafting.MODID + ".category.";

	public GuiConfigPrimitive(GuiScreen parent)
	{
		super(parent, getConfigElements(), PrimitiveCrafting.MODID, false, false, "Primitive Crafting Configuration");
	}

	private static List<IConfigElement> getConfigElements()
	{

		final Configuration configuration = PrimitiveConfig.EventHandler.getConfiguration();

		final ConfigCategory topLevelCategory = configuration.getCategory(Configuration.CATEGORY_GENERAL);
		topLevelCategory.getChildren().forEach(configCategory -> configCategory.setLanguageKey(GuiConfigPrimitive.LANG_PREFIX + configCategory.getName()));

		return new ConfigElement(topLevelCategory).getChildElements();
	}

	@Override
	public void initGui()
	{
		super.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		super.actionPerformed(button);
	}
}
