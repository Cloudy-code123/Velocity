package com.velocity.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.velocity.VelocityMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Manages persistent JSON configuration for the Velocity mod.
 * Auto-creates defaults on first run and saves changes immediately.
 */
public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("velocity.json");

    // Default values
    private static final double DEFAULT_SPEED_MULTIPLIER = 1.5;
    private static final double DEFAULT_ACCELERATION = 0.5;
    private static final boolean DEFAULT_SPRINT_BOOST = false;
    private static final boolean DEFAULT_JUMP_BOOST = false;

    // Current values
    private double speedMultiplier = DEFAULT_SPEED_MULTIPLIER;
    private double acceleration = DEFAULT_ACCELERATION;
    private boolean sprintBoost = DEFAULT_SPRINT_BOOST;
    private boolean jumpBoost = DEFAULT_JUMP_BOOST;

    /** Loads configuration from disk, or creates defaults if the file does not exist. */
    public void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                JsonObject obj = GSON.fromJson(json, JsonObject.class);

                speedMultiplier = getDouble(obj, "speedMultiplier", DEFAULT_SPEED_MULTIPLIER, 1.0, 10.0);
                acceleration = getDouble(obj, "acceleration", DEFAULT_ACCELERATION, 0.1, 1.0);
                sprintBoost = getBoolean(obj, "sprintBoost", DEFAULT_SPRINT_BOOST);
                jumpBoost = getBoolean(obj, "jumpBoost", DEFAULT_JUMP_BOOST);

                VelocityMod.LOGGER.info("[Velocity] Config loaded from {}", CONFIG_PATH);
            } else {
                save();
                VelocityMod.LOGGER.info("[Velocity] Default config created at {}", CONFIG_PATH);
            }
        } catch (Exception e) {
            VelocityMod.LOGGER.error("[Velocity] Failed to load config, using defaults", e);
        }
    }

    /** Saves the current configuration to disk immediately. */
    public void save() {
        try {
            JsonObject obj = new JsonObject();
            obj.addProperty("speedMultiplier", speedMultiplier);
            obj.addProperty("acceleration", acceleration);
            obj.addProperty("sprintBoost", sprintBoost);
            obj.addProperty("jumpBoost", jumpBoost);

            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(obj));
        } catch (IOException e) {
            VelocityMod.LOGGER.error("[Velocity] Failed to save config", e);
        }
    }

    /** Resets all values to defaults and saves. */
    public void resetToDefaults() {
        speedMultiplier = DEFAULT_SPEED_MULTIPLIER;
        acceleration = DEFAULT_ACCELERATION;
        sprintBoost = DEFAULT_SPRINT_BOOST;
        jumpBoost = DEFAULT_JUMP_BOOST;
        save();
    }

    // --- Getters and setters (each setter saves immediately) ---

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(double value) {
        this.speedMultiplier = clamp(value, 1.0, 10.0);
        save();
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double value) {
        this.acceleration = clamp(value, 0.1, 1.0);
        save();
    }

    public boolean isSprintBoost() {
        return sprintBoost;
    }

    public void setSprintBoost(boolean value) {
        this.sprintBoost = value;
        save();
    }

    public boolean isJumpBoost() {
        return jumpBoost;
    }

    public void setJumpBoost(boolean value) {
        this.jumpBoost = value;
        save();
    }

    // --- Utility methods ---

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double getDouble(JsonObject obj, String key, double def, double min, double max) {
        if (obj.has(key) && obj.get(key).isJsonPrimitive()) {
            try {
                return clamp(obj.get(key).getAsDouble(), min, max);
            } catch (NumberFormatException e) {
                return def;
            }
        }
        return def;
    }

    private static boolean getBoolean(JsonObject obj, String key, boolean def) {
        if (obj.has(key) && obj.get(key).isJsonPrimitive()) {
            try {
                return obj.get(key).getAsBoolean();
            } catch (Exception e) {
                return def;
            }
        }
        return def;
    }
}
