package xyz.kohara.adjcore.client.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EnchantedCritParticleS2CPacket {

    private final int entityId;

    public EnchantedCritParticleS2CPacket(int entityId) {
        this.entityId = entityId;
    }

    public EnchantedCritParticleS2CPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId);
            if (entity != null) {
                Minecraft.getInstance().particleEngine.createTrackingEmitter(entity, ParticleTypes.ENCHANTED_HIT);
            }
        });
        return true;
    }

}
