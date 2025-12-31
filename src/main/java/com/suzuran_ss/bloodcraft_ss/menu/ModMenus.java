package com.suzuran_ss.bloodcraft_ss.menu;

import com.suzuran_ss.bloodcraft_ss.bloodcraft;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.*;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, bloodcraft.MODID);

    public static final RegistryObject<MenuType<IronCauldronMenu>> IRON_CAULDRON =
            MENUS.register("iron_cauldron",
                    () -> IForgeMenuType.create(
                            (windowId, inv, data) ->
                                    new IronCauldronMenu(windowId, inv, ContainerLevelAccess.NULL)
                    ));
}
