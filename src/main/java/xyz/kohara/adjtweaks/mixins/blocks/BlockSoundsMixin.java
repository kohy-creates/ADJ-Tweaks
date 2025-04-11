// Auditory
package xyz.kohara.adjtweaks.mixins.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.misc.AuditoryTags;
import xyz.kohara.adjtweaks.sounds.ModSoundEvents;

@Mixin(Block.class)
public class BlockSoundsMixin {

    @Inject(method = "onLandedUpon", at = @At("RETURN"))
    public void auditory_fallSound(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
            if (!entity.isSneaking() && entity instanceof LivingEntity) {
                if (!state.isAir() && !state.isIn(BlockTags.FIRE) && !state.isIn(BlockTags.PORTALS) && (!(state.getBlock() instanceof FluidBlock))) {
                    BlockSoundGroup soundType = state.getSoundGroup();
                    world.playSound(null, pos, soundType.getStepSound(), SoundCategory.BLOCKS, soundType.getVolume() * 0.15F, soundType.getPitch());
                }
            }
    }

    @Inject(at = @At("HEAD"), method = "getSoundGroup", cancellable = true)
    private void auditory_alterBlockSoundGroup(BlockState state, CallbackInfoReturnable<BlockSoundGroup> cir) {
        if (state.isIn(AuditoryTags.BASALT_SOUNDS)) {
            cir.setReturnValue(BlockSoundGroup.BASALT);
        }
        else if (state.isIn(AuditoryTags.CLAY_BRICK_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.CLAY_BRICKS);
        }
        else if (state.isIn(AuditoryTags.DIRT_SOUNDS)) {
            cir.setReturnValue(BlockSoundGroup.ROOTED_DIRT);
        }
        else if (state.isIn(AuditoryTags.GOLD_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.GOLD);
        }
        else if (state.isIn(AuditoryTags.LEAF_SOUNDS)) {
            cir.setReturnValue(BlockSoundGroup.AZALEA_LEAVES);
        }
        else if (state.isIn(AuditoryTags.LILY_PAD_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.LILY_PAD);
        }
        else if (state.isIn(AuditoryTags.METAL_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.METAL);
        }
        else if (state.isIn(AuditoryTags.NETHERRACK_SOUNDS)) {
            cir.setReturnValue(BlockSoundGroup.NETHERRACK);
        }
        else if (state.isIn(AuditoryTags.OBSIDIAN_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.OBSIDIAN);
        }
        else if (state.isIn(AuditoryTags.PLANT_SOUNDS)) {
            cir.setReturnValue(BlockSoundGroup.HANGING_ROOTS);
        }
        else if (state.isIn(AuditoryTags.RAW_ORE_BLOCK_SOUNDS)) {
            cir.setReturnValue(BlockSoundGroup.NETHER_GOLD_ORE);
        }
        else if (state.isIn(AuditoryTags.SAND_SOUNDS)) {
            cir.setReturnValue(BlockSoundGroup.SAND);
        }
        else if (state.isIn(AuditoryTags.SHULKER_BOX_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.SHULKER_BOX);
        }
        else if (state.isIn(AuditoryTags.SMALL_OBJECT_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.SMALL_OBJECT);
        }
        else if (state.isIn(AuditoryTags.SPAWNER_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.SPAWNER);
        }
        else if (state.isIn(AuditoryTags.STONE_BRICK_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.STONE_BRICKS);
        }
        else if (state.isIn(AuditoryTags.STONE_ORE_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.STONE_ORE);
        }
        else if (state.isIn(AuditoryTags.STRING_SOUNDS)) {
            cir.setReturnValue(BlockSoundGroup.VINE);
        }
        else if (state.isIn(AuditoryTags.TERRACOTTA_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.TERRACOTTA);
        }
        else if (state.isIn(AuditoryTags.WOOD_SOUNDS)) {
            cir.setReturnValue(BlockSoundGroup.WOOD);
        }
        else if (state.isIn(AuditoryTags.MUSHROOM_SOUNDS)) {
            cir.setReturnValue(BlockSoundGroup.WART_BLOCK);
        }
        else if (state.isIn(AuditoryTags.MUSHROOM_STEM_SOUNDS)) {
            cir.setReturnValue(BlockSoundGroup.STEM);
        }
        else if (state.isIn(AuditoryTags.PURPUR_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.PURPUR);
        }
        else if (state.isIn(AuditoryTags.CHORUS_PLANT_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.CHORUS_PLANT);
        }
        else if (state.isIn(AuditoryTags.ICE_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.ICE);
        }
        else if (state.isIn(AuditoryTags.GOURD_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.GOURD);
        }
        else if (state.isIn(AuditoryTags.POT_SOUNDS)) {
            cir.setReturnValue(ModSoundEvents.SMALL_OBJECT);
        }
        else if (state.isIn(AuditoryTags.BOOKSHELF_SOUNDS)) {
            cir.setReturnValue(BlockSoundGroup.CHISELED_BOOKSHELF);
        }
    }
}
