package org.coding4ever123.scrollrebind.client.utils;

import com.mojang.brigadier.context.CommandContext;
import java.awt.Color;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import org.coding4ever123.scrollrebind.client.Config;
import org.coding4ever123.scrollrebind.client.argumentTypes.DirectionArgumentType.ScrollDirection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class CommandReturner {

  private final FabricClientCommandSource source;

  public CommandReturner(CommandContext<FabricClientCommandSource> context) {
    this(context.getSource());
  }

  public CommandReturner(FabricClientCommandSource source) {
    this.source = source;
  }

  public void printSet(ScrollDirection direction, String text) {
    source.sendFeedback(Text.translatable("scrollrebind.return.set", direction, text));
  }

  public void printUnset(ScrollDirection direction) {
    source.sendFeedback(Text.translatable("scrollrebind.return.unset", direction));
  }

  public void printBinds() {
    Config inst = Config.getInstance();

    String up = inst.getBind(ScrollDirection.UP);
    String down = inst.getBind(ScrollDirection.DOWN);

    source.sendFeedback(Text.translatable("scrollrebind.return.state", up, down));
  }

  public void printState() {
    if (Config.getInstance().isEnabled()) {
      source.sendFeedback(Text.translatable("scrollrebind.return.enabled"));
    } else {
      source.sendFeedback(Text.translatable("scrollrebind.return.disabled"));
    }
  }
}
