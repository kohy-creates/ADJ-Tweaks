package xyz.kohara.adjcore.mixins.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.misc.ModBlockTags;

@Mixin(value = DiggerItem.class, priority = 4000)
public class DiggerItemMixin {

    @Inject(
            method = "isCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    public void isCorrectNew(BlockState block, CallbackInfoReturnable<Boolean> cir) {
        DiggerItem item = (DiggerItem) (Object) this;
        System.out.println("isCorrectNew");
        cir.setReturnValue(block.is(item.blocks) && adjcore$isCorrectTier(item.getTier(), block));
    }

    @Unique
    private boolean adjcore$isCorrectTier(Tier tier, BlockState block) {
        System.out.println("isCorrectTier");
        DiggerItem item = (DiggerItem) (Object) this;
        if (TierSortingRegistry.isTierSorted(tier)) {
            System.out.println("sorted");
            return TierSortingRegistry.isCorrectTierForDrops(tier, block) && block.is(item.blocks);
        } else {
            int i = tier.getLevel();
            if (i < 6 && block.is(ModBlockTags.NEEDS_TIER_6_TOOLS)) {
                return false;
            } else if (i < 5 && block.is(ModBlockTags.NEEDS_TIER_5_TOOLS)) {
                return false;
            } else if (i < 4 && block.is(ModBlockTags.NEEDS_NETHERITE_TOOLS)) {
                return false;
            } else if (i < 3 && block.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return false;
            } else if (i < 2 && block.is(BlockTags.NEEDS_IRON_TOOL)) {
                return false;
            } else return i >= 1 || !block.is(BlockTags.NEEDS_STONE_TOOL);
        }
    }
}
