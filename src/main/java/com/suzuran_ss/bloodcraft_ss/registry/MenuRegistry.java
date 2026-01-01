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
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new IronCauldronMenu(windowId, inv)));
}