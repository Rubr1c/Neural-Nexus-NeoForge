package net.rubr1c.neuralnexus.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record LearnerModelData(int accuracy, ResourceLocation entity, int slot) {
    public static final Codec<LearnerModelData> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.intRange(0, 100).fieldOf("accuracy").forGetter(LearnerModelData::accuracy),
                    ResourceLocation.CODEC.fieldOf("entity").forGetter(LearnerModelData::entity),
                    Codec.intRange(0, 8).fieldOf("slot").forGetter(LearnerModelData::slot)
            ).apply(inst, LearnerModelData::new)
    );

    public static final StreamCodec<ByteBuf, LearnerModelData> STREAM_CODEC =
            StreamCodec.of(
                    (buf, data) -> {
                        if (buf instanceof FriendlyByteBuf fb) {
                            fb.writeVarInt(data.accuracy());
                            fb.writeResourceLocation(data.entity());
                            fb.writeVarInt(data.slot());
                        } else {
                            buf.writeInt(data.accuracy());
                            buf.writeInt(data.slot());
                        }
                    },
                    // decode: ByteBuf → LearnerModelData
                    buf -> {
                        if (buf instanceof FriendlyByteBuf fb) {
                            int acc = fb.readVarInt();
                            ResourceLocation ent = fb.readResourceLocation();
                            int slot = fb.readVarInt();
                            return new LearnerModelData(acc, ent, slot);
                        } else {
                            int acc = buf.readInt();
                            throw new IllegalStateException("Expected FriendlyByteBuf here");
                        }
                    }
            );
}
