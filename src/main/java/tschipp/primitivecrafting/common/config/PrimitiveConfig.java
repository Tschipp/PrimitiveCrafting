package tschipp.primitivecrafting.common.config;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.Optional;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tschipp.primitivecrafting.PrimitiveCrafting;
import tschipp.primitivecrafting.common.helper.ReflectionUtil;

@Config(modid = PrimitiveCrafting.MODID)
public class PrimitiveConfig
{

	@Config.LangKey(PrimitiveCrafting.MODID)
	@Config.Comment("General Mod Settings")
	public static Configs.Settings Settings = new Configs.Settings();
	
	@Config.LangKey(PrimitiveCrafting.MODID)
	@Config.Comment("Blacklist for Recipes")
	public static Configs.Blacklist Blacklist = new Configs.Blacklist();
	
	@Config.LangKey(PrimitiveCrafting.MODID)
	@Config.Comment("Whitelist for Recipes")
	public static Configs.WhiteList Whitelist = new Configs.WhiteList();
	
	@Mod.EventBusSubscriber
	public static class EventHandler
	{
		/**
		 * The {@link ConfigManager#CONFIGS} getter.
		 */
		private static final MethodHandle CONFIGS_GETTER = ReflectionUtil.findFieldGetter(ConfigManager.class, "CONFIGS");

		/**
		 * The {@link Configuration} instance.
		 */
		private static Configuration configuration;

		/**
		 * Get the {@link Configuration} instance from {@link ConfigManager}.
		 * <p>
		 * TODO: Use a less hackish method of getting the
		 * {@link Configuration}/{@link IConfigElement}s when possible.
		 *
		 * @return The Configuration instance
		 */
		public static Configuration getConfiguration()
		{
			if (EventHandler.configuration == null)
				try
				{
					final String fileName = PrimitiveCrafting.MODID + ".cfg";

					@SuppressWarnings("unchecked")
					final Map<String, Configuration> configsMap = (Map<String, Configuration>) EventHandler.CONFIGS_GETTER.invokeExact();

					final Optional<Map.Entry<String, Configuration>> entryOptional = configsMap.entrySet().stream().filter(entry -> fileName.equals(new File(entry.getKey()).getName())).findFirst();

					entryOptional.ifPresent(stringConfigurationEntry -> EventHandler.configuration = stringConfigurationEntry.getValue());
				}
				catch (Throwable throwable)
				{
					throwable.printStackTrace();
				}

			return EventHandler.configuration;
		}

		/**
		 * Inject the new values and save to the config file when the config has
		 * been changed from the GUI.
		 *
		 * @param event
		 *            The event
		 */
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if (event.getModID().equals(PrimitiveCrafting.MODID))
				ConfigManager.load(PrimitiveCrafting.MODID, Config.Type.INSTANCE);

		}

	}

}
