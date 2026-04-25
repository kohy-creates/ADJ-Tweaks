package xyz.kohara.adjcore.mixins.compat.twilightforest;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.world.components.structures.HedgeMazeComponent;
import twilightforest.world.components.structures.TFMaze;

@Mixin(value = HedgeMazeComponent.class, remap = false)
public class MazeStructureMixin {

    @Inject(
            method = "postProcess",
            at = @At(
                    value = "INVOKE",
                    target = "Ltwilightforest/world/components/structures/TFMaze;setSeed(J)V",
                    shift = At.Shift.AFTER
            ),
            remap = true
    )
    private void makeMazesSlightlyNewer(
            WorldGenLevel world,
            StructureManager manager,
            ChunkGenerator generator,
            RandomSource rand,
            BoundingBox sbb,
            ChunkPos chunkPosIn,
            BlockPos blockPos,
            CallbackInfo ci,
            @Local(name = "maze") TFMaze maze
    ) {
        maze.tall = 8;
        maze.torchBlockState = Blocks.SHROOMLIGHT.defaultBlockState();
        maze.torchRarity = 0.3f;
    }
}
