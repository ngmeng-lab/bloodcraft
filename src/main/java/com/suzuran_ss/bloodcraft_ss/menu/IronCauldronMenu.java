package com.suzuran_ss.bloodcraft_ss.menu;

import com.mojang.logging.LogUtils;
import com.suzuran_ss.bloodcraft_ss.menu.slot.IronCauldronResultSlot;
import com.suzuran_ss.bloodcraft_ss.registry.BlockRegistry;
import com.suzuran_ss.bloodcraft_ss.registry.MenuRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Optional;

public class IronCauldronMenu extends RecipeBookMenu<CraftingContainer> {

    private static final Logger LOGGER = LogUtils.getLogger();

    /* ===== 槽位常量定义 ===== */
    public static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CRAFT_SLOT_END = 10;
    private static final int INV_SLOT_START = 10;
    private static final int INV_SLOT_END = 37;
    private static final int USE_ROW_SLOT_START = 37;
    private static final int USE_ROW_SLOT_END = 46;

    /* ===== 容器 ===== */
    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    public IronCauldronMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, ContainerLevelAccess.NULL);
    }

    public IronCauldronMenu(int id, Inventory playerInventory, ContainerLevelAccess access) {
        // 使用自定义的菜单类型，而不是 MenuType.CRAFTING
        super(MenuRegistry.IRON_CAULDRON_MENU.get(), id);
        this.access = access;
        this.player = playerInventory.player;

        LOGGER.info("[IronCauldron] GUI opened by player: {}", playerInventory.player.getName().getString());

        /* ===== 结果槽 ===== */
        this.addSlot(new IronCauldronResultSlot(
                playerInventory.player,
                this.craftSlots,
                this.resultSlots,
                RESULT_SLOT,
                124,
                35
        ));

        /* ===== 3x3 输入槽 ===== */
        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 3; ++col) {
                this.addSlot(new Slot(
                        this.craftSlots,
                        col + row * 3,
                        30 + col * 18,
                        17 + row * 18
                ));
            }
        }

        /* ===== 玩家背包 ===== */
        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18
                ));
            }
        }

        /* ===== 快捷栏 ===== */
        for(int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(
                    playerInventory,
                    col,
                    8 + col * 18,
                    142
            ));
        }
    }

    /* ===== 合成逻辑 ===== */
    protected static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player,
                                                  CraftingContainer craftSlots, ResultContainer resultSlots) {
        if (!level.isClientSide) {
            LOGGER.info("[IronCauldron] 检测合成网格变化 (服务器端)");

            // 打印输入槽位信息
            for (int i = 0; i < craftSlots.getContainerSize(); i++) {
                ItemStack stack = craftSlots.getItem(i);
                String itemInfo = stack.isEmpty() ? "EMPTY" :
                        String.format("%s x%d",
                                stack.getItem().getDescription().getString(),
                                stack.getCount());
                LOGGER.info("[IronCauldron] 输入槽位 {}: {}", i, itemInfo);
            }

            // 原版逻辑：尝试匹配配方
            Optional<CraftingRecipe> optional = level.getRecipeManager()
                    .getRecipeFor(RecipeType.CRAFTING, craftSlots, level);

            if (optional.isPresent()) {
                CraftingRecipe recipe = optional.get();
                ItemStack result = recipe.assemble(craftSlots, level.registryAccess());

                if (result.isItemEnabled(level.enabledFeatures())) {
                    LOGGER.info("[IronCauldron] 找到配方，生成结果: {}", result.getItem().getDescription().getString());
                    resultSlots.setRecipeUsed(recipe);
                    resultSlots.setItem(0, result);
                } else {
                    LOGGER.info("[IronCauldron] 配方结果被禁用");
                    resultSlots.setItem(0, ItemStack.EMPTY);
                }
            } else {
                // 没有匹配配方，根据你的需求自定义逻辑
                boolean hasAnyInput = false;
                for (int i = 0; i < craftSlots.getContainerSize(); i++) {
                    if (!craftSlots.getItem(i).isEmpty()) {
                        hasAnyInput = true;
                        break;
                    }
                }

                ItemStack currentResult = resultSlots.getItem(0);
                LOGGER.info("[IronCauldron] 输入状态: hasAnyInput={}, 当前结果: {}", hasAnyInput,
                        currentResult.isEmpty() ? "EMPTY" : currentResult.getItem().getDescription().getString());

                if (hasAnyInput && currentResult.isEmpty()) {
                    // 自定义逻辑：当有输入且结果为空时，生成铁锭
                    ItemStack customResult = new ItemStack(Items.IRON_INGOT, 1);
                    resultSlots.setItem(0, customResult);
                    LOGGER.info("[IronCauldron] 自定义逻辑：检测到输入物品，生成铁锭");
                } else if (!hasAnyInput && !currentResult.isEmpty()) {
                    // 输入为空，清空结果
                    resultSlots.setItem(0, ItemStack.EMPTY);
                    LOGGER.info("[IronCauldron] 输入为空，清空结果槽");
                }
            }
        } else {
            LOGGER.info("[IronCauldron] 检测合成网格变化 (客户端)");
        }
    }

    @Override
    public void slotsChanged(Container container) {
        this.access.execute((level, pos) -> {
            slotChangedCraftingGrid(this, level, this.player, this.craftSlots, this.resultSlots);
        });
    }

    @Override
    public void fillCraftSlotsStackedContents(net.minecraft.world.entity.player.StackedContents stackedContents) {
        this.craftSlots.fillStackedContents(stackedContents);
    }

    @Override
    public void clearCraftingContent() {
        this.craftSlots.clearContent();
        this.resultSlots.clearContent();
    }

    @Override
    public boolean recipeMatches(net.minecraft.world.item.crafting.Recipe<? super CraftingContainer> recipe) {
        return recipe.matches(this.craftSlots, this.player.level());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> {
            LOGGER.info("[IronCauldron] 关闭容器，清理输入槽位物品");
            this.clearContainer(player, this.craftSlots);
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, BlockRegistry.IRON_CAULDRON_BLOCK.get());
    }

    /* ===== 快速移动物品逻辑 ===== */
    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == RESULT_SLOT) {
                // 从结果槽移动
                this.access.execute((level, pos) -> {
                    itemstack1.getItem().onCraftedBy(itemstack1, level, player);
                });

                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= INV_SLOT_START && index < USE_ROW_SLOT_END) {
                // 从背包移动
                if (!this.moveItemStackTo(itemstack1, CRAFT_SLOT_START, CRAFT_SLOT_END, false)) {
                    if (index < INV_SLOT_END) {
                        if (!this.moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            if (index == RESULT_SLOT) {
                player.drop(itemstack1, false);
            }
        }

        return itemstack;
    }

    /* ===== 其他必要方法 ===== */
    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public int getResultSlotIndex() {
        return RESULT_SLOT;
    }

    @Override
    public int getGridWidth() {
        return this.craftSlots.getWidth();
    }

    @Override
    public int getGridHeight() {
        return this.craftSlots.getHeight();
    }

    @Override
    public int getSize() {
        return 10; // 1个结果槽 + 9个合成槽
    }

    // 禁用配方书功能
    @Override
    public net.minecraft.world.inventory.RecipeBookType getRecipeBookType() {
        // 返回一个不存在的类型来禁用配方书
        return net.minecraft.world.inventory.RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int index) {
        return index != this.getResultSlotIndex();
    }

    /* ===== 覆盖以禁用配方书功能 ===== */
    @Override
    public void handlePlacement(boolean shift, net.minecraft.world.item.crafting.Recipe<?> recipe, net.minecraft.server.level.ServerPlayer player) {
        // 不执行任何操作，禁用配方书放置功能
    }

    @Override
    public java.util.List<net.minecraft.client.RecipeBookCategories> getRecipeBookCategories() {
        // 返回空列表，不显示任何配方书分类
        return java.util.List.of();
    }
}