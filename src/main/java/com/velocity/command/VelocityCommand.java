package com.velocity.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.velocity.VelocityMod;
import com.velocity.config.ConfigManager;
import com.velocity.module.VelocityModule;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

/**
 * Registers the /velocity command tree with subcommands:
 * on, off, speed, reset.
 * Uses FabricClientCommandSource for client-side command execution.
 */
public final class VelocityCommand {

    private VelocityCommand() {
        // Utility class — no instantiation
    }

    /**
     * Registers the /velocity command with the given dispatcher.
     *
     * @param dispatcher the client command dispatcher
     */
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("velocity")
                .then(ClientCommandManager.literal("on")
                        .executes(ctx -> executeOn(ctx.getSource()))
                )
                .then(ClientCommandManager.literal("off")
                        .executes(ctx -> executeOff(ctx.getSource()))
                )
                .then(ClientCommandManager.literal("speed")
                        .then(ClientCommandManager.argument("value", DoubleArgumentType.doubleArg(1.0, 10.0))
                                .executes(ctx -> executeSpeed(
                                        ctx.getSource(),
                                        DoubleArgumentType.getDouble(ctx, "value")
                                ))
                        )
                )
                .then(ClientCommandManager.literal("reset")
                        .executes(ctx -> executeReset(ctx.getSource()))
                )
        );
    }

    private static int executeOn(FabricClientCommandSource source) {
        VelocityModule module = VelocityMod.getInstance().getModuleManager().getVelocityModule();
        module.setEnabled(true);
        source.sendFeedback(Text.literal("\u00a76[Velocity] \u00a7aEnabled"));
        return 1;
    }

    private static int executeOff(FabricClientCommandSource source) {
        VelocityModule module = VelocityMod.getInstance().getModuleManager().getVelocityModule();
        module.setEnabled(false);
        source.sendFeedback(Text.literal("\u00a76[Velocity] \u00a7cDisabled"));
        return 1;
    }

    private static int executeSpeed(FabricClientCommandSource source, double value) {
        ConfigManager config = VelocityMod.getInstance().getConfigManager();
        config.setSpeedMultiplier(value);
        source.sendFeedback(
                Text.literal(String.format("\u00a76[Velocity] \u00a7eSpeed set to \u00a7f%.1fx", value))
        );
        return 1;
    }

    private static int executeReset(FabricClientCommandSource source) {
        ConfigManager config = VelocityMod.getInstance().getConfigManager();
        VelocityModule module = VelocityMod.getInstance().getModuleManager().getVelocityModule();

        config.resetToDefaults();
        module.setEnabled(false);

        source.sendFeedback(Text.literal("\u00a76[Velocity] \u00a77Settings reset to defaults."));
        return 1;
    }
}
