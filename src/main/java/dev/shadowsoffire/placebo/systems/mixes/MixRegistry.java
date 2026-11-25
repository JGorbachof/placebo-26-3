package dev.shadowsoffire.placebo.systems.mixes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.shadowsoffire.placebo.Placebo;
import dev.shadowsoffire.placebo.dynreg.DynamicRegistry;
import dev.shadowsoffire.placebo.dynreg.RegistrySerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.PotionsPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.BrewingInput;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/** Legacy Placebo brewing definitions, compiled into native 26.3 brewing recipes. */
public class MixRegistry extends DynamicRegistry<JsonMix<?>> {
    public static final MixRegistry INSTANCE = new MixRegistry();

    public MixRegistry() {
        super(Placebo.LOGGER, Placebo.loc("brewing_mixes"), RegistrySerializer.synced(JsonMix.CODEC));
    }

    /** Rebuild the active native recipe index. Initial startup and reload use the RecipeManager mixin. */
    public static void applyMixes() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.getRecipeManager().finalizeRecipeLoading(server.getWorldData().enabledFeatures());
        }
    }

    public List<RecipeHolder<?>> toNativeRecipes() {
        List<RecipeHolder<?>> out = new ArrayList<>();
        this.getKeys().stream().sorted().forEach(id -> {
            JsonMix<?> mix = this.getValue(id);
            if (mix != null) {
                if (mix.type() == JsonMix.Type.CONTAINER) {
                    @SuppressWarnings("unchecked")
                    JsonMix<Item> typed = (JsonMix<Item>) mix;
                    var from = typed.mix().from().value();
                    var to = typed.mix().to().value();
                    var reagent = new PotionIngredient(typed.mix().ingredient(), Optional.empty());
                    var recipe = new BrewingRecipe(PotionIngredient.of(from), reagent, new ItemStackTemplate(to)) {
                        @Override public ItemStack assemble(BrewingInput input) {
                            return input.input().transmuteCopy(to, 1);
                        }
                    };
                    add(out, id, "container", recipe);
                } else {
                    @SuppressWarnings("unchecked")
                    JsonMix<Potion> typed = (JsonMix<Potion>) mix;
                    Holder<Potion> from = typed.mix().from();
                    Holder<Potion> to = typed.mix().to();
                    var reagent = new PotionIngredient(typed.mix().ingredient(), Optional.empty());
                    for (Holder<Item> container : BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.BREWING_POTION_INPUTS)) {
                        var item = container.value();
                        var patch = DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(to)).build();
                        var recipe = new BrewingRecipe(PotionIngredient.of(item, PotionsPredicate.ofPotion(from)), reagent, new ItemStackTemplate(item, patch)) {
                            @Override public ItemStack assemble(BrewingInput input) {
                                ItemStack result = input.input().copyWithCount(1);
                                PotionContents previous = result.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                                result.set(DataComponents.POTION_CONTENTS, previous.withPotion(to));
                                return result;
                            }
                        };
                        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
                        add(out, id, "potion/" + itemId.getNamespace() + "/" + itemId.getPath(), recipe);
                    }
                }
            }
        });
        return out;
    }

    private static void add(List<RecipeHolder<?>> out, Identifier id, String suffix, BrewingRecipe recipe) {
        var key = ResourceKey.create(Registries.RECIPE, Placebo.loc("legacy_brewing/" + id.getNamespace() + "/" + id.getPath() + "/" + suffix));
        out.add(new RecipeHolder<>(key, recipe));
    }
}
