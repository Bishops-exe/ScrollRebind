package me.bishops_exe.scrollrebind.client.utils;

import java.lang.reflect.Field;
import net.minecraft.client.KeyMapping;

public class Utils {

  private static final Field CLICK_COUNT_FIELD;

  static {
    try {
      CLICK_COUNT_FIELD = KeyMapping.class.getDeclaredField("clickCount");
      CLICK_COUNT_FIELD.setAccessible(true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void incrementTimesClicked(KeyMapping keybind) {
    try {
      CLICK_COUNT_FIELD.setInt(keybind, CLICK_COUNT_FIELD.getInt(keybind) + 1);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
