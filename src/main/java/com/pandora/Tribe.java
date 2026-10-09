package com.pandora;

import net.minecraft.world.entity.player.Player;

public enum Tribe {
    NONE("None", ""),
    FOREST("Omatikaya", "Forest clan. Climb trees, reduced fall damage in the canopy."),
    WATER("Metkayina", "Reef clan. Breathe and see underwater."),
    AIR("Tlalim", "Wind Traders. Light-boned, take far less fall damage."),
    FIRE("Mangkwan", "Ash People. Immune to fire, arrows always burn.");

    public final String displayName, description;
    Tribe(String n, String d) { displayName = n; description = d; }

    public static Tribe parse(String s) {
        try { return valueOf(s); } catch (Exception e) { return NONE; }
    }
    public static Tribe of(Player p) { return parse(p.getData(PandoraAttachments.TRIBE)); }
}
