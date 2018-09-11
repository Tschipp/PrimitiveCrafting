package tschipp.primitivecrafting.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tschipp.primitivecrafting.client.render.event.RenderEvents;
import tschipp.primitivecrafting.common.CommonProxy;

public class ClientProxy extends CommonProxy
{
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(new RenderEvents());
	}

	public void init(FMLInitializationEvent event)
	{
		super.init(event);

	}

	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);

	}
}
