package com.pandora.client;

import com.pandora.PandoraAttachments;
import com.pandora.Tribe;
import net.minecraft.client.Minecraft;

public class ClientPayloadHandler {
    public static void handleSync(String tribe) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        mc.player.setData(PandoraAttachments.TRIBE, tribe);
        if (Tribe.parse(tribe) == Tribe.NONE) mc.setScreen(new TribeSelectScreen());
        else if (mc.screen instanceof TribeSelectScreen) mc.setScreen(null);
    }
}
