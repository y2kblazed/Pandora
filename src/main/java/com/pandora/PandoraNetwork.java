package com.pandora;

import com.pandora.client.ClientPayloadHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PandoraNetwork {

    public record SelectTribe(String tribe) implements CustomPacketPayload {
        public static final Type<SelectTribe> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(PandoraMod.MODID, "select_tribe"));
        public static final StreamCodec<ByteBuf, SelectTribe> CODEC =
                StreamCodec.composite(ByteBufCodecs.STRING_UTF8, SelectTribe::tribe, SelectTribe::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record SyncTribe(String tribe) implements CustomPacketPayload {
        public static final Type<SyncTribe> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(PandoraMod.MODID, "sync_tribe"));
        public static final StreamCodec<ByteBuf, SyncTribe> CODEC =
                StreamCodec.composite(ByteBufCodecs.STRING_UTF8, SyncTribe::tribe, SyncTribe::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar r = event.registrar("1");
        r.playToServer(SelectTribe.TYPE, SelectTribe.CODEC, (payload, ctx) -> ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sp)) return;
            Tribe chosen = Tribe.parse(payload.tribe());
            if (chosen == Tribe.NONE || Tribe.of(sp) != Tribe.NONE) return;
            sp.setData(PandoraAttachments.TRIBE, chosen.name());
            TribeEffects.applyAttributes(sp);
            sync(sp);
        }));
        r.playToClient(SyncTribe.TYPE, SyncTribe.CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> ClientPayloadHandler.handleSync(payload.tribe())));
    }

    public static void sync(ServerPlayer sp) {
        PacketDistributor.sendToPlayer(sp, new SyncTribe(sp.getData(PandoraAttachments.TRIBE)));
    }
}
