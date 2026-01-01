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
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IronCauldronMenu extends RecipeBookMenu<CraftingContainer> {

    private static final Logger LOGGER = LogUtils.getLogger();

    /* ===== 槽位常量定义 ===== */
    // 使用2x2网格，所以有4个输入槽
    public static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CRAFT_SLOT_COUNT = 4; // 2x2 = 4个槽位
    private static final int CRAFT_SLOT_END = CRAFT_SLOT_START + CRAFT_SLOT_COUNT;

    // 玩家背包槽位索引不变
    private static final int INV_SLOT_START = CRAFT_SLOT_END;
    private static final int INV_SLOT_END = INV_SLOT_START + 27;
    private static final int USE_ROW_SLOT_START = INV_SLOT_END;
    private static final int USE_ROW_SLOT_END = USE_ROW_SLOT_START + 9;

    /* ===== 容器 ===== */
    private final CraftingContainer craftSlots;
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    // 自定义槽位位置
    private final List<SlotPosition> inputSlotPositions = new ArrayList<>();
    private final SlotPosition resultSlotPosition;

    // 槽位位置类
    public static class SlotPosition {
        public final int x;
        public final int y;

        public SlotPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // 默认构造函数 - 使用默认2x2布局
    public IronCauldronMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, ContainerLevelAccess.NULL,
                getDefault2x2InputPositions(), new SlotPosition(124, 35));
    }

    public IronCauldronMenu(int id, Inventory playerInventory, ContainerLevelAccess access) {
        this(id, playerInventory, access,
                getDefault2x2InputPositions(), new SlotPosition(124, 35));
    }

    // 主构造函数 - 允许自定义所有槽位位置
    public IronCauldronMenu(int id, Inventory playerInventory, ContainerLevelAccess access,
                            List<SlotPosition> inputPositions, SlotPosition resultPosition) {
        super(MenuRegistry.IRON_CAULDRON_MENU.get(), id);
        this.access = access;
        this.player = playerInventory.player;

        // 创建2x2的CraftingContainer
        this.craftSlots = new TransientCraftingContainer(this, 2, 2);

        // 验证输入位置数量
        if (inputPositions.size() != CRAFT_SLOT_COUNT) {
            LOGGER.warn("[IronCauldron] 输入位置数量({})与输入槽数量({})不匹配，使用默认位置",
                    inputPositions.size(), CRAFT_SLOT_COUNT);
            inputSlotPositions.addAll(getDefault2x2InputPositions());
        } else {
            inputSlotPositions.addAll(inputPositions);
        }

        this.resultSlotPosition = resultPosition;

        // 初始化UI布局
        initSlots(playerInventory);
    }

    // 获取默认2x2输入槽位置
    private static List<SlotPosition> getDefault2x2InputPositions() {
        List<SlotPosition> positions = new ArrayList<>();

        // 2x2网格布局
        positions.add(new SlotPosition(30, 17));  // 左上 (0,0)
        positions.add(new SlotPosition(48, 17));  // 右上 (1,0)
        positions.add(new SlotPosition(30, 35));  // 左下 (0,1)
        positions.add(new SlotPosition(48, 35));  // 右下 (1,1)

        return positions;
    }

    // 初始化所有槽位
    private void initSlots(Inventory playerInventory) {
        /* ===== 1. 结果槽 ===== */
        this.addSlot(new IronCauldronResultSlot(
                playerInventory.player,
                this.craftSlots,
                this.resultSlots,
                RESULT_SLOT,
                resultSlotPosition.x,
                resultSlotPosition.y
        ));

        /* ===== 2. 2x2输入槽 - 使用自定义位置 ===== */
        for (int i = 0; i < CRAFT_SLOT_COUNT; i++) {
            SlotPosition position = inputSlotPositions.get(i);
            this.addSlot(new Slot(
                    this.craftSlots,
                    i,
                    position.x,
                    position.y
            ));
        }

        /* ===== 3. 玩家背包 ===== */
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18
                ));
            }
        }

        /* ===== 4. 玩家快捷栏 ===== */
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(
                    playerInventory,
                    col,
                    8 + col * 18,
                    142
            ));
        }
    }

    /* ===== 合成逻辑 - 支持原版配方 ===== */
    protected static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player,
                                                  CraftingContainer craftSlots, ResultContainer resultSlots) {
        if (!level.isClientSide) {
            // 原版逻辑：尝试匹配配方（2x2合成网格）
            Optional<CraftingRecipe> optional = level.getRecipeManager()
                    .getRecipeFor(RecipeType.CRAFTING, craftSlots, level);

            if (optional.isPresent()) {
                CraftingRecipe recipe = optional.get();
                // 检查配方是否可以在2x2网格中合成
                if (recipe.canCraftInDimensions(2, 2)) {
                    ItemStack result = recipe.assemble(craftSlots, level.registryAccess());

                    if (result.isItemEnabled(level.enabledFeatures())) {
                        resultSlots.setRecipeUsed(recipe);
                        resultSlots.setItem(0, result);
                    } else {
                        resultSlots.setItem(0, ItemStack.EMPTY);
                    }
                } else {
                    // 配方需要3x3网格，但当前是2x2网格
                    resultSlots.setItem(0, ItemStack.EMPTY);
                }
            } else {
                // 没有匹配的原版配方，清空结果
                resultSlots.setItem(0, ItemStack.EMPTY);
            }
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
        // 检查配方是否可以在2x2网格中合成
        if (recipe.canCraftInDimensions(2, 2)) {
            return recipe.matches(this.craftSlots, this.player.level());
        }
        return false;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> {
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
        return 2; // 2x2网格
    }

    @Override
    public int getGridHeight() {
        return 2; // 2x2网格
    }

    @Override
    public int getSize() {
        return CRAFT_SLOT_COUNT + 1; // 输入槽数量 + 结果槽
    }

    // 禁用配方书功能（但仍然使用原版配方系统）
    @Override
    public net.minecraft.world.inventory.RecipeBookType getRecipeBookType() {
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

    /* ===== 获取容器的方法 ===== */
    public CraftingContainer getCraftSlots() {
        return this.craftSlots;
    }

    public ResultContainer getResultSlots() {
        return this.resultSlots;
    }

    public List<SlotPosition> getInputSlotPositions() {
        return new ArrayList<>(inputSlotPositions);
    }

    public SlotPosition getResultSlotPosition() {
        return resultSlotPosition;
    }
}