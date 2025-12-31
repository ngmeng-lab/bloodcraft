package com.suzuran_ss.bloodcraft_ss.registry;

import com.suzuran_ss.bloodcraft_ss.bloodcraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, bloodcraft.MODID);

    public static final RegistryObject<Item> IRON_CAULDRON_ITEM = ITEMS.register(
            "iron_cauldron",
            () -> new BlockItem(BlockRegistry.IRON_CAULDRON_BLOCK.get(), new Item.Properties())
    );
}