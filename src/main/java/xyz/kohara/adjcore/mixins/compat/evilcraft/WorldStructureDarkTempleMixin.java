package xyz.kohara.adjcore.mixins.compat.evilcraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.cyclops.evilcraft.world.gen.structure.WorldStructureDarkTemple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldStructureDarkTemple.class)
public class WorldStructureDarkTempleMixin {

    @Shadow
    @Final
    @Mutable
    private int minHeight;

    @Shadow
    @Final
    @Mutable
    private int maxHeight;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void overwriteMinMaxHeight(Structure.StructureSettings structureSettings, int minHeight, int maxHeight, CallbackInfo ci) {
        this.minHeight = 64;
        this.maxHeight = 256;
    }

    @WrapOperation(
            method = "generatePieces",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/pieces/StructurePiecesBuilder;addPiece(Lnet/minecraft/world/level/levelgen/structure/StructurePiece;)V"
            )
    )
    private void decreaseHeight(
            StructurePiecesBuilder instance,
            StructurePiece piece,
            Operation<Void> original,
            @Local(argsOnly = true) Structure.GenerationContext context
    ) {
        int y = 1 + Mth.clamp(context.chunkGenerator().getFirstFreeHeight(context.chunkPos().getMiddleBlockX(), context.chunkPos().getMiddleBlockZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()), this.minHeight, this.maxHeight);
        original.call(instance, new WorldStructureDarkTemple.Piece(context.random(), context.chunkPos().getMinBlockX(), y, context.chunkPos().getMinBlockZ()));
    }
}
