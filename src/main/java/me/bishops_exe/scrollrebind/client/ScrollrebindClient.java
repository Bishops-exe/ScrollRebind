package me.bishops_exe.scrollrebind.client;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import me.bishops_exe.scrollrebind.client.argumentTypes.DirectionArgumentType;
import me.bishops_exe.scrollrebind.client.argumentTypes.DirectionArgumentType.ScrollDirection;
import me.bishops_exe.scrollrebind.client.argumentTypes.KeybindArgumentType;
import me.bishops_exe.scrollrebind.client.utils.CommandReturner;

public class ScrollrebindClient implements ClientModInitializer {

  public void commandReg(CommandDispatcher<FabricClientCommandSource> dispatcher) {
    dispatcher.register(
        ClientCommands.literal("scrollrebind")
            .then(
                ClientCommands.literal("set").then(
                    ClientCommands.argument("direction", new DirectionArgumentType())
                        .then(
                            ClientCommands.argument("keybind", new KeybindArgumentType())
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
                ClientCommands.literal("unset").then(
                    ClientCommands.argument("direction", new DirectionArgumentType())
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
            .then(ClientCommands.literal("enable").executes((ctx) -> {
              Config.getInstance().setEnabled(true);
              new CommandReturner(ctx).printState();
              return 1;
            }))
            .then(ClientCommands.literal("disable").executes((ctx) -> {
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
