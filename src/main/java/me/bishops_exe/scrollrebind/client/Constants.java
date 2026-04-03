package me.bishops_exe.scrollrebind.client;

import java.io.File;
import net.fabricmc.loader.api.FabricLoader;

public class Constants {
  public static final File CONFIG_DIR = FabricLoader
      .getInstance()
      .getConfigDir()
      .resolve("scrollrebind")
      .toFile();
  public static final File CONFIG_FILE = new File(CONFIG_DIR, "config.json");
}
