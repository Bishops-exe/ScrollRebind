package me.bishops_exe.scrollrebind.client.argumentTypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
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

    public List<ScrollDirection> toList() {
      return this == BOTH ? List.of(UP, DOWN) : List.of(this);
    }
  }

  public DirectionArgumentType() {
  }

  @Override
  public ScrollDirection parse(StringReader reader) throws CommandSyntaxException {
    return switch (reader.readString().toLowerCase()) {
      case "up" -> ScrollDirection.UP;
      case "down" -> ScrollDirection.DOWN;
      case "both" -> ScrollDirection.BOTH;
      default -> throw INVALID_EXCEPTION.create();
    };
  }


  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {
    return SharedSuggestionProvider.suggest(List.of("UP", "DOWN", "BOTH"), builder);
  }


}
