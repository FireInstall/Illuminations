package ladysnake.illuminations.client.particle.pet;

import ladysnake.illuminations.client.render.GlowyRenderLayer;
import ladysnake.illuminations.client.render.entity.model.pet.LanternModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import static org.joml.Math.cos;
import static org.joml.Math.sin;

public class PlayerLanternParticle extends Particle {
    public final Identifier texture;
    final RenderLayer layer;
    public float yaw;
    public float pitch;
    public float prevYaw;
    public float prevPitch;
    protected PlayerEntity owner;
    Model model;

    protected PlayerLanternParticle(ClientWorld world, double x, double y, double z, Identifier texture, float red, float green, float blue) {
        super(world, x, y, z);
        this.texture = texture;
        this.model = new LanternModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(LanternModel.MODEL_LAYER));
        this.layer = RenderLayer.getEntityTranslucent(texture);
        this.gravityStrength = 0.0F;

        this.maxAge = 35;
        this.owner = world.getClosestPlayer((TargetPredicate.createNonAttackable()).setBaseMaxDistance(1D), this.x, this.y, this.z);

        if (this.owner == null) {
            this.markDead();
        }

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = 0;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d vec3d = camera.getPos();
        float f = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(f, g, h);
        float rotationAngle = (MathHelper.lerp(g, this.prevYaw, this.yaw) - 180) * 0.017453292F;
        matrixStack.multiply(new Quaternionf(0, sin(rotationAngle / 2.0F), 0, cos(rotationAngle / 2.0F)));
        rotationAngle = MathHelper.lerp(g, this.prevPitch, this.pitch) * 0.017453292F;
        matrixStack.multiply(new Quaternionf(sin(rotationAngle / 2.0F), 0, 0, cos(rotationAngle / 2.0F)));
        matrixStack.scale(0.5F, -0.5F, 0.5F);
        matrixStack.translate(0, -1, 0);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertexConsumer2 = immediate.getBuffer(GlowyRenderLayer.get(texture));
        if (alpha > 0) {
            this.model.render(matrixStack, vertexConsumer2, 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0f);
        }
        immediate.draw();
    }

    @Override
    public void tick() {
        if (this.age > 10) {
            this.alpha = 1f;
        } else {
            this.alpha = 0;
        }

        if (owner != null) {
            this.prevPosX = this.x;
            this.prevPosY = this.y;
            this.prevPosZ = this.z;

            // die if old enough
            if (this.age++ >= this.maxAge) {
                this.markDead();
            }

            this.setPos(owner.getX() + Math.cos(owner.bodyYaw / 50) * 0.5, owner.getY() + owner.getHeight() + 0.5f + Math.sin(owner.age / 12f) / 12f, owner.getZ() - Math.cos(owner.bodyYaw / 50) * 0.5);

            this.prevYaw = this.yaw;
            this.yaw = owner.age * 2;
        } else {
            this.markDead();
        }
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
        private final Identifier texture;
        private final float red;
        private final float green;
        private final float blue;

        public DefaultFactory(SpriteProvider spriteProvider, Identifier texture, float red, float green, float blue) {
            this.texture = texture;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new PlayerLanternParticle(world, x, y, z, this.texture, this.red, this.green, this.blue);
        }
    }
}
