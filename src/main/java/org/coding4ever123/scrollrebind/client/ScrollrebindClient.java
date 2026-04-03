package org.coding4ever123.scrollrebind.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import org.coding4ever123.scrollrebind.client.argumentTypes.DirectionArgumentType;
import org.coding4ever123.scrollrebind.client.argumentTypes.DirectionArgumentType.ScrollDirection;
import org.coding4ever123.scrollrebind.client.argumentTypes.KeybindArgumentType;
import org.coding4ever123.scrollrebind.client.utils.CommandReturner;

public class ScrollrebindClient implements ClientModInitializer {

  public void commandReg(CommandDispatcher<FabricClientCommandSource> dispatcher) {
    dispatcher.register(
        ClientCommandManager.literal("scrollrebind")
            .then(
                ClientCommandManager.literal("set").then(
                    ClientCommandManager.argument("direction", new DirectionArgumentType())
                        .then(
                            ClientCommandManager.argument("keybind", new KeybindArgumentType())
                                .executes(ctx -> {
                                  CommandReturner cmdr = new CommandReturner(ctx);
                                  ScrollDirection direction = ctx.getArgument("direction",
                                      ScrollDirection.class);
                                  String keybind = ctx.getArgument("keybind", String.class);

                                  for (ScrollDirection scrollDirection : direction.toList()) {
                                    Config.getInstance().bind(scrollDirection, keybind);
                                    cmdr.printSet(scrollDirection, keybind);
                                  }

                                  cmdr.printBinds();
                                  return 1;
                                })
                        )
                )
            ).then(
                ClientCommandManager.literal("unset").then(
                    ClientCommandManager.argument("direction", new DirectionArgumentType())
                        .executes(ctx -> {
                          CommandReturner cmdr = new CommandReturner(ctx);
                          ScrollDirection direction = ctx.getArgument("direction",
                              ScrollDirection.class);

                          for (ScrollDirection scrollDirection : direction.toList()) {
                            Config.getInstance().unbind(scrollDirection);
                            cmdr.printUnset(scrollDirection);
                          }

                          cmdr.printBinds();
                          return 1;
                        })
                )
            )
            .then(ClientCommandManager.literal("enable").executes((ctx) -> {
              Config.getInstance().setEnabled(true);
              new CommandReturner(ctx).printState();
              return 1;
            }))
            .then(ClientCommandManager.literal("disable").executes((ctx) -> {
              Config.getInstance().setEnabled(false);
              new CommandReturner(ctx).printState();
              return 1;
            }))
    );
  }

  @Override
  public void onInitializeClient() {
    ClientCommandRegistrationCallback.EVENT.register((x, y) -> commandReg(x));
  }

}
