package cy.jdkdigital.treetap.compat.jade;

import cy.jdkdigital.treetap.TreeTap;
import cy.jdkdigital.treetap.common.block.TapBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(value = TreeTap.MODID)
public class JadePlugin implements IWailaPlugin
{
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(TapBlockEntityProvider.INSTANCE, TapBlock.class);
    }
}
