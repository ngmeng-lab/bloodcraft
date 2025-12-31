package com.suzuran_ss.bloodcraft_ss.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class IronCauldronResultSlot extends Slot {

    private final Player player;
    private final Container input;

    public IronCauldronResultSlot(Player player,
                                  Container input,
                                  Container result,
                                  int index,
                                  int x,
                                  int y) {
        super(result, index, x, y);
        this.player = player;
        this.input = input;
    }

    /* ===== 禁止手动放入 ===== */
    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    /* ===== 取出时消耗输入 ===== */
    @Override
    public void onTake(Player player, ItemStack stack) {
        super.onTake(player, stack);

        for (int i = 0; i < input.getContainerSize(); i++) {
            input.removeItem(i, 1);
        }
    }
}
