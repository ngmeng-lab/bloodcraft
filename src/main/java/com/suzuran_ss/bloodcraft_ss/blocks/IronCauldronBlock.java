package com.suzuran_ss.bloodcraft_ss.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class IronCauldronBlock extends Block {
    public IronCauldronBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.0f, 6.0f)
                .requiresCorrectToolForDrops()
        );
    }
}