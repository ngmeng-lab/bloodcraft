package com.suzuran_ss.bloodcraft_ss.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class IronCauldronRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;

    public IronCauldronRecipe(ResourceLocation id,
                              ItemStack result,
                              NonNullList<Ingredient> ingredients) {
        this.id = id;
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(Container container, Level level) {
        for (int i = 0; i < ingredients.size(); i++) {
            if (!ingredients.get(i).test(container.getItem(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= ingredients.size();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.IRON_CAULDRON_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.IRON_CAULDRON_TYPE.get();
    }


    public static class Serializer implements RecipeSerializer<IronCauldronRecipe> {

        @Override
        public IronCauldronRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);

            for (int i = 0; i < ingredientsJson.size(); i++) {
                ingredients.set(i, Ingredient.fromJson(ingredientsJson.get(i)));
            }

            ItemStack result = ShapedRecipe.itemStackFromJson(
                    GsonHelper.getAsJsonObject(json, "result")
            );

            return new IronCauldronRecipe(id, result, ingredients);
        }

        @Override
        public IronCauldronRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);

            for (int i = 0; i < size; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack result = buf.readItem();
            return new IronCauldronRecipe(id, result, ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, IronCauldronRecipe recipe) {
            buf.writeInt(recipe.ingredients.size());
            for (Ingredient ing : recipe.ingredients) {
                ing.toNetwork(buf);
            }
            buf.writeItem(recipe.result);
        }
    }
}
