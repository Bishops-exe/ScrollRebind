package me.bishops_exe.scrollrebind.client.argumentTypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class KeybindArgumentType implements ArgumentType<String> {
  public static final SimpleCommandExceptionType INVALID_EXCEPTION = new SimpleCommandExceptionType(
      Component.translatable("scrollrebind.keybind.invalid")
  );
  public static ArrayList<String> KEYBINDS = new ArrayList<>();

  static {
    for (KeyMapping key : Minecraft.getInstance().options.keyMappings) {
      KEYBINDS.add(key.getName());
    }
  }

  @Override
  public String parse(StringReader reader) throws CommandSyntaxException {
    String value = reader.readUnquotedString();
    if (KEYBINDS.contains(value)) {
      return value;
    } else {
      throw INVALID_EXCEPTION.create();
    }
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {
    return SharedSuggestionProvider.suggest(KEYBINDS, builder);
  }
}
