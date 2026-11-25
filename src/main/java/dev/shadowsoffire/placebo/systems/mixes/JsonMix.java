package dev.shadowsoffire.placebo.systems.mixes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Ingredient;

public record JsonMix<T>(Type type, Mix<T> mix) {

    public static Codec<JsonMix<?>> CODEC = PlaceboCodecs.enumCodec(Type.class).dispatch("mix_type", JsonMix::type, Type::codec);

    public JsonMix(Holder<T> pFrom, Ingredient pIngredient, Holder<T> pTo, Type mixType) {
        this(mixType, new Mix<>(pFrom, pIngredient, pTo));
    }

    /** Legacy data-driven mix descriptor, replacing the removed vanilla PotionBrewing.Mix. */
    public record Mix<T>(Holder<T> from, Ingredient ingredient, Holder<T> to) {}

    public static enum Type {
        CONTAINER(BuiltInRegistries.ITEM.holderByNameCodec()),
        POTION(BuiltInRegistries.POTION.holderByNameCodec());

        private final MapCodec<JsonMix<?>> codec;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private <T> Type(Codec<Holder<T>> elementCodec) {
            this.codec = (MapCodec) forType(this, elementCodec);
        }

        public MapCodec<JsonMix<?>> codec() {
            return this.codec;
        }

        private static <T> MapCodec<JsonMix<T>> forType(Type type, Codec<Holder<T>> elementCodec) {
            return RecordCodecBuilder.mapCodec(inst -> inst
                .group(
                    elementCodec.fieldOf("from").forGetter(m -> m.mix.from()),
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(m -> m.mix.ingredient()),
                    elementCodec.fieldOf("to").forGetter(m -> m.mix.to()))
                .apply(inst, (from, ingredient, to) -> new JsonMix<>(from, ingredient, to, type))

            );

        }
    }

}
