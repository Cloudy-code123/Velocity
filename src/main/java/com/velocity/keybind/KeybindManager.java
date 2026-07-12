package com.velocity.keybind;

import com.velocity.VelocityMod;
import com.velocity.gui.VelocityScreen;
import com.velocity.module.VelocityModule;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Manages all keybindings for the Velocity mod.
 * Registers the toggle key (") and the settings GUI key (HOME).
 */
public class KeybindManager {

    private KeyBinding toggleKey;
    private KeyBinding settingsKey;

    /** Initializes and registers all keybindings, then hooks into the client tick. */
    public void init() {
        // Toggle key: " key (apostrophe key on standard layout)
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.velocity.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_APOSTROPHE,
                "category.velocity"
        ));

        // Settings GUI key: HOME
        settingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.velocity.settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_HOME,
                "category.velocity"
        ));

        // Listen for key presses each client tick
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    /**
     * Processes keybind presses each tick.
     *
     * @param client the Minecraft client instance
     */
    private void onClientTick(MinecraftClient client) {
        if (client.player == null) return;

        // Handle toggle key
        while (toggleKey.wasPressed()) {
            if (client.currentScreen != null) continue;

            VelocityModule module = VelocityMod.getInstance().getModuleManager().getVelocityModule();
            module.toggle();
        }

        // Handle settings key — open or close GUI
        while (settingsKey.wasPressed()) {
            if (client.currentScreen instanceof VelocityScreen) {
                client.setScreen(null);
            } else if (client.currentScreen == null) {
                client.setScreen(new VelocityScreen());
            }
        }
    }
}
