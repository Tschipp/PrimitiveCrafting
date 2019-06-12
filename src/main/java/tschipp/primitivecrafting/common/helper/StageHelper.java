package tschipp.primitivecrafting.common.helper;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class StageHelper
{
	private static Class<?> gameStageHelper;
	private static Class<?> iStageHelper;

	private static Method getPlayerData;
	private static Method hasStage;

	private static boolean usesNewVersion = true;

	private static Class<?> playerDataHandler;
	private static Class<?> iStageData;

	private static Method getStageData;
	private static Method hasUnlockedStage;

	static
	{
		if (Loader.isModLoaded("gamestages"))
		{
			try
			{
				gameStageHelper = Class.forName("net.darkhax.gamestages.GameStageHelper");
				iStageData = Class.forName("net.darkhax.gamestages.data.IStageData");

				getPlayerData = ReflectionHelper.findMethod(gameStageHelper, "getPlayerData", null, EntityPlayer.class);
				hasStage = ReflectionHelper.findMethod(iStageData, "hasStage", null, String.class);

			} catch (Exception e)
			{
				try
				{
					playerDataHandler = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler");
					iStageData = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler$IStageData");

					getStageData = ReflectionHelper.findMethod(playerDataHandler, "getStageData", null, EntityPlayer.class);
					hasUnlockedStage = ReflectionHelper.findMethod(iStageData, "hasUnlockedStage", null, String.class);

					usesNewVersion = false;

				} catch (Exception ex)
				{
				}
			}
		}
	}

	public static boolean hasStage(EntityPlayer player, String stage)
	{
		if (Loader.isModLoaded("gamestages"))
		{
			if (stage.isEmpty())
				return true;

			if (usesNewVersion)
			{
				try
				{
					Object stageData = getPlayerData.invoke(null, player);
					boolean has = (boolean) hasStage.invoke(stageData, stage);

					return has;
				} catch (Exception e)
				{
					return true;
				}
			} else
			{
				try
				{
					Object stageData = getStageData.invoke(null, player);
					boolean has = (boolean) hasUnlockedStage.invoke(stageData, stage);

					return has;
				} catch (Exception e)
				{
					return true;
				}
			}
		}

		return true;
	}

}
