package xyz.kohara.adjcore.client.handler;


import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.client.Keybindings;
import xyz.kohara.adjcore.client.particle.FlashingSparkParticle;
import xyz.kohara.adjcore.client.particle.ShimmerParticle;
import xyz.kohara.adjcore.client.particle.TerraShineParticle;
import xyz.kohara.adjcore.client.renderer.entity.CollectableItemRenderer;
import xyz.kohara.adjcore.client.renderer.entity.TerraSlashRenderer;
import xyz.kohara.adjcore.registry.ADJEntities;
import xyz.kohara.adjcore.registry.ADJParticles;
import xyz.kohara.adjcore.registry.entities.CollectibleEntity;

@Mod.EventBusSubscriber(modid = ADJCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModHandler {

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(Keybindings.INSTANCE.LOADOUT_1);
        event.register(Keybindings.INSTANCE.LOADOUT_2);
        event.register(Keybindings.INSTANCE.LOADOUT_3);
        event.register(Keybindings.INSTANCE.NEW_HIDE_GUI);
//        event.register(Keybindings.INSTANCE.HUGE_ASS_SCREENSHOT);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
                ADJParticles.SHIMMER.get(),
                ShimmerParticle.ShimmerParticleFactory::new
        );

        event.registerSpriteSet(
                ADJParticles.TERRA_SHINE.get(),
                TerraShineParticle.TerraShineParticleFactory::new
        );

        event.registerSpriteSet(
                ADJParticles.FLASHING_SPARK.get(),
                FlashingSparkParticle.FlashingSparkParticleFactory::new
        );
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        ADJEntities.COLLECTIBLES.forEach(obj -> {
                    var entry = (RegistryObject<EntityType<CollectibleEntity>>) obj;
                    event.registerEntityRenderer(entry.get(), CollectableItemRenderer::new);
                }
        );
        event.registerEntityRenderer(ADJEntities.TERRA_SLASH.get(), TerraSlashRenderer::new);
    }
}
