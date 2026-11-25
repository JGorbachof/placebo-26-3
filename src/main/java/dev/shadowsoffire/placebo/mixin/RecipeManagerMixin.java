package dev.shadowsoffire.placebo.mixin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.shadowsoffire.placebo.Placebo;
import dev.shadowsoffire.placebo.systems.mixes.MixRegistry;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Shadow @Final @Mutable private RecipeMap recipes;
    @Unique private List<RecipeHolder<?>> placebo$baseRecipes;

    @Inject(method = "finalizeRecipeLoading", at = @At("HEAD"), require = 1)
    private void placebo$addLegacyBrewing(FeatureFlagSet enabledFlags, CallbackInfo ci) {
        if (this.placebo$baseRecipes == null) this.placebo$baseRecipes = List.copyOf(this.recipes.values());
        var combined = new ArrayList<>(this.placebo$baseRecipes);
        var ids = new HashSet<>(combined.stream().map(RecipeHolder::id).toList());
        for (var recipe : MixRegistry.INSTANCE.toNativeRecipes()) {
            if (ids.add(recipe.id())) combined.add(recipe);
            else Placebo.LOGGER.warn("Skipping legacy brewing recipe with colliding id {}", recipe.id());
        }
        this.recipes = RecipeMap.createClient(combined);
    }
}
