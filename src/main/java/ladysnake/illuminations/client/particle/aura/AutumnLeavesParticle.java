package ladysnake.illuminations.client.particle.aura;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class AutumnLeavesParticle extends SpriteBillboardParticle {
    private static final Random RANDOM = new Random();
    private final int variant = RANDOM.nextInt(6);
    private final SpriteProvider spriteProvider;

    private final double beginX;
    private final double beginY;
    private final double beginZ;

    public AutumnLeavesParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y + 0.05F, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;

        this.scale *= 0.5F + RANDOM.nextFloat() / 2.0F;
        this.maxAge = 30 + RANDOM.nextInt(60);
        this.collidesWithWorld = true;
        this.setSprite(spriteProvider.getSprite(this.variant % 3, 2));

        this.green = RANDOM.nextFloat() / 2f + 0.5f;
        this.blue = 0f;

        beginX = x;
        beginY = y;
        beginZ = z;
        this.angle = random.nextFloat() * 360f;
    }

    @Override
    public void buildGeometry(@NotNull VertexConsumer vertexConsumer, @NotNull Camera camera, float tickDelta) {
        Vec3d vec3d = camera.getPos();
        float f = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
        Quaternionf quaternion2;
        if (this.angle == 0.0F) {
            quaternion2 = camera.getRotation();
        } else {
            quaternion2 = new Quaternionf(camera.getRotation());

            // degreesQuaternion
            double i = this.angle * 0.017453292F;
            Quaternionf other = new Quaternionf(0, 0, sin(i / 2.0F), cos(i / 2.0F));

            // hamiltonProduct
            float x1 = quaternion2.x();
            float y1 = quaternion2.y();
            float z1 = quaternion2.z();
            float w1 = quaternion2.w();
            float x2 = other.x();
            float y2 = other.y();
            float z2 = other.z();
            float w2 = other.w();
            quaternion2.x = w1 * x2 + x1 * w2 + y1 * z2 - z1 * y2;
            quaternion2.y = w1 * y2 - x1 * z2 + y1 * w2 + z1 * x2;
            quaternion2.z = w1 * z2 + x1 * y2 - y1 * x2 + z1 * w2;
            quaternion2.w = w1 * w2 - x1 * x2 - y1 * y2 - z1 * z2;
        }

        Vector3f Vec3f = new Vector3f(-1.0F, -1.0F, 0.0F);
        Vec3f.rotate(quaternion2);
        Vector3f[] Vec3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float j = this.getSize(tickDelta);

        for (int k = 0; k < 4; ++k) {
            Vector3f Vec3f2 = Vec3fs[k];
            Vec3f2.rotate(quaternion2);
            Vec3f2.mul(j);
            Vec3f2.add(f, g, h);
        }

        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();
        int l = 15728880;

        vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).texture(maxU, maxV).color(red, green, blue, alpha).light(l);
        vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).texture(maxU, minV).color(red, green, blue, alpha).light(l);
        vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).texture(minU, minV).color(red, green, blue, alpha).light(l);
        vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).texture(minU, maxV).color(red, green, blue, alpha).light(l);
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        if (this.age++ < this.maxAge - 10) {
            this.alpha = Math.min(1f, this.alpha + 0.1f);
        }

        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        this.green *= 0.98;

        float fraction = this.age / (float) this.maxAge;
        this.x = MathHelper.cos(this.age / 15.0F + 1.0471973f * (variant + 0.5f)) * fraction + beginX;
        this.z = sin(this.age / 15.0F + 1.0471973f * (variant + 0.5f)) * fraction + beginZ;
        this.y = this.age / 34.0F + beginY + 0.05F;

        if (this.age >= this.maxAge - 10) {

            this.alpha = Math.max(0f, this.alpha - 0.1f);

            if (this.alpha <= 0f) {
                this.markDead();
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public DefaultFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new AutumnLeavesParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
