package tschipp.primitivecrafting.common.helper;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class StageHelper
{

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
	
}
