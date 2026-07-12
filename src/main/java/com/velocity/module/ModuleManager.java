package com.velocity.module;

import com.velocity.VelocityMod;
import com.velocity.config.ConfigManager;

/**
 * Central manager for all mod modules.
 * Provides access to individual module instances.
 */
public class ModuleManager {

    private VelocityModule velocityModule;

    /** Initializes all modules with the current configuration. */
    public void init() {
        ConfigManager config = VelocityMod.getInstance().getConfigManager();
        velocityModule = new VelocityModule(config);
    }

    /** Returns the velocity module instance. */
    public VelocityModule getVelocityModule() {
        return velocityModule;
    }
}
