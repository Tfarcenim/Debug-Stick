package com.tfar.debugstick;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
@Mod.EventBusSubscriber(modid = DebugStick.MODID)
@Mod(modid = DebugStick.MODID, name = DebugStick.NAME, version = DebugStick.VERSION)
public class DebugStick
{
    public static final String MODID = "debugstick";
    public static final String NAME = "Debug Stick";
    public static final String VERSION = "@VERSION@";

    private static Logger logger;
    @SidedProxy(clientSide = "com.tfar."+MODID+".ClientProxy", serverSide = "com.tfar."+MODID+".CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Utility.register(event.getRegistry());
    }
}
