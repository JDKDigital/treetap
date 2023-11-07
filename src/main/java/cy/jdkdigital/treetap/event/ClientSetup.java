package cy.jdkdigital.treetap.event;

import cy.jdkdigital.treetap.TreeTap;
import cy.jdkdigital.treetap.client.particle.ColoredDripParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TreeTap.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
    @SubscribeEvent
    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(TreeTap.COLORED_DRIP_PARTICLE.get(), ColoredDripParticle.ColoredDripParticleFactory::new);
    }
}
