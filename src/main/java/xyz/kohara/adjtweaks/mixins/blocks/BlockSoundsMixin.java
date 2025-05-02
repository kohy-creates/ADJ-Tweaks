// Auditory
package xyz.kohara.adjtweaks.mixins.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.misc.AuditoryTags;
import xyz.kohara.adjtweaks.sounds.ModSoundEvents;

@Mixin(Block.class)
public class BlockSoundsMixin {

    @Inject(method = "fallOn", at = @At("RETURN"))
    public void auditory_fallSound(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
            if (!entity.isShiftKeyDown() && entity instanceof LivingEntity) {
                if (!state.isAir() && !state.is(BlockTags.FIRE) && !state.is(BlockTags.PORTALS) && (!(state.getBlock() instanceof LiquidBlock))) {
                    SoundType soundType = state.getSoundType();
                    world.playSound(null, pos, soundType.getStepSound(), SoundSource.BLOCKS, soundType.getVolume() * 0.15F, soundType.getPitch());
                }
            }
    }

    @Inject(at = @At("HEAD"), method = "getSoundType", cancellable = true)
    private void auditory_alterSoundType(BlockState state, CallbackInfoReturnable<SoundType> cir) {
        if (state.is(AuditoryTags.BASALT_SOUNDS)) {
            cir.setReturnValue(SoundType.BASALT);
        }
        else if (state.is(AuditoryTags.CLAY_BRICK_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.CLAY_BRICKS);
        }
        else if (state.is(AuditoryTags.DIRT_SOUNDS)) {
            cir.setReturnValue(SoundType.ROOTED_DIRT);
        }
        else if (state.is(AuditoryTags.GOLD_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.GOLD);
        }
        else if (state.is(AuditoryTags.LEAF_SOUNDS)) {
            cir.setReturnValue(SoundType.AZALEA_LEAVES);
        }
        else if (state.is(AuditoryTags.LILY_PAD_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.LILY_PAD);
        }
        else if (state.is(AuditoryTags.METAL_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.METAL);
        }
        else if (state.is(AuditoryTags.NETHERRACK_SOUNDS)) {
            cir.setReturnValue(SoundType.NETHERRACK);
        }
        else if (state.is(AuditoryTags.OBSIDIAN_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.OBSIDIAN);
        }
        else if (state.is(AuditoryTags.PLANT_SOUNDS)) {
            cir.setReturnValue(SoundType.HANGING_ROOTS);
        }
        else if (state.is(AuditoryTags.RAW_ORE_BLOCK_SOUNDS)) {
            cir.setReturnValue(SoundType.NETHER_GOLD_ORE);
        }
        else if (state.is(AuditoryTags.SAND_SOUNDS)) {
            cir.setReturnValue(SoundType.SAND);
        }
        else if (state.is(AuditoryTags.SHULKER_BOX_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.SHULKER_BOX);
        }
        else if (state.is(AuditoryTags.SMALL_OBJECT_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.SMALL_OBJECT);
        }
        else if (state.is(AuditoryTags.SPAWNER_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.SPAWNER);
        }
        else if (state.is(AuditoryTags.STONE_BRICK_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.STONE_BRICKS);
        }
        else if (state.is(AuditoryTags.STONE_ORE_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.STONE_ORE);
        }
        else if (state.is(AuditoryTags.STRING_SOUNDS)) {
            cir.setReturnValue(SoundType.VINE);
        }
        else if (state.is(AuditoryTags.TERRACOTTA_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.TERRACOTTA);
        }
        else if (state.is(AuditoryTags.WOOD_SOUNDS)) {
            cir.setReturnValue(SoundType.WOOD);
        }
        else if (state.is(AuditoryTags.MUSHROOM_SOUNDS)) {
            cir.setReturnValue(SoundType.WART_BLOCK);
        }
        else if (state.is(AuditoryTags.MUSHROOM_STEM_SOUNDS)) {
            cir.setReturnValue(SoundType.STEM);
        }
        else if (state.is(AuditoryTags.PURPUR_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.PURPUR);
        }
        else if (state.is(AuditoryTags.CHORUS_PLANT_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.CHORUS_PLANT);
        }
        else if (state.is(AuditoryTags.ICE_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.ICE);
        }
        else if (state.is(AuditoryTags.GOURD_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.GOURD);
        }
        else if (state.is(AuditoryTags.POT_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.SMALL_OBJECT);
        }
        else if (state.is(AuditoryTags.BOOKSHELF_SOUNDS)) {
            cir.setReturnValue(SoundType.CHISELED_BOOKSHELF);
        }
    }
}
