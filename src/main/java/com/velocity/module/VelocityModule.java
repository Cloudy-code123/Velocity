package com.velocity.module;

import com.velocity.config.ConfigManager;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Core velocity module that handles speed multiplier logic with smooth
 * acceleration/deceleration and jitter prevention.
 * <p>
 * The effective multiplier is interpolated each tick to provide smooth
 * transitions when toggling or changing settings.
 */
public class VelocityModule {

    private boolean enabled = false;
    private double currentEffectiveMultiplier = 1.0;
    private double currentBoostAccumulator = 0.0;

    private final ConfigManager config;

    /** Smooth interpolation factor for acceleration/deceleration. */
    private static final double INTERPOLATION_SPEED = 0.1;

    /** Threshold below which the multiplier snaps to target to avoid jitter. */
    private static final double SNAP_THRESHOLD = 0.005;

    /** Maximum allowed multiplier to prevent instability. */
    private static final double MAX_MULTIPLIER = 10.0;

    /** Minimum multiplier to prevent reverse movement. */
    private static final double MIN_MULTIPLIER = 0.1;

    public VelocityModule(ConfigManager config) {
        this.config = config;
    }

    /**
     * Called every client tick to update the effective multiplier.
     * Smoothly interpolates towards the target value based on acceleration setting.
     *
     * @param player the local client player
     */
    public void tick(ClientPlayerEntity player) {
        if (!enabled) {
            // Smoothly decelerate back to 1.0 when disabled
            smoothReturnToNormal();
            return;
        }

        // Calculate target multiplier from config
        double target = config.getSpeedMultiplier();

        // Apply sprint boost if enabled and player is sprinting
        if (config.isSprintBoost() && player.isSprinting()) {
            target *= 1.3;
        }

        // Clamp target to safe range
        target = clamp(target, MIN_MULTIPLIER, MAX_MULTIPLIER);

        // Smooth interpolation based on acceleration setting
        double accelFactor = config.getAcceleration() * INTERPOLATION_SPEED;
        double diff = target - currentEffectiveMultiplier;

        if (Math.abs(diff) < SNAP_THRESHOLD) {
            currentEffectiveMultiplier = target;
        } else {
            currentEffectiveMultiplier += diff * Math.max(accelFactor, 0.01);
        }

        currentEffectiveMultiplier = clamp(currentEffectiveMultiplier, MIN_MULTIPLIER, MAX_MULTIPLIER);
    }

    /**
     * Applies jump momentum boost when the player jumps.
     *
     * @param player the local client player
     */
    public void onJump(ClientPlayerEntity player) {
        if (!enabled || !config.isJumpBoost()) return;

        var velocity = player.getVelocity();
        double boostAmount = config.getSpeedMultiplier() * 0.12;
        player.setVelocity(velocity.x, velocity.y + boostAmount, velocity.z);
    }

    /** Toggles the module on/off. Resets multiplier when disabling. */
    public void toggle() {
        enabled = !enabled;
        if (!enabled) {
            currentEffectiveMultiplier = 1.0;
            currentBoostAccumulator = 0.0;
        }
    }

    /** Sets the enabled state explicitly. */
    public void setEnabled(boolean state) {
        this.enabled = state;
        if (!state) {
            currentEffectiveMultiplier = 1.0;
            currentBoostAccumulator = 0.0;
        }
    }

    /** Returns the current interpolated effective multiplier. */
    public double getCurrentEffectiveMultiplier() {
        return currentEffectiveMultiplier;
    }

    /** Returns whether the module is currently active. */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Smoothly returns the effective multiplier towards 1.0 (vanilla speed)
     * when the module is disabled, preventing abrupt speed changes.
     */
    private void smoothReturnToNormal() {
        double diff = 1.0 - currentEffectiveMultiplier;
        if (Math.abs(diff) < SNAP_THRESHOLD) {
            currentEffectiveMultiplier = 1.0;
        } else {
            currentEffectiveMultiplier += diff * 0.15;
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
