package com.suzuran_ss.bloodcraft_ss.menu;

import com.suzuran_ss.bloodcraft_ss.menu.slot.IronCauldronResultSlot;
import com.suzuran_ss.bloodcraft_ss.recipe.ModRecipes;
import com.suzuran_ss.bloodcraft_ss.registry.BlockRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class IronCauldronMenu extends AbstractContainerMenu {

    /* ===== 3x3 输入 ===== */
    private final Container craftMatrix = new SimpleContainer(9);

    /* ===== 输出 ===== */
    private final ResultContainer resultContainer = new ResultContainer();

    private final ContainerLevelAccess access;
    private final Level level;

    public IronCauldronMenu(int id, Inventory playerInv, ContainerLevelAccess access) {
        super(ModMenus.IRON_CAULDRON.get(), id);
        this.access = access;
        this.level = playerInv.player.level();

        /* ===== 输出槽 ===== */
        this.addSlot(new IronCauldronResultSlot(
                playerInv.player,
                craftMatrix,
                resultContainer,
                0,
                124, 35
        ));

        /* ===== 3x3 输入槽 ===== */
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new Slot(
                        craftMatrix,
                        col + row * 3,
                        30 + col * 18,
                        17 + row * 18
                ));
            }
        }

        /* ===== 玩家背包 ===== */
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(
                        playerInv,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18
                ));
            }
        }

        /* ===== 快捷栏 ===== */
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(
                    playerInv,
                    col,
                    8 + col * 18,
                    142
            ));
        }
    }

    /* ===== Shift 点击（暂不支持） ===== */
    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    /* ================================================= */
    /* ================= 合成结果计算 ================= */
    /* ================================================= */

    @Override
    public void slotsChanged(Container container) {
        access.execute((level, pos) -> {
            if (!level.isClientSide) {
                var recipeOpt = level.getRecipeManager()
                        .getRecipeFor(ModRecipes.IRON_CAULDRON_TYPE.get(), craftMatrix, level);

                if (recipeOpt.isPresent()) {
                    ItemStack result = recipeOpt.get()
                            .assemble(craftMatrix, level.registryAccess());
                    resultContainer.setItem(0, result);
                } else {
                    resultContainer.setItem(0, ItemStack.EMPTY);
                }
            }
        });
    }

    /* ================================================= */
    /* ================= 关闭 GUI 行为 ================= */
    /* ================================================= */

    @Override
    public void removed(Player player) {
        super.removed(player);

        // 只在服务端执行
        if (!player.level().isClientSide) {
            // 把 3x3 输入槽里的物品全部丢到地上
            this.clearContainer(player, this.craftMatrix);
        }
    }

    /* ===== 是否仍然有效 ===== */
    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, BlockRegistry.IRON_CAULDRON_BLOCK.get());
    }
}
