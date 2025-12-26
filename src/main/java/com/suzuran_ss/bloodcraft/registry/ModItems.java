package com.suzuran_ss.bloodcraft.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.suzuran_ss.bloodcraft.bloodcraft.MODID;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> WOOD_SCYTHE =
            ITEMS.register("wood_scythe",
                    () -> new SwordItem(
                            Tiers.WOOD,
                            2,          // 额外攻击力（基础木剑是 3，这里更低一点）
                            -2.8f,      // 攻速（镰刀偏慢）
                            new Item.Properties()
                    )
            );
}
