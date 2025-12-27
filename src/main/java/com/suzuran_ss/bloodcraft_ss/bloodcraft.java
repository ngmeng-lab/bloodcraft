package com.suzuran_ss.bloodcraft_ss;

import com.mojang.logging.LogUtils;
import com.suzuran_ss.bloodcraft_ss.registry.BlockRegistry;
import com.suzuran_ss.bloodcraft_ss.registry.ItemRegistry;
import com.suzuran_ss.bloodcraft_ss.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(bloodcraft.MODID)
public class bloodcraft {

    public static final String MODID = "bloodcraft_ss";
    private static final Logger LOGGER = LogUtils.getLogger();

    public bloodcraft() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // ========= 注册内容 ========

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockRegistry.BLOCKS.register(eventBus);
        ItemRegistry.ITEMS.register(eventBus);
        MinecraftForge.EVENT_BUS.register(this);
        //------------注册内容------------------//


        // 物品
        ModItems.ITEMS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                Config.SPEC
        );
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.WOOD_SCYTHE.get());
            event.accept(ModItems.STONE_SCYTHE.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    // ===== Client-only =====
    @Mod.EventBusSubscriber(
            modid = MODID,
            bus = Mod.EventBusSubscriber.Bus.MOD,
            value = Dist.CLIENT
    )
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
        }
    }
}

