package com.suzuran_ss.bloodcraft_ss.registry;

import com.suzuran_ss.bloodcraft_ss.bloodcraft;
import com.suzuran_ss.bloodcraft_ss.menu.IronCauldronMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, bloodcraft.MODID);

    public static final RegistryObject<MenuType<IronCauldronMenu>> IRON_CAULDRON_MENU = MENUS.register("iron_cauldron",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                // 你可以在这里根据data读取自定义位置
                // 默认使用2x2布局
                return new IronCauldronMenu(windowId, inv);
            }));
}