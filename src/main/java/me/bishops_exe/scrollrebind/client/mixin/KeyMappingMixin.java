package me.bishops_exe.scrollrebind.client.mixin;

import me.bishops_exe.scrollrebind.client.duck.ScrollableKeyMapping;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Adds {@link ScrollableKeyMapping#scrollrebind$incrementTimesClicked()} to {@link KeyMapping}.
 *
 * <p>The counter is private, and reflecting on it by name only works on 26.1+, where Minecraft
 * ships unobfuscated. Shadowing it goes through mixin remapping instead, so it resolves on the
 * obfuscated versions too.
 */
@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin implements ScrollableKeyMapping {

  @Shadow
  private int clickCount;

  @Override
  public void scrollrebind$incrementTimesClicked() {
    this.clickCount++;
  }
}