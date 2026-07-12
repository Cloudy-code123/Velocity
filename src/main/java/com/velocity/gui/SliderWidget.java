package com.velocity.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.function.DoubleConsumer;

/**
 * A custom horizontal slider widget for adjusting double values within a range.
 * Supports mouse click and drag for smooth value selection.
 */
public class SliderWidget extends ClickableWidget {

    private double normalizedValue;
    private final double minValue;
    private final double maxValue;
    private final Text label;
    private final DoubleConsumer onChange;

    // Rendering constants
    private static final int TRACK_HEIGHT = 10;
    private static final int HANDLE_WIDTH = 4;

    /**
     * Creates a new slider widget.
     *
     * @param x            the x position
     * @param y            the y position
     * @param width        the total widget width
     * @param height       the total widget height (including label area)
     * @param label        the label to display above the slider
     * @param minValue     the minimum value
     * @param maxValue     the maximum value
     * @param currentValue the initial value
     * @param onChange      callback when the value changes
     */
    public SliderWidget(int x, int y, int width, int height, Text label,
                        double minValue, double maxValue, double currentValue,
                        DoubleConsumer onChange) {
        super(x, y, width, height, label);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.label = label;
        this.onChange = onChange;

        double range = maxValue - minValue;
        this.normalizedValue = (range > 0) ? (currentValue - minValue) / range : 0.0;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        int trackX = this.getX();
        int trackY = this.getY() + 14;
        int trackWidth = this.getWidth();
        int handleX = (int) (trackX + trackWidth * this.normalizedValue);

        // Track background
        context.fill(trackX, trackY, trackX + trackWidth, trackY + TRACK_HEIGHT, 0xFF404040);

        // Filled portion
        context.fill(trackX, trackY, handleX, trackY + TRACK_HEIGHT, 0xFF5599FF);

        // Handle
        context.fill(
                handleX - HANDLE_WIDTH / 2, trackY - 1,
                handleX + HANDLE_WIDTH / 2, trackY + TRACK_HEIGHT + 1,
                0xFFDDDDDD
        );

        // Label with current value
        double actualValue = getActualValue();
        String displayText = label.getString() + ": " + String.format("%.1f", actualValue);
        context.drawCenteredTextWithShadow(
                client.textRenderer,
                displayText,
                this.getX() + this.getWidth() / 2,
                this.getY(),
                0xFFFFFF
        );
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        updateValueFromMouse(mouseX);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        updateValueFromMouse(mouseX);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button,
                                double deltaX, double deltaY) {
        // Allow dragging outside widget bounds for better UX
        if (this.active && this.visible && button == 0) {
            this.onDrag(mouseX, mouseY, deltaX, deltaY);
            return true;
        }
        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
    }

    /**
     * Updates the slider value based on the mouse X position.
     *
     * @param mouseX the current mouse X coordinate
     */
    private void updateValueFromMouse(double mouseX) {
        double relative = (mouseX - this.getX()) / (double) this.getWidth();
        this.normalizedValue = clamp(relative, 0.0, 1.0);

        double actualValue = getActualValue();
        // Round to 1 decimal place for clean values
        actualValue = Math.round(actualValue * 10.0) / 10.0;
        onChange.accept(actualValue);
    }

    /** Returns the current actual value within the configured range. */
    public double getActualValue() {
        return minValue + normalizedValue * (maxValue - minValue);
    }

    /** Sets the slider to a specific actual value. */
    public void setActualValue(double value) {
        double range = maxValue - minValue;
        if (range > 0) {
            this.normalizedValue = clamp((value - minValue) / range, 0.0, 1.0);
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
