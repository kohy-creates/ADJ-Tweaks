package xyz.kohara.adjcore.mixins.ars;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ItemStack.class)
public class GlyphCostMixin {

    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"))
    public void appendHoverText(Item instance, ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        instance.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        if (ArsNouveau.proxy.getMinecraft() == null) return;
        Player player = ArsNouveau.proxy.getPlayer();
        if (player == null) return;
        int cost;
        if (instance instanceof Glyph glyph) cost = glyph.spellPart.getCastingCost();
        else if (instance instanceof ICasterTool casterTool) {
            var casterData = casterTool.getSpellCaster(stack);
            Spell spell = casterData.getSpell(casterData.getCurrentSlot());
            if (spell.isEmpty()) return;
            cost = spell.getCost() - ManaUtil.getPlayerDiscounts(player, spell, stack);
        } else if (stack.getEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get()) > 0) {
            Spell casterData = new ReactiveCaster(stack).getSpell();
            if (casterData.isEmpty()) return;
            cost = casterData.getCost() - ManaUtil.getPlayerDiscounts(player, casterData, stack);
        } else return;

        tooltipComponents.add(Component.literal("Mᴀɴᴀ Cᴏsᴛ: ").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE)).append(String.valueOf(cost)));
    }
}
