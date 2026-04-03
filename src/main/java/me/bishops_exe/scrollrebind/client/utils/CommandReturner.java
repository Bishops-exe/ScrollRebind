package me.bishops_exe.scrollrebind.client.utils;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import me.bishops_exe.scrollrebind.client.Config;
import me.bishops_exe.scrollrebind.client.argumentTypes.DirectionArgumentType.ScrollDirection;

public class CommandReturner {

  private final FabricClientCommandSource source;

  public CommandReturner(CommandContext<FabricClientCommandSource> context) {
    this(context.getSource());
  }

  public CommandReturner(FabricClientCommandSource source) {
    this.source = source;
  }

  public void printSet(ScrollDirection direction, String text) {
    source.sendFeedback(Component.translatable("scrollrebind.return.set", direction, text));
  }

  public void printUnset(ScrollDirection direction) {
    source.sendFeedback(Component.translatable("scrollrebind.return.unset", direction));
  }

  public void printBinds() {
    Config inst = Config.getInstance();

    String up = inst.getBind(ScrollDirection.UP);
    String down = inst.getBind(ScrollDirection.DOWN);

    source.sendFeedback(Component.translatable("scrollrebind.return.state", up, down));
  }

  public void printState() {
    if (Config.getInstance().isEnabled()) {
      source.sendFeedback(Component.translatable("scrollrebind.return.enabled"));
    } else {
      source.sendFeedback(Component.translatable("scrollrebind.return.disabled"));
    }
  }
}
