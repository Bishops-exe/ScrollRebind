package me.bishops_exe.scrollrebind.client.mixin;

import java.util.Arrays;
import me.bishops_exe.scrollrebind.client.duck.ScrollableKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import me.bishops_exe.scrollrebind.client.Config;
import me.bishops_exe.scrollrebind.client.argumentTypes.DirectionArgumentType.ScrollDirection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {


  @Shadow
  @Final
  private Minecraft minecraft;

  @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"), cancellable = true)
  void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
    if (!Config.getInstance().isEnabled()) {
      return;
    }

    ci.cancel();

    if (vertical == 0) {
      return;
    }

    ScrollDirection direction = vertical > 0 ? ScrollDirection.UP : ScrollDirection.DOWN;
    String keybindId = Config.getInstance().getBind(direction);

    KeyMapping keybind = Arrays.stream(minecraft.options.keyMappings)
        .filter(bind -> bind.getName().equals(keybindId)).findFirst().orElse(null);

    if (keybind == null) {
      return;
    }

    ((ScrollableKeyMapping) keybind).scrollrebind$incrementTimesClicked();
  }
}
