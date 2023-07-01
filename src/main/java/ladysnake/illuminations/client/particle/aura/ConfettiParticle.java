package ladysnake.illuminations.client.particle.aura;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.joml.Math.cos;
import static org.joml.Math.sin;

public class ConfettiParticle extends SpriteBillboardParticle {

    private static final Random RANDOM = new Random();
    private final double rotationXmod;
    private final double rotationYmod;
    private final double rotationZmod;
    private final float groundOffset;
    private float rotationX;
    private float rotationY;
    private float rotationZ;

    public ConfettiParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.scale *= 0.1f + new Random().nextFloat() * 0.5f;
        this.collidesWithWorld = true;
        this.setSpriteForAge(spriteProvider);
        this.alpha = 1f;

        this.maxAge = ThreadLocalRandom.current().nextInt(400, 420); // live approx 20s
        this.red = RANDOM.nextFloat();
        this.blue = RANDOM.nextFloat();
        this.green = RANDOM.nextFloat();

        this.gravityStrength = 0.1f;
        this.velocityX = velocityX * 10f;
        this.velocityY = velocityY * 10f;
        this.velocityZ = velocityZ * 10f;
        this.velocityMultiplier = 0.5f;

        this.rotationX = RANDOM.nextFloat() * 360f;
        this.rotationY = RANDOM.nextFloat() * 360f;
        this.rotationZ = RANDOM.nextFloat() * 360f;
        this.rotationXmod = RANDOM.nextFloat() * 10f * (random.nextBoolean() ? -1 : 1);
        this.rotationYmod = RANDOM.nextFloat() * 10f * (random.nextBoolean() ? -1 : 1);
        this.rotationZmod = RANDOM.nextFloat() * 10f * (random.nextBoolean() ? -1 : 1);

        this.groundOffset = RANDOM.nextFloat() / 100f + 0.001f;

        this.setPos(this.x + TwilightFireflyParticle.getWanderingDistance(this.random), this.y + random.nextFloat() * 2d, this.z + TwilightFireflyParticle.getWanderingDistance(this.random));
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d vec3d = camera.getPos();
        float f = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());

        Vector3f[] Vec3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float j = this.getSize(tickDelta);

        if (!this.onGround) {
            rotationX += rotationXmod;
            rotationY += rotationYmod;
            rotationZ += rotationZmod;

            for (int k = 0; k < 4; ++k) {
                Vector3f Vec3f2 = Vec3fs[k];

                //deg to rad
                float x = rotationX * 0.017453292F;
                float y = rotationY * 0.017453292F;
                float z = rotationZ * 0.017453292F;

                float f2 = sin(0.5F * x);
                float g2 = cos(0.5F * x);
                float h2 = sin(0.5F * y);
                float i2 = cos(0.5F * y);
                float j2 = sin(0.5F * z);
                float k2 = cos(0.5F * z);
                x = f2 * i2 * k2 + g2 * h2 * j2;
                y = g2 * h2 * k2 - f2 * i2 * j2;
                z = f2 * h2 * k2 + g2 * i2 * j2;
                float w = g2 * i2 * k2 - f2 * h2 * j2;

                Vec3f2.rotate(new Quaternionf(x, y, z, w));
                Vec3f2.mul(j);
                Vec3f2.add(f, g, h);
            }
        } else {
            rotationX = 90f;
            rotationY = 0;

            for (int k = 0; k < 4; ++k) {
                Vector3f Vec3f2 = Vec3fs[k];

                //deg to rad
                float x = rotationX * 0.017453292F;
                float y = rotationY * 0.017453292F;
                float z = rotationZ * 0.017453292F;

                float f2 = sin(0.5F * x);
                float g2 = cos(0.5F * x);
                float h2 = sin(0.5F * y);
                float i2 = cos(0.5F * y);
                float j2 = sin(0.5F * z);
                float k2 = cos(0.5F * z);
                x = f2 * i2 * k2 + g2 * h2 * j2;
                y = g2 * h2 * k2 - f2 * i2 * j2;
                z = f2 * h2 * k2 + g2 * i2 * j2;
                float w = g2 * i2 * k2 - f2 * h2 * j2;

                Vec3f2.rotate(new Quaternionf(x, y, z, w));
                Vec3f2.mul(j);
                Vec3f2.add(f, g + this.groundOffset, h);
            }
        }

        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();
        int l = 15728880;

        vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).texture(maxU, maxV).color(red, green, blue, alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).texture(maxU, minV).color(red, green, blue, alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).texture(minU, minV).color(red, green, blue, alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).texture(minU, maxV).color(red, green, blue, alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).texture(maxU, maxV).color(red, green, blue, alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).texture(maxU, minV).color(red, green, blue, alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).texture(minU, minV).color(red, green, blue, alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).texture(minU, maxV).color(red, green, blue, alpha).light(l).next();
    }

    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            if (this.world.getFluidState(new BlockPos((int) this.x, (int) (this.y + 0.2), (int) this.z)).isEmpty()) {
                if (this.world.getFluidState(new BlockPos((int) this.x, (int) (this.y - 0.01), (int) this.z)).isIn(FluidTags.WATER)) {
                    this.onGround = true;
                    this.velocityY = 0;
                } else {
                    this.velocityY -= 0.04D * (double) this.gravityStrength;
                    this.move(this.velocityX, this.velocityY, this.velocityZ);
                    if (this.field_28787 && this.y == this.prevPosY) {
                        this.velocityX *= 1.1D;
                        this.velocityZ *= 1.1D;
                    }

                    this.velocityX *= this.velocityMultiplier;
                    this.velocityY *= this.velocityMultiplier;
                    this.velocityZ *= this.velocityMultiplier;

                    this.velocityMultiplier = Math.min(0.98f, this.velocityMultiplier * 1.15f);

                    if (this.onGround) {
                        this.velocityX *= 0.699999988079071D;
                        this.velocityZ *= 0.699999988079071D;
                    }
                }
            } else {
                this.markDead();
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public DefaultFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new ConfettiParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
