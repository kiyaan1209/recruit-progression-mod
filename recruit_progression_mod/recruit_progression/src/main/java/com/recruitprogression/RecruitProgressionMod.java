package com.recruitprogression;

import com.recruitprogression.capability.RecruitCapability;
import com.recruitprogression.events.RecruitEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RecruitProgressionMod.MOD_ID)
public class RecruitProgressionMod {

    public static final String MOD_ID = "recruitprogression";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public RecruitProgressionMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        // Register capability events and game events
        MinecraftForge.EVENT_BUS.register(new RecruitEventHandler());
        MinecraftForge.EVENT_BUS.register(new RecruitCapability.AttachCapabilitiesHandler());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        RecruitCapability.register();
        LOGGER.info("[RecruitProgression] Capability registered.");
    }
}
