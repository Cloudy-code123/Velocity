package com.velocity.mixin;

import com.velocity.VelocityMod;
import com.velocity.module.VelocityModule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyVariable(method = "travel", at = @At("HEAD"), argsOnly = true)
    private Vec3d velocity$scaleMovementInput(Vec3d movementInput) {
        if (!((Object) this instanceof PlayerEntity)) return movementInput;
        if (!((LivingEntity)(Object)this).getWorld().isClient()) return movementInput;

        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        VelocityModule module = VelocityMod.getInstance().getModuleManager().getVelocityModule();
        module.tick(player);

        if (!module.isEnabled()) return movementInput;

        double multiplier = module.getCurrentEffectiveMultiplier();
        return new Vec3d(
                movementInput.x * multiplier,
                movementInput.y,
                movementInput.z * multiplier
        );
    }

    @Inject(method = "jump", at = @At("TAIL"))
    private void velocity$applyJumpBoost(CallbackInfo ci) {
        if (!((Object) this instanceof PlayerEntity)) return;
        if (!((LivingEntity)(Object)this).getWorld().isClient()) return;

        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        VelocityModule module = VelocityMod.getInstance().getModuleManager().getVelocityModule();
        module.onJump(player);
    }

    @Inject(method = "takeKnockback", at = @At("HEAD"), cancellable = true)
    private void velocity$cancelKnockback(double strength, double x, double z, CallbackInfo ci) {
        try {
            if (!((Object) this instanceof ClientPlayerEntity)) return;

            VelocityModule module = VelocityMod.getInstance().getModuleManager().getVelocityModule();
            if (!module.isEnabled()) return;

            ci.cancel();
        } catch (Exception ignored) {
        }
    }
}
