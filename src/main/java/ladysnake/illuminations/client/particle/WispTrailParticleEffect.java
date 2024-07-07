package ladysnake.illuminations.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ladysnake.illuminations.client.Illuminations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.dynamic.Codecs;

import java.util.Locale;

public record WispTrailParticleEffect(float red, float green, float blue, float redEvolution, float greenEvolution,
                                      float blueEvolution) implements ParticleEffect {
    public static final MapCodec<WispTrailParticleEffect> CODEC = RecordCodecBuilder.mapCodec((instance) ->
        instance.group(
            Codec.FLOAT.fieldOf("r").forGetter((wispTrailParticleEffect) -> wispTrailParticleEffect.red),
            Codec.FLOAT.fieldOf("g").forGetter((wispTrailParticleEffect) -> wispTrailParticleEffect.green),
            Codec.FLOAT.fieldOf("b").forGetter((wispTrailParticleEffect) -> wispTrailParticleEffect.blue),
            Codec.FLOAT.fieldOf("re").forGetter((wispTrailParticleEffect) -> wispTrailParticleEffect.redEvolution),
            Codec.FLOAT.fieldOf("ge").forGetter((wispTrailParticleEffect) -> wispTrailParticleEffect.greenEvolution),
            Codec.FLOAT.fieldOf("be").forGetter((wispTrailParticleEffect) -> wispTrailParticleEffect.blueEvolution)).
            apply(instance, WispTrailParticleEffect::new));

    public static final PacketCodec<RegistryByteBuf, WispTrailParticleEffect> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public WispTrailParticleEffect decode(RegistryByteBuf buf) {
            return new WispTrailParticleEffect(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
        }

        @Override
        public void encode(RegistryByteBuf buf, WispTrailParticleEffect value) {
            buf.writeFloat(value.red);
            buf.writeFloat(value.green);
            buf.writeFloat(value.blue);
            buf.writeFloat(value.redEvolution);
            buf.writeFloat(value.greenEvolution);
            buf.writeFloat(value.blueEvolution);
        }
    };

    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f", Registries.PARTICLE_TYPE.getId(this.getType()), this.red, this.green, this.blue, this.redEvolution, this.greenEvolution, this.blueEvolution);
    }

    public ParticleType<WispTrailParticleEffect> getType() {
        return Illuminations.WISP_TRAIL;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float red() {
        return this.red;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float green() {
        return this.green;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float blue() {
        return this.blue;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float redEvolution() {
        return redEvolution;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float greenEvolution() {
        return greenEvolution;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float blueEvolution() {
        return blueEvolution;
    }
}
