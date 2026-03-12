package xyz.kohara.adjcore.mixins.compat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BackpackBlockEntity.class)
public class TravelersBackpackReplaceSleepingBag {

    @Unique
    private static Block adj$getBlockFromID(String id) {
        return ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse("accents:sewing_station"));
    }

    @ModifyReturnValue(method = "getProperSleepingBag", at = @At("RETURN"), remap = false)
    private static BlockState getProperSleepingBag(BlockState original, @Local(argsOnly = true) int sleepingBagColor) {
        return switch (sleepingBagColor) {
            case 0 -> adj$getBlockFromID("upgrade_aquatic:white_bedroll").defaultBlockState();
            case 1 -> adj$getBlockFromID("upgrade_aquatic:orange_bedroll").defaultBlockState();
            case 2 -> adj$getBlockFromID("upgrade_aquatic:magenta_bedroll").defaultBlockState();
            case 3 -> adj$getBlockFromID("upgrade_aquatic:light_blue_bedroll").defaultBlockState();
            case 4 -> adj$getBlockFromID("upgrade_aquatic:yellow_bedroll").defaultBlockState();
            case 5 -> adj$getBlockFromID("upgrade_aquatic:lime_bedroll").defaultBlockState();
            case 6 -> adj$getBlockFromID("upgrade_aquatic:pink_bedroll").defaultBlockState();
            case 7 -> adj$getBlockFromID("upgrade_aquatic:gray_bedroll").defaultBlockState();
            case 8 -> adj$getBlockFromID("upgrade_aquatic:light_gray_bedroll").defaultBlockState();
            case 9 -> adj$getBlockFromID("upgrade_aquatic:cyan_bedroll").defaultBlockState();
            case 10 -> adj$getBlockFromID("upgrade_aquatic:purple_bedroll").defaultBlockState();
            case 11 -> adj$getBlockFromID("upgrade_aquatic:blue_bedroll").defaultBlockState();
            case 12 -> adj$getBlockFromID("upgrade_aquatic:brown_bedroll").defaultBlockState();
            case 13 -> adj$getBlockFromID("upgrade_aquatic:green_bedroll").defaultBlockState();
            case 15 -> adj$getBlockFromID("upgrade_aquatic:black_bedroll").defaultBlockState();
            default -> adj$getBlockFromID("upgrade_aquatic:red_bedroll").defaultBlockState();
        };
    }
}
