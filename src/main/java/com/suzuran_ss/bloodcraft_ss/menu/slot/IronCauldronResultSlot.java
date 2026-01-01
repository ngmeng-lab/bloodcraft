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
            net.minecraftforge.event.ForgeEventFactory.firePlayerCraftingEvent(this.player, stack, this.craftSlots);
        }

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

        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(player);
        NonNullList<ItemStack> remainingItems = player.level().getRecipeManager()
                .getRemainingItemsFor(RecipeType.CRAFTING, this.craftSlots, player.level());
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

        for (int i = 0; i < remainingItems.size(); ++i) {
            ItemStack slotItem = this.craftSlots.getItem(i);
            ItemStack remainingItem = remainingItems.get(i);

            if (!slotItem.isEmpty()) {
                this.craftSlots.removeItem(i, 1);
                slotItem = this.craftSlots.getItem(i);
            }

            if (!remainingItem.isEmpty()) {
                if (slotItem.isEmpty()) {
                    this.craftSlots.setItem(i, remainingItem);
                } else if (ItemStack.isSameItemSameTags(slotItem, remainingItem)) {
                    remainingItem.grow(slotItem.getCount());
                    this.craftSlots.setItem(i, remainingItem);
                } else if (!player.getInventory().add(remainingItem)) {
                    player.drop(remainingItem, false);
                }
            }
        }

        this.setChanged();
    }
}