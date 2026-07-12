package com.velocity.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin placeholder — speed boost logic is handled in LivingEntityMixin
 * via @ModifyVariable on travel(), which is more compatible with 1.21.7's
 * immutable Input/PlayerInput record types.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerMixin {
}
