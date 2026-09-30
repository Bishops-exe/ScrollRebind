package me.bishops_exe.scrollrebind.client.argumentTypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import me.bishops_exe.scrollrebind.client.argumentTypes.DirectionArgumentType.ScrollDirection;

public class DirectionArgumentType implements ArgumentType<ScrollDirection> {

  public static final SimpleCommandExceptionType INVALID_EXCEPTION = new SimpleCommandExceptionType(
      Component.translatable("scrollrebind.direction.invalid")
  );

  public enum ScrollDirection {
    UP,
    DOWN,
    BOTH;

    public static Iterable<String> getSuggestions() {
      return Arrays.stream(ScrollDirection.values()).map(Object::toString).toList();
    }

    public static ScrollDirection parse(String value) throws CommandSyntaxException {
      try {
        return ScrollDirection.valueOf(value.toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException e) {
        throw INVALID_EXCEPTION.create();
      }
    }

    public List<ScrollDirection> toList() {
      return this == BOTH ? List.of(UP, DOWN) : List.of(this);
    }
  }

  @Override
  public ScrollDirection parse(StringReader reader) throws CommandSyntaxException {
    return ScrollDirection.parse(reader.readString());
  }


  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {
    return SharedSuggestionProvider.suggest(ScrollDirection.getSuggestions(), builder);
  }


}
