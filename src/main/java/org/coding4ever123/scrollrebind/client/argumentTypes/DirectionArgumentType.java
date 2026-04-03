package org.coding4ever123.scrollrebind.client.argumentTypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.coding4ever123.scrollrebind.client.argumentTypes.DirectionArgumentType.ScrollDirection;

public class DirectionArgumentType implements ArgumentType<ScrollDirection> {

  public static final DynamicCommandExceptionType INVALID_EXCEPTION = new DynamicCommandExceptionType(
      (x) -> Text.stringifiedTranslatable("scrollrebind.direction.invalid")
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
      default -> throw INVALID_EXCEPTION.create(reader);
    };
  }


  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {
    return CommandSource.suggestMatching(List.of("UP", "DOWN", "BOTH"), builder);
  }


}
