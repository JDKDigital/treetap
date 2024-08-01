package cy.jdkdigital.treetap.event;

import cy.jdkdigital.treetap.TreeTap;
import cy.jdkdigital.treetap.client.particle.ColoredDripParticle;
import cy.jdkdigital.treetap.client.render.block.SapCollectorBlockEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = TreeTap.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
    @SubscribeEvent
    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(TreeTap.COLORED_DRIP_PARTICLE.get(), ColoredDripParticle.ColoredDripParticleFactory::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TreeTap.SAP_COLLECTOR_BLOCK_ENTITY.get(), SapCollectorBlockEntityRenderer::new);
    }
}
