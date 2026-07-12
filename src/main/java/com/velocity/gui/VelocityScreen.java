package com.velocity.gui;

import com.velocity.VelocityMod;
import com.velocity.config.ConfigManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

public class VelocityScreen extends Screen {

    private final ConfigManager config = VelocityMod.getInstance().getConfigManager();

    public VelocityScreen() {
        super(Text.literal("Velocity"));
    }

    @Override
    protected void init() {
        int w = 130;
        int x = this.width - w - 5;
        int y = this.height - 90;

        this.addDrawableChild(new SliderWidget(
                x, y, w, 14, Text.literal("Speed"),
                1.0, 10.0, config.getSpeedMultiplier(),
                config::setSpeedMultiplier
        ));

        y += 18;

        this.addDrawableChild(new SliderWidget(
                x, y, w, 14, Text.literal("Accel"),
                0.1, 1.0, config.getAcceleration(),
                config::setAcceleration
        ));

        y += 18;

        this.addDrawableChild(CheckboxWidget.builder(
                Text.literal("Sprint"), this.textRenderer
        ).pos(x, y).checked(config.isSprintBoost()).callback((b, c) -> config.setSprintBoost(c)).build());

        y += 16;

        this.addDrawableChild(CheckboxWidget.builder(
                Text.literal("Jump"), this.textRenderer
        ).pos(x, y).checked(config.isJumpBoost()).callback((b, c) -> config.setJumpBoost(c)).build());

        y += 18;

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Done"), b -> this.close())
                        .dimensions(x, y, 50, 14).build()
        );
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
