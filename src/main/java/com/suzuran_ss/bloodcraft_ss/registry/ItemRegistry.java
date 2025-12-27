// src/main/java/com/suzuran_ss/bloodcraft_ss/registry/ItemRegistry.java
package com.suzuran_ss.bloodcraft_ss.registry;

import com.suzuran_ss.bloodcraft_ss.bloodcraft;
import com.suzuran_ss.bloodcraft_ss.registry.BlockRegistry; // 导入 BlockRegistry 以便引用方块
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, bloodcraft.MODID);

    // 注册 BlockItem，使用与 Block 相同的名称 "iron_cauldron"
    // BlockRegistry.IRON_CAULDRON_BLOCK.get() 获取已注册的方块实例
    public static final RegistryObject<Item> IRON_CAULDRON_ITEM = ITEMS.register(
            "iron_cauldron", // 这个名称与 BlockRegistry 中的方块名称相同
            () -> new BlockItem(BlockRegistry.IRON_CAULDRON_BLOCK.get(), new Item.Properties())
    );
}