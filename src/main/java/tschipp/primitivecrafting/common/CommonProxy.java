package tschipp.primitivecrafting.common;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import tschipp.primitivecrafting.PrimitiveCrafting;
import tschipp.primitivecrafting.common.event.CommonEvents;
import tschipp.primitivecrafting.network.AddItem;
import tschipp.primitivecrafting.network.Craft;

public class CommonProxy
{
	public void preInit(FMLPreInitializationEvent event)
	{

		PrimitiveCrafting.network = NetworkRegistry.INSTANCE.newSimpleChannel("PrimitiveCrafting");

		PrimitiveCrafting.network.registerMessage(Craft.class, Craft.class, 0, Side.SERVER);
		PrimitiveCrafting.network.registerMessage(AddItem.class, AddItem.class, 1, Side.SERVER);
		
		MinecraftForge.EVENT_BUS.register(new CommonEvents());

	}

	public void init(FMLInitializationEvent event)
	{

	}

	public void postInit(FMLPostInitializationEvent event)
	{
	}
}
