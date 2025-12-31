package com.suzuran_ss.bloodcraft_ss.recipe;

import com.suzuran_ss.bloodcraft_ss.bloodcraft;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.registries.*;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, bloodcraft.MODID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, bloodcraft.MODID);

    /* ===== RecipeType ===== */
    public static final RegistryObject<RecipeType<IronCauldronRecipe>> IRON_CAULDRON_TYPE =
            TYPES.register("iron_cauldron", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return bloodcraft.MODID + ":iron_cauldron";
                }
            });

    /* ===== Serializer ===== */
    public static final RegistryObject<RecipeSerializer<IronCauldronRecipe>> IRON_CAULDRON_SERIALIZER =
            SERIALIZERS.register("iron_cauldron", IronCauldronRecipe.Serializer::new);
}
