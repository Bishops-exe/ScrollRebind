package me.bishops_exe.scrollrebind.client;

// Fabric API renamed the client command builder factory in the 26.1 release line.
//? if >=26.1 {
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
//?} else {
/*import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
*///?}

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import me.bishops_exe.scrollrebind.client.argumentTypes.DirectionArgumentType;
import me.bishops_exe.scrollrebind.client.argumentTypes.DirectionArgumentType.ScrollDirection;
import me.bishops_exe.scrollrebind.client.argumentTypes.KeybindArgumentType;
import me.bishops_exe.scrollrebind.client.utils.CommandReturner;

public class ScrollrebindClient implements ClientModInitializer {
  public void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, Object context) {
    registerCommand(dispatcher);
  }

  public void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
    dispatcher.register(
        literal("scrollrebind")
            .then(
                literal("set").then(
                    argument("direction", new DirectionArgumentType())
                        .then(
                            argument("keybind", new KeybindArgumentType())
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
                literal("unset").then(
                    argument("direction", new DirectionArgumentType())
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
            .then(literal("enable").executes((ctx) -> {
              Config.getInstance().setEnabled(true);
              new CommandReturner(ctx).printState();
              return 1;
            }))
            .then(literal("disable").executes((ctx) -> {
              Config.getInstance().setEnabled(false);
              new CommandReturner(ctx).printState();
              return 1;
            }))
    );
  }

  @Override
  public void onInitializeClient() {
    ClientCommandRegistrationCallback.EVENT.register(this::registerCommand);
  }

}