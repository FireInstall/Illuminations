package ladysnake.illuminations.mixin;

import ladysnake.illuminations.client.Illuminations;
import ladysnake.illuminations.client.config.Config;
import ladysnake.illuminations.client.enums.HalloweenFeatures;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.potion.Potion;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.LocalDate;
import java.time.Month;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract boolean hasInvertedHealingAndHarm();

    @Inject(method = "onDeath", at = @At("RETURN"))
    public void onDeath(DamageSource source, CallbackInfo callbackInfo) {
        if (this.hasInvertedHealingAndHarm() && random.nextInt(5) == 0 && Illuminations.isNightTime(this.getWorld()) && ((Config.getHalloweenFeatures() == HalloweenFeatures.ENABLE && LocalDate.now().getMonth() == Month.OCTOBER) || Config.getHalloweenFeatures() == HalloweenFeatures.ALWAYS)) {
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_VEX_CHARGE, SoundCategory.AMBIENT, 1.0f, 0.8f);

            this.getWorld().addParticle(Illuminations.POLTERGEIST, true, this.getX() + 0.5, this.getEyeY(), this.getZ(), 0f, 0f, 0f);
        }
    }
}
