package xyz.kohara.adjcore.client.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import xyz.kohara.adjcore.registry.ADJParticles;

import java.util.function.Supplier;

public class DamageIndicatorS2CPacket {

    private final double x, y, z;
    private final float amount;
    private final int type;
    private final int critOrSmall;

    public DamageIndicatorS2CPacket(double x, double y, double z, float amount, int type, int critOrSmall) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.amount = amount;
        this.type = type;
        this.critOrSmall = critOrSmall;
    }

    public DamageIndicatorS2CPacket(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.amount = buf.readFloat();
        this.type = buf.readInt();
        this.critOrSmall = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.amount);
        buf.writeInt(this.type);
        buf.writeInt(this.critOrSmall);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.getConnection().getLevel();

            level.addParticle(
                    ADJParticles.DAMAGE_PARTICLE.get(),
                    this.x,
                    this.y,
                    this.z,
                    this.amount,
                    this.type,
                    this.critOrSmall
            );
        });
        return true;
    }
}
