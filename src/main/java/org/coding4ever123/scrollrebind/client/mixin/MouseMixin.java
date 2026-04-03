package org.coding4ever123.scrollrebind.client.mixin;

import java.util.Arrays;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.option.KeyBinding;
import org.coding4ever123.scrollrebind.client.Config;
import org.coding4ever123.scrollrebind.client.argumentTypes.DirectionArgumentType.ScrollDirection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin  {

  @Shadow
  @Final
  private MinecraftClient client;

  @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V"), cancellable = true)
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

    KeyBinding keybind = Arrays.stream(client.options.allKeys)
        .filter(bind -> bind.getId().equals(keybindId)).findFirst().orElse(null);
    if (keybind == null) {
      return;
    }
    
    keybind.timesPressed++;
  }
}
