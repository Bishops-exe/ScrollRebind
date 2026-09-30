package me.bishops_exe.scrollrebind.client.duck;

/**
 * Implemented on {@code KeyMapping} by
 * {@link me.bishops_exe.scrollrebind.client.mixin.KeyMappingMixin}.
 *
 * <p>Lives outside the mixin package so the mixin config doesn't treat it as a mixin itself.
 */
public interface ScrollableKeyMapping {

  /**
   * Bumps the keybind's pending click count by one, as if it had been pressed.
   *
   * <p>Prefixed to avoid clashing with another mod injecting a method of the same descriptor.
   */
  void scrollrebind$incrementTimesClicked();
}