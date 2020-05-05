package tschipp.primitivecrafting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import tschipp.primitivecrafting.common.CommonProxy;

@EventBusSubscriber
@Mod(modid = PrimitiveCrafting.MODID, name = PrimitiveCrafting.NAME, version = PrimitiveCrafting.VERSION, dependencies = PrimitiveCrafting.DEPENDENCIES, acceptedMinecraftVersions = PrimitiveCrafting.ACCEPTED_VERSIONS, guiFactory = "tschipp.primitivecrafting.client.gui.GuiFactoryPrimitive", certificateFingerprint = "fd21553434f4905f2f73ea7838147ac4ea07bd88")
public class PrimitiveCrafting
{

	@SidedProxy(clientSide = "tschipp.primitivecrafting.client.ClientProxy", serverSide = "tschipp.primitivecrafting.common.CommonProxy")
	public static CommonProxy proxy;

	// Instance
	@Instance(PrimitiveCrafting.MODID)
	public static PrimitiveCrafting instance;

	public static final String MODID = "primitivecrafting";
	public static final String VERSION = "GRADLE:VERSION";
	public static final String NAME = "Primitive Crafting";
	public static final String ACCEPTED_VERSIONS = "[1.12.2,1.13)";
	public static final String DEPENDENCIES = "required-after:forge@[13.20.1.2386,);before:jei@[4.11.0.212,);after:recipestages";
	public static final Logger LOGGER = LogManager.getFormatterLogger(MODID.toUpperCase());

	public static SimpleNetworkWrapper network;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		PrimitiveCrafting.proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		PrimitiveCrafting.proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		PrimitiveCrafting.proxy.postInit(event);
	}

	@EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        
		LOGGER.error("WARNING! Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with! If you didn't download the file from https://minecraft.curseforge.com/projects/primitive-crafting or through any kind of mod launcher, immediately delete the file and re-download it from https://minecraft.curseforge.com/projects/primitive-crafting");
    }
	
}
	
