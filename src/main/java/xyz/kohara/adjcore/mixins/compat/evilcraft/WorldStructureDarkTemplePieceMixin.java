package xyz.kohara.adjcore.mixins.compat.evilcraft;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.cyclops.evilcraft.world.gen.structure.WorldStructureDarkTemple;
import org.cyclops.evilcraft.world.gen.structure.WorldStructurePieceQuarterSymmetrical;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.ADJCore;

@Mixin(value = WorldStructureDarkTemple.Piece.class, remap = false)
public class WorldStructureDarkTemplePieceMixin {

    @Inject(
            method = "generateLayers",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/cyclops/evilcraft/world/gen/structure/WorldStructureDarkTemple$Piece;addLayer(I[Lorg/cyclops/evilcraft/world/gen/structure/WorldStructurePieceQuarterSymmetrical$BlockWrapper;)V",
                    ordinal = 0
            )
    )
    private void overrideChestLoot(CallbackInfo ci, @Local(name = "lc") WorldStructurePieceQuarterSymmetrical.BlockWrapper lc) {
        lc.action = (world, pos) -> {
            RandomSource rand = RandomSource.create();
            RandomizableContainerBlockEntity.setLootTable(world, rand, pos, ADJCore.of("chests/evilcraft_dark_temple"));
        };
    }

}
