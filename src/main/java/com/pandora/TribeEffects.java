package com.pandora;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = PandoraMod.MODID)
public class TribeEffects {
    private static final ResourceLocation NAVI_SCALE =
            ResourceLocation.fromNamespaceAndPath(PandoraMod.MODID, "navi_scale");

    public static void applyAttributes(ServerPlayer p) {
        AttributeInstance scale = p.getAttribute(Attributes.SCALE);
        if (scale == null) return;
        scale.removeModifier(NAVI_SCALE);
        if (Tribe.of(p) != Tribe.NONE)
            scale.addPermanentModifier(new AttributeModifier(NAVI_SCALE, 0.95,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) { applyAttributes(sp); PandoraNetwork.sync(sp); }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) { applyAttributes(sp); PandoraNetwork.sync(sp); }
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post e) {
        Player p = e.getEntity();
        switch (Tribe.of(p)) {
            case WATER -> {
                if (p.isInWater()) {
                    p.setAirSupply(p.getMaxAirSupply());
                    if (!p.level().isClientSide)
                        p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, true, false));
                }
            }
            case FOREST -> {
                if (p.horizontalCollision && touchingTree(p)) {
                    Vec3 v = p.getDeltaMovement();
                    p.setDeltaMovement(v.x, p.isShiftKeyDown() ? 0.0 : 0.22, v.z);
                    p.resetFallDistance();
                }
            }
            case FIRE -> { if (p.isOnFire() && !p.level().isClientSide) p.clearFire(); }
            default -> {}
        }
    }

    private static boolean touchingTree(Player p) {
        BlockPos base = p.blockPosition();
        for (Direction d : Direction.Plane.HORIZONTAL) {
            for (int y = 0; y < 3; y++) {
                BlockState s = p.level().getBlockState(base.above(y).relative(d));
                if (s.is(BlockTags.LOGS) || s.is(BlockTags.LEAVES)) return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent e) {
        if (e.getEntity() instanceof Player p && Tribe.of(p) == Tribe.FIRE
                && e.getSource().is(DamageTypeTags.IS_FIRE)) e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        Tribe t = Tribe.of(p);
        if (t == Tribe.AIR) e.setDamageMultiplier(e.getDamageMultiplier() * 0.25f);
        else if (t == Tribe.FOREST) e.setDamageMultiplier(e.getDamageMultiplier() * 0.6f);
    }

    @SubscribeEvent
    public static void onArrow(EntityJoinLevelEvent e) {
        if (e.getEntity() instanceof AbstractArrow arrow && arrow.getOwner() instanceof Player p
                && Tribe.of(p) == Tribe.FIRE) arrow.igniteForSeconds(100);
    }
}
