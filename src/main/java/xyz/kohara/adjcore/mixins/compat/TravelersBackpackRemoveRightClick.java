package xyz.kohara.adjcore.mixins.compat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurio;
import com.tiviacz.travelersbackpack.config.BackpackEffect;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.FluidTankItemWrapper;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.KeyHelper;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import xyz.kohara.adjcore.compat.NewTravelersBackpackCurio;

import java.util.List;
import java.util.Objects;

@Mixin(value = TravelersBackpackItem.class)
public abstract class TravelersBackpackRemoveRightClick extends BlockItem {

    @Shadow
    public abstract void addAttributeModifierTooltip(ItemStack stack, List<Component> tooltipComponents, boolean whenEquippedPresent);

    public TravelersBackpackRemoveRightClick(Block block, Properties properties) {
        super(block, properties);
    }

    @ModifyExpressionValue(method = "overrideStackedOnOther", at = @At(value = "INVOKE", target = "Lcom/tiviacz/travelersbackpack/items/TravelersBackpackItem;isCreative(Lnet/minecraft/world/entity/player/Player;)Z"))
    public boolean overrideStackedOnOther(boolean original) {
        return true;
    }

    @ModifyExpressionValue(method = "overrideOtherStackedOnMe", at = @At(value = "INVOKE", target = "Lcom/tiviacz/travelersbackpack/items/TravelersBackpackItem;isCreative(Lnet/minecraft/world/entity/player/Player;)Z"))
    public boolean overrideOtherStackedOnMe(boolean original) {
        return true;
    }

    @ModifyReturnValue(method = "initCapabilities", at = @At(value = "RETURN"), remap = false)
    private ICapabilityProvider returnBetterCurioCapability(ICapabilityProvider original, @Local(argsOnly = true) ItemStack stack) {
        return new ICapabilityProvider() {
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ITEM_HANDLER) {
                    BackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
                    Objects.requireNonNull(wrapper);
                    return LazyOptional.of(wrapper::getStorageForInputOutput).cast();
                } else {
                    if (cap == ForgeCapabilities.FLUID_HANDLER_ITEM) {
                        BackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
                        if (wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).isPresent()) {
                            FluidTankItemWrapper fluidItemWrapper = new FluidTankItemWrapper(stack, wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).get());
                            return LazyOptional.of(() -> fluidItemWrapper).cast();
                        }
                    }

                    return TravelersBackpack.enableCurios() ? NewTravelersBackpackCurio.getCurioCapability(cap, stack) : LazyOptional.empty();
                }
            }
        };
    }

    /**
     * @author me
     * @reason ADJ
     */
    @OnlyIn(Dist.CLIENT)
    @Overwrite
    public void appendHoverText(@NotNull ItemStack stack, Level context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if (NbtHelper.has(stack, "Tier")) {
            tooltipComponents.add(Component.translatable("tier.travelersbackpack.backpack").append(Tiers.of((Integer) NbtHelper.get(stack, "Tier")).getLocalizedName()));
        }

        if (NbtHelper.getOrDefault(stack, "Inventory", NonNullList.withSize(0, ItemStack.EMPTY)).stream().anyMatch((itemStack) -> !itemStack.isEmpty())) {
            tooltipComponents.add(Component.literal("Backpacks with items inside can't be unequipped!").withStyle(Style.EMPTY.withColor(TextColor.parseColor("#D10000"))));
        }
    }

    /**
     * @author me
     * @reason ADJ
     */
    @Overwrite
    public @NotNull InteractionResult place(@NotNull BlockPlaceContext context) {
        return InteractionResult.FAIL;
    }
}
