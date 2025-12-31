package com.suzuran_ss.bloodcraft_ss.menu;

import com.suzuran_ss.bloodcraft_ss.menu.slot.IronCauldronResultSlot;
import com.suzuran_ss.bloodcraft_ss.recipe.ModRecipes;
import com.suzuran_ss.bloodcraft_ss.registry.BlockRegistry; // 添加导入
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class IronCauldronMenu extends AbstractContainerMenu {

    // 3x3 输入
    private final Container craftMatrix = new SimpleContainer(9);
    // 输出
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

    @Override
    public @NotNull ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        // TODO: Implement quick move stack logic if needed
        return ItemStack.EMPTY; // 返回空堆栈表示不进行快速移动
    }

    /* ===== 核心：当输入变化时重新计算结果 ===== */
    @Override
    public void slotsChanged(Container container) {
        access.execute((level, pos) -> {
            if (!level.isClientSide) {
                var recipeOpt = level.getRecipeManager()
                        .getRecipeFor(ModRecipes.IRON_CAULDRON_TYPE.get(), craftMatrix, level);

                if (recipeOpt.isPresent()) {
                    ItemStack result = recipeOpt.get().assemble(craftMatrix, level.registryAccess());
                    resultContainer.setItem(0, result);
                } else {
                    resultContainer.setItem(0, ItemStack.EMPTY);
                }
            }
        });
    }

    @Override
    public boolean stillValid(Player player) {
        // 修正：使用你实际的自定义方块类型
        return stillValid(access, player, BlockRegistry.IRON_CAULDRON_BLOCK.get());
    }
}