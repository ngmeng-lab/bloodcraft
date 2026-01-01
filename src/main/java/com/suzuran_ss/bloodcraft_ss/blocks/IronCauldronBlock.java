package com.suzuran_ss.bloodcraft_ss.blocks;

import com.suzuran_ss.bloodcraft_ss.menu.IronCauldronMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class IronCauldronBlock extends Block {

    private static final Component CONTAINER_TITLE =
            Component.translatable("container.crafting");

    public IronCauldronBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.0f, 6.0f)
                .requiresCorrectToolForDrops()
        );
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        if (level.isClientSide) return InteractionResult.SUCCESS;

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) ->
                        new IronCauldronMenu(id, inv, ContainerLevelAccess.create(level, pos)),
                Component.translatable("container.bloodcraft_ss.iron_cauldron")
        ));

        return InteractionResult.CONSUME;
    }
}
