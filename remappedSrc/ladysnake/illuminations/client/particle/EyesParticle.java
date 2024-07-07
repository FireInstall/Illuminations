package ladysnake.illuminations.client.particle;

import ladysnake.illuminations.client.Illuminations;
import ladysnake.illuminations.client.config.Config;
import ladysnake.illuminations.client.enums.HalloweenFeatures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class EyesParticle extends SpriteBillboardParticle {
    private static final Random RANDOM = new Random();
    private final SpriteProvider spriteProvider;
    protected float alpha = 1f;

    public EyesParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.setSprite(spriteProvider.getSprite(0, 3));

        this.scale *= 1f + new Random().nextFloat();
        this.maxAge = ThreadLocalRandom.current().nextInt(400, 1201); // live between   seconds and one minute
        this.collidesWithWorld = true;

        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        // disable if night vision or config is set to disabled
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity) camera.getFocusedEntity()).hasStatusEffect(StatusEffects.NIGHT_VISION) || Config.getHalloweenFeatures() == HalloweenFeatures.DISABLE) {
            this.markDead();
        }

        Vec3d vec3d = camera.getPos();
        float f = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
        Quaternionf quaternion2;
        if (this.angle == 0.0F) {
            quaternion2 = camera.getRotation();
        } else {
            quaternion2 = new Quaternionf(camera.getRotation());
            float i = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
            quaternion2.mul(new Quaternionf(0, 0, sin(i / 2.0F), cos(i / 2.0F)));
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
        float a = Math.min(1f, Math.max(0f, this.alpha));

        vertexConsumer.vertex(Vec3fs[0].x(), Vec3fs[0].y(), Vec3fs[0].z()).texture(maxU, maxV).color(red, green, blue, alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[1].x(), Vec3fs[1].y(), Vec3fs[1].z()).texture(maxU, minV).color(red, green, blue, alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[2].x(), Vec3fs[2].y(), Vec3fs[2].z()).texture(minU, minV).color(red, green, blue, alpha).light(l).next();
        vertexConsumer.vertex(Vec3fs[3].x(), Vec3fs[3].y(), Vec3fs[3].z()).texture(minU, maxV).color(red, green, blue, alpha).light(l).next();
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        if (this.age++ < this.maxAge) {
            if (this.age < 1) {
                this.setSprite(spriteProvider.getSprite(0, 3));
            } else if (this.age < 2) {
                this.setSprite(spriteProvider.getSprite(1, 3));
            } else if (this.age < 3) {
                this.setSprite(spriteProvider.getSprite(2, 3));
            } else {
                this.setSprite(spriteProvider.getSprite(3, 3));
            }
        } else {
            if (this.age < this.maxAge + 1) {
                this.setSprite(spriteProvider.getSprite(2, 3));
            } else if (this.age < this.maxAge + 2) {
                this.setSprite(spriteProvider.getSprite(1, 3));
            } else if (this.age < this.maxAge + 3) {
                this.setSprite(spriteProvider.getSprite(0, 3));
            } else {
                this.markDead();
            }
        }

        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        // disappear if light or if player gets too close
        if (this.maxAge > this.age && (world.getLightLevel(new BlockPos((int) x, (int) y, (int) z)) > 0 || world.getClosestPlayer(x, y, z, Illuminations.EYES_VANISHING_DISTANCE, false) != null)) {
            this.maxAge = this.age;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public DefaultFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new EyesParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }

}
