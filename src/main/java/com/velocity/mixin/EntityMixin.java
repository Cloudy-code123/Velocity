package com.velocity.mixin;

import com.velocity.VelocityMod;
import com.velocity.module.VelocityModule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Unique
    private boolean velocity$isClientPlayer() {
        return (Object) this instanceof ClientPlayerEntity;
    }

    @Inject(method = "setVelocityClient", at = @At("HEAD"), cancellable = true)
    private void velocity$cancelKnockback(double x, double y, double z, CallbackInfo ci) {
        try {
            if (!velocity$isClientPlayer()) return;

            VelocityModule module = VelocityMod.getInstance().getModuleManager().getVelocityModule();
            if (!module.isEnabled()) return;

            ci.cancel();
        } catch (Exception ignored) {
        }
    }
}
