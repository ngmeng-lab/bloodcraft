// src/main/java/com/suzuran_ss/bloodcraft_ss/registry/ModItems.java

package com.suzuran_ss.bloodcraft_ss.registry;

import com.suzuran_ss.bloodcraft_ss.items.CustomScytheItem; // 导入新的自定义类
import com.suzuran_ss.bloodcraft_ss.items.ExoticMatterItem;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.suzuran_ss.bloodcraft_ss.bloodcraft.MODID;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

//-------------------------------物品类注册------------------------//

    public static final RegistryObject<Item> EXOTIC_MATTER = ITEMS.register("exotic_matter",
            () -> new ExoticMatterItem(new Item.Properties()));
    public static final RegistryObject<Item> REDBOOK = ITEMS.register("redbook",
            () -> new ExoticMatterItem(new Item.Properties()));

//---------------------------------------------------------------//

//--------------------------------镰刀类注册-----------------------//

    public static final RegistryObject<Item> WOOD_SCYTHE =
            ITEMS.register("wood_scythe",
                    () -> new CustomScytheItem(//自定义类
                            Tiers.WOOD,
                            3, // 额外攻击力
                            -2.8f,      // 攻速
                            0.0f,
                            new Item.Properties()
                                    .durability(64)     //耐久度
                    )
            );

    public static final RegistryObject<Item> STONE_SCYTHE =
            ITEMS.register("stone_scythe",
                    () -> new CustomScytheItem(
                            Tiers.STONE,
                            6,
                            -2.8f,
                            0.0f,
                            new Item.Properties()
                                    .durability(128)
                    )
            );

    public static final RegistryObject<Item> IRON_SCYTHE =
            ITEMS.register("iron_scythe",
                    () -> new CustomScytheItem(
                            Tiers.IRON,
                            7, // 基础攻击力
                            -2.4f, // 攻速
                            0.10f, // 基于血量的额外伤害百分比
                            new Item.Properties()
                                    .durability(256) // 耐久度
                    )
            );

    //------------------------------------------------------------------------//
}