package org.coding4ever123.scrollrebind.client.argumentTypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class KeybindArgumentType implements ArgumentType<String> {
  public static final DynamicCommandExceptionType INVALID_EXCEPTION = new DynamicCommandExceptionType(
      (x) -> Text.stringifiedTranslatable("scrollrebind.keybind.invalid")
  );
  public static ArrayList<String> KEYBINDS = new ArrayList<>();

  static {
    for (KeyBinding key : MinecraftClient.getInstance().options.allKeys) {
      KEYBINDS.add(key.getId());
    }
  }

  @Override
  public String parse(StringReader reader) throws CommandSyntaxException {
    String value = reader.readUnquotedString();
    if (KEYBINDS.contains(value)) {
      return value;
    } else {
      throw INVALID_EXCEPTION.create(reader);
    }
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {
    return CommandSource.suggestMatching(KEYBINDS, builder);
  }
}
