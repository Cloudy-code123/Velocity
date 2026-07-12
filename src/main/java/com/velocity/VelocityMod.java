package com.velocity;

import com.velocity.command.VelocityCommand;
import com.velocity.config.ConfigManager;
import com.velocity.hud.HudRenderer;
import com.velocity.keybind.KeybindManager;
import com.velocity.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Velocity client mod.
 * Initializes all subsystems: config, modules, keybinds, HUD, and commands.
 */
public class VelocityMod implements ClientModInitializer {

    public static final String MOD_ID = "velocity";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static VelocityMod instance;

    private ConfigManager configManager;
    private ModuleManager moduleManager;
    private KeybindManager keybindManager;
    private HudRenderer hudRenderer;

    @Override
    public void onInitializeClient() {
        instance = this;
        LOGGER.info("[Velocity] Initializing...");

        // Load configuration first — other systems depend on it
        configManager = new ConfigManager();
        configManager.load();

        // Initialize modules
        moduleManager = new ModuleManager();
        moduleManager.init();

        // Register keybinds
        keybindManager = new KeybindManager();
        keybindManager.init();

        // Register HUD overlay
        hudRenderer = new HudRenderer();
        hudRenderer.init();

        // Register client-side commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                VelocityCommand.register(dispatcher)
        );

        LOGGER.info("[Velocity] Initialized successfully.");
    }

    /** Returns the singleton instance of the mod. */
    public static VelocityMod getInstance() {
        return instance;
    }

    /** Returns the configuration manager. */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /** Returns the module manager. */
    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    /** Returns the keybind manager. */
    public KeybindManager getKeybindManager() {
        return keybindManager;
    }

    /** Returns the HUD renderer. */
    public HudRenderer getHudRenderer() {
        return hudRenderer;
    }
}
