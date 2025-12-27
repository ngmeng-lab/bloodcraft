// src/main/java/com/suzuran_ss/bloodcraft_ss/registry/BlockRegistry.java
package com.suzuran_ss.bloodcraft_ss.registry;

import com.suzuran_ss.bloodcraft_ss.bloodcraft;
import com.suzuran_ss.bloodcraft_ss.blocks.IronCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, bloodcraft.MODID);

    // 只注册 Block
    public static final RegistryObject<Block> IRON_CAULDRON_BLOCK = BLOCKS.register("iron_cauldron", IronCauldronBlock::new);
}