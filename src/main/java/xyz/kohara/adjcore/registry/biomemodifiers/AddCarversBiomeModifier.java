package xyz.kohara.adjcore.registry.biomemodifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public record AddCarversBiomeModifier(HolderSet<Biome> biomes, HolderSet<ConfiguredWorldCarver<?>> carvers,
                                      GenerationStep.Carving step) implements BiomeModifier {

    @Override
    public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == BiomeModifier.Phase.ADD && this.biomes.contains(biome)) {
            BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
            this.carvers.forEach(holder -> generationSettings.addCarver(this.step, holder));
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return ForgeMod.ADD_FEATURES_BIOME_MODIFIER_TYPE.get();
    }

    public static final Codec<AddCarversBiomeModifier> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddCarversBiomeModifier::biomes),
            ConfiguredWorldCarver.LIST_CODEC.fieldOf("carvers").forGetter(AddCarversBiomeModifier::carvers),
            GenerationStep.Carving.CODEC.fieldOf("step").forGetter(AddCarversBiomeModifier::step)
    ).apply(builder, AddCarversBiomeModifier::new));
}
