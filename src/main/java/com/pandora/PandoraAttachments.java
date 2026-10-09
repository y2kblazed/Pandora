package com.pandora;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class PandoraAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, PandoraMod.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<String>> TRIBE =
            ATTACHMENT_TYPES.register("tribe", () -> AttachmentType.builder(() -> "NONE")
                    .serialize(Codec.STRING).copyOnDeath().build());
}
