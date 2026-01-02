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

    public static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CRAFT_SLOT_COUNT = 4; // 2x2 = 4个槽位
    private static final int CRAFT_SLOT_END = CRAFT_SLOT_START + CRAFT_SLOT_COUNT;

    private static final int INV_SLOT_START = CRAFT_SLOT_END;
    private static final int INV_SLOT_END = INV_SLOT_START + 27;
    private static final int USE_ROW_SLOT_START = INV_SLOT_END;
    private static final int USE_ROW_SLOT_END = USE_ROW_SLOT_START + 9;

    private final CraftingContainer craftSlots;
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    private final List<SlotPosition> inputPositions = new ArrayList<>();
    private final SlotPosition resultSlotPosition;

    // 存绝对坐标
    public static class SlotPosition {
        public final int x;
        public final int y;

        public SlotPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public IronCauldronMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, ContainerLevelAccess.NULL,
                getDefault2x2InputPositions(), new SlotPosition(90, 30));
    }

    public IronCauldronMenu(int id, Inventory playerInventory, ContainerLevelAccess access) {
        this(id, playerInventory, access,
                getDefault2x2InputPositions(), new SlotPosition(90, 30));
    }

    public IronCauldronMenu(int id, Inventory playerInventory, ContainerLevelAccess access,
                            List<SlotPosition> inputPositions, SlotPosition resultPosition) {
        super(MenuRegistry.IRON_CAULDRON_MENU.get(), id);
        this.access = access;
        this.player = playerInventory.player;

        this.craftSlots = new TransientCraftingContainer(this, 2, 2);

        if (inputPositions.size() != CRAFT_SLOT_COUNT) {
            LOGGER.warn("[IronCauldron] 输入位置数量({})与输入槽数量({})不匹配，使用默认位置",
                    inputPositions.size(), CRAFT_SLOT_COUNT);
            this.inputPositions.addAll(getDefault2x2InputPositions());
        } else {
            this.inputPositions.addAll(inputPositions);
        }

        this.resultSlotPosition = resultPosition;

        initSlots(playerInventory);
    }

    private static List<SlotPosition> getDefault2x2InputPositions() {
        List<SlotPosition> positions = new ArrayList<>();
        positions.add(new SlotPosition(130, 30)); // 左
        positions.add(new SlotPosition(90, 7));  // 上
        positions.add(new SlotPosition(90, 53));  // 下
        positions.add(new SlotPosition(50, 30));  // 右
        return positions;
    }

    private void initSlots(Inventory playerInventory) {
        // 结果槽
        Slot resultSlot = new IronCauldronResultSlot(
                playerInventory.player,
                this.craftSlots,
                this.resultSlots,
                RESULT_SLOT,
                resultSlotPosition.x,
                resultSlotPosition.y
        );
        this.addSlot(resultSlot);
        LOGGER.info("[IronCauldron] 添加结果槽: index={}, x={}, y={}", RESULT_SLOT, resultSlotPosition.x, resultSlotPosition.y);

        // 输入槽
        for (int i = 0; i < CRAFT_SLOT_COUNT; i++) {
            SlotPosition pos = inputPositions.get(i);
            Slot inputSlot = new Slot(this.craftSlots, i, pos.x, pos.y);
            this.addSlot(inputSlot);
            LOGGER.info("[IronCauldron] 添加输入槽: index={}, x={}, y={}", i, pos.x, pos.y);
        }

        // 玩家背包
        int slotSize = 18;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int index = col + row * 9 + 9;
                int x = 8 + col * slotSize;
                int y = 84 + row * slotSize;
                this.addSlot(new Slot(playerInventory, index, x, y));
            }
        }

        // 热键栏
        for (int col = 0; col < 9; ++col) {
            int index = col;
            int x = 8 + col * slotSize;
            int y = 142;
            this.addSlot(new Slot(playerInventory, index, x, y));
        }
    }

    protected static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player,
                                                  CraftingContainer craftSlots, ResultContainer resultSlots) {
        if (!level.isClientSide) {
            Optional<CraftingRecipe> optional = level.getRecipeManager()
                    .getRecipeFor(RecipeType.CRAFTING, craftSlots, level);

            if (optional.isPresent()) {
                CraftingRecipe recipe = optional.get();
                if (recipe.canCraftInDimensions(2, 2)) {
                    ItemStack result = recipe.assemble(craftSlots, level.registryAccess());
                    if (result.isItemEnabled(level.enabledFeatures())) {
                        resultSlots.setRecipeUsed(recipe);
                        resultSlots.setItem(0, result);
                    } else {
                        resultSlots.setItem(0, ItemStack.EMPTY);
                    }
                } else {
                    resultSlots.setItem(0, ItemStack.EMPTY);
                }
            } else {
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
        return recipe.canCraftInDimensions(2, 2) && recipe.matches(this.craftSlots, this.player.level());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.craftSlots));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, BlockRegistry.IRON_CAULDRON_BLOCK.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == RESULT_SLOT) {
                this.access.execute((level, pos) -> itemstack1.getItem().onCraftedBy(itemstack1, level, player));

                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= INV_SLOT_START && index < USE_ROW_SLOT_END) {
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

            if (itemstack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();

            if (itemstack1.getCount() == itemstack.getCount()) return ItemStack.EMPTY;

            slot.onTake(player, itemstack1);
            if (index == RESULT_SLOT) player.drop(itemstack1, false);
        }

        return itemstack;
    }

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
        return 2;
    }

    @Override
    public int getGridHeight() {
        return 2;
    }

    @Override
    public int getSize() {
        return CRAFT_SLOT_COUNT + 1;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int index) {
        return index != this.getResultSlotIndex();
    }

    @Override
    public void handlePlacement(boolean shift, net.minecraft.world.item.crafting.Recipe<?> recipe, net.minecraft.server.level.ServerPlayer player) {
    }

    @Override
    public java.util.List<net.minecraft.client.RecipeBookCategories> getRecipeBookCategories() {
        return java.util.List.of();
    }

    public CraftingContainer getCraftSlots() {
        return this.craftSlots;
    }

    public ResultContainer getResultSlots() {
        return this.resultSlots;
    }
}
