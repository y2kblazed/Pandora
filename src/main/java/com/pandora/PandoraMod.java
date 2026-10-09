package com.pandora;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(PandoraMod.MODID)
public class PandoraMod {
    public static final String MODID = "pandora";

    public PandoraMod(IEventBus modBus, ModContainer container) {
        PandoraAttachments.ATTACHMENT_TYPES.register(modBus);
        modBus.addListener(PandoraNetwork::register);
    }
}
