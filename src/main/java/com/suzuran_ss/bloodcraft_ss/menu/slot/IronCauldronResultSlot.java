package com.suzuran_ss.bloodcraft_ss.menu.slot;

import com.mojang.logging.LogUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.slf4j.Logger;

public class IronCauldronResultSlot extends Slot {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final CraftingContainer craftSlots;
    private final Player player;
    private int removeCount;

    public IronCauldronResultSlot(
            Player player,
            CraftingContainer craftSlots,
            Container resultContainer,
            int index,
            int x,
            int y
    ) {
        super(resultContainer, index, x, y);
        this.player = player;
        this.craftSlots = craftSlots;
    }

    /* 禁止手动放入 */
    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }
        return super.remove(amount);
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }

    @Override
    protected void onSwapCraft(int amount) {
        this.removeCount += amount;
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        if (this.removeCount > 0) {
            stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
            // 触发合成事件（Forge事件）
            net.minecraftforge.event.ForgeEventFactory.firePlayerCraftingEvent(this.player, stack, this.craftSlots);
        }

        // 如果结果容器实现了RecipeHolder，则授予配方使用统计
        if (this.container instanceof net.minecraft.world.inventory.RecipeHolder recipeHolder) {
            recipeHolder.awardUsedRecipes(this.player, this.craftSlots.getItems());
        }

        this.removeCount = 0;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);

        LOGGER.info(
                "[IronCauldron] 玩家 {} 取走了结果槽物品：{} x{}",
                player.getName().getString(),
                stack.getItem().getDescription().getString(),
                stack.getCount()
        );

        // 处理剩余物品（参考原版合成台）
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(player);
        NonNullList<ItemStack> remainingItems = player.level().getRecipeManager()
                .getRemainingItemsFor(RecipeType.CRAFTING, this.craftSlots, player.level());
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

        for (int i = 0; i < remainingItems.size(); ++i) {
            ItemStack slotItem = this.craftSlots.getItem(i);
            ItemStack remainingItem = remainingItems.get(i);

            if (!slotItem.isEmpty()) {
                // 移除已使用的物品
                this.craftSlots.removeItem(i, 1);
                slotItem = this.craftSlots.getItem(i);
            }

            if (!remainingItem.isEmpty()) {
                if (slotItem.isEmpty()) {
                    // 如果有剩余物品，放入输入槽
                    this.craftSlots.setItem(i, remainingItem);
                } else if (ItemStack.isSameItemSameTags(slotItem, remainingItem)) {
                    // 如果相同物品，合并数量
                    remainingItem.grow(slotItem.getCount());
                    this.craftSlots.setItem(i, remainingItem);
                } else if (!player.getInventory().add(remainingItem)) {
                    // 如果背包放不下，掉落在地
                    player.drop(remainingItem, false);
                }
            }
        }

        // 通知容器更新
        this.setChanged();
    }
}