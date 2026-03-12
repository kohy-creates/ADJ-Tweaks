package xyz.kohara.adjcore.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICurio;

public record NewTravelersBackpackCurio(ItemStack stack) implements ICurio {
    @OnlyIn(Dist.CLIENT)
    public static void registerCurioRenderer() {
        ModItems.ITEMS.getEntries().stream().filter((holder) -> holder.get() instanceof TravelersBackpackItem).forEach((holder) -> CuriosRendererRegistry.register((Item) holder.get(), com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurio.Renderer::new));
    }

    public static LazyOptional getCurioCapability(Capability cap, ItemStack backpack) {
        return CuriosCapability.ITEM.orEmpty(cap, LazyOptional.of(() -> new NewTravelersBackpackCurio(backpack)));
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public boolean canEquip(SlotContext context) {
        return TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get();
    }

    public boolean canEquipFromUse(SlotContext slotContext) {
        return false;
    }

    public void curioTick(SlotContext slotContext) {
        if (TravelersBackpackConfig.SERVER.backpackSettings.backSlotIntegration.get()) {
            LivingEntity var3 = slotContext.entity();
            if (var3 instanceof Player player) {
                BackpackWrapper.tick(this.stack, player, true);
            }

        }
    }

    public boolean canUnequip(SlotContext slotContext) {
        return NbtHelper.getOrDefault(stack, "Inventory", NonNullList.withSize(0, ItemStack.EMPTY)).stream().allMatch(ItemStack::isEmpty);
    }

    @NotNull
    public ICurio.@NotNull DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit) {
        return DropRule.DEFAULT;
    }

    @Override
    public @NotNull SoundInfo getEquipSound(SlotContext context) {
        return new SoundInfo(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Renderer implements ICurioRenderer {
        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (stack.getItem() instanceof TravelersBackpackItem) {
                LivingEntity var15 = slotContext.entity();
                if (var15 instanceof Player player) {
                    EntityModel<T> var16 = renderLayerParent.getModel();
                    if (var16 instanceof PlayerModel<?> playerModel) {
                        BackpackLayer.renderBackpackLayer(playerModel, matrixStack, renderTypeBuffer, light, player, stack);
                    }
                }
            }

        }
    }
}
