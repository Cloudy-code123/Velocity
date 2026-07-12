package com.velocity.hud;

import com.velocity.VelocityMod;
import com.velocity.config.ConfigManager;
import com.velocity.module.VelocityModule;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudRenderer {

    public void init() {
        VelocityMod.LOGGER.info("[Velocity] HudRenderer registering...");
        HudRenderCallback.EVENT.register((DrawContext ctx, RenderTickCounter tc) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;
            if (client.currentScreen != null) return;

            VelocityModule module = VelocityMod.getInstance().getModuleManager().getVelocityModule();
            if (!module.isEnabled()) return;

            ConfigManager config = VelocityMod.getInstance().getConfigManager();
            var tr = client.textRenderer;
            int sw = client.getWindow().getScaledWidth();
            int sh = client.getWindow().getScaledHeight();

            String line = "Velocity " + String.format("%.1f", config.getSpeedMultiplier()) + "x";
            int tw = tr.getWidth(line);
            int x = sw - tw - 2;
            int y = sh - tr.fontHeight - 38;

            ctx.fill(x - 2, y - 2, x + tw + 2, y + tr.fontHeight + 2, (160 << 24) | 0x333333);
            ctx.drawTextWithShadow(tr, line, x, y, 0x808080);
        });
        VelocityMod.LOGGER.info("[Velocity] HudRenderer registered!");
    }
}
