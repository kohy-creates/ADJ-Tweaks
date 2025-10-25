package xyz.kohara.adjcore.client.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.kohara.adjcore.client.particle.ModParticles;

import java.util.function.Supplier;

public class DamageIndicatorS2CPacket {

    private final double x, y, z;
    private final float amount;
    private final int type;

    public DamageIndicatorS2CPacket(double x, double y, double z, float amount, int type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.amount = amount;
        this.type = type;
    }

    public DamageIndicatorS2CPacket(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.amount = buf.readFloat();
        this.type = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(amount);
        buf.writeInt(type);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.getConnection().getLevel();

            level.addParticle(
                    ModParticles.DAMAGE_PARTICLE.get(),
                    x,
                    y,
                    z,
                    this.amount,
                    this.type,
                    0f
            );
        });
        return true;
    }
}
