package com.pandora.client;

import com.pandora.PandoraNetwork;
import com.pandora.Tribe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class TribeSelectScreen extends Screen {
    private static final Tribe[] CHOICES = {Tribe.FOREST, Tribe.WATER, Tribe.AIR, Tribe.FIRE};
    private static final int[] COLORS = {0x4FE38A, 0x3FD6E8, 0xD8F0FF, 0xFF7A3A};

    public TribeSelectScreen() { super(Component.literal("Choose your clan")); }

    @Override
    protected void init() {
        int w = 110, gap = 10, total = CHOICES.length * w + (CHOICES.length - 1) * gap;
        int x = (width - total) / 2, y = height / 2;
        for (Tribe t : CHOICES) {
            addRenderableWidget(Button.builder(Component.literal(t.displayName), b ->
                        PacketDistributor.sendToServer(new PandoraNetwork.SelectTribe(t.name())))
                    .bounds(x, y, w, 24)
                    .tooltip(Tooltip.create(Component.literal(t.description))).build());
            x += w + gap;
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        g.drawCenteredString(font, Component.literal("Choose your clan"), width / 2, height / 2 - 60, 0x9FF0FF);
        g.drawCenteredString(font, Component.literal("Eywa will guide your path. This choice is permanent."),
                width / 2, height / 2 - 44, 0xBBBBBB);
        int w = 110, gap = 10, total = CHOICES.length * w + (CHOICES.length - 1) * gap;
        int x = (width - total) / 2;
        for (int i = 0; i < CHOICES.length; i++) {
            g.fill(x + i * (w + gap), height / 2 - 6, x + i * (w + gap) + w, height / 2 - 3, 0xFF000000 | COLORS[i]);
        }
    }

    @Override public boolean shouldCloseOnEsc() { return false; }
    @Override public boolean isPauseScreen() { return false; }
}
