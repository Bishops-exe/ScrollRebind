package me.bishops_exe.scrollrebind.client.utils;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import me.bishops_exe.scrollrebind.client.Config;
import me.bishops_exe.scrollrebind.client.argumentTypes.DirectionArgumentType.ScrollDirection;

public record CommandReturner(FabricClientCommandSource source) {
  public CommandReturner(CommandContext<FabricClientCommandSource> context) {
    this(context.getSource());
  }

  public void printTranslationKey(String key, Object... args) {
    source.sendFeedback(Component.translatable(key, args));
  }

  public void printSet(ScrollDirection direction, String text) {
    printTranslationKey("scrollrebind.return.set", direction, text);
  }

  public void printUnset(ScrollDirection direction) {
    printTranslationKey("scrollrebind.return.unset", direction);
  }

  public void printBinds() {
    Config inst = Config.getInstance();

    String up = inst.getBind(ScrollDirection.UP);
    String down = inst.getBind(ScrollDirection.DOWN);

    printTranslationKey("scrollrebind.return.state", up, down);
  }

  public void printState() {
    String translationKey = Config.getInstance().isEnabled() ? "scrollrebind.return.enabled" : "scrollrebind.return.disabled";

    printTranslationKey(translationKey);
  }
}
