package org.coding4ever123.scrollrebind.client;

import static org.coding4ever123.scrollrebind.client.Constants.CONFIG_DIR;
import static org.coding4ever123.scrollrebind.client.Constants.CONFIG_FILE;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import org.coding4ever123.scrollrebind.client.argumentTypes.DirectionArgumentType.ScrollDirection;

public class Config {
  private static final Gson gson = new GsonBuilder().create();
  private static final Config instance = Config.load();
  HashMap<ScrollDirection, String> bindMap = new HashMap<>();



  boolean enabled = true;

  public Config() {
  }

  public String serialize() {
    return gson.toJson(this, Config.class);
  }

  public static Config load() {
    CONFIG_DIR.mkdirs();
    if (!CONFIG_FILE.exists()) {
      new Config().save();
      return load();
    }
    try {
      String json = Files.readString(CONFIG_FILE.toPath(), StandardCharsets.UTF_8);
      return load(json);
    } catch (IOException e) {
      throw new RuntimeException(
          "Unable to load config from file: %s".formatted(CONFIG_FILE.getAbsolutePath()), e);
    }
  }

  public static Config load(String json) {
    return gson.fromJson(json, Config.class);
  }

  public void save() {
    String json = this.serialize();
    this.save(json);
  }

  public void save(String json) {
    try {
      PrintWriter out = new PrintWriter(CONFIG_FILE);
      out.println(json);
      out.close();
    } catch (Exception e) {
      throw new RuntimeException(
          "Unable to save config to file: %s".formatted(CONFIG_FILE.getAbsolutePath()), e);
    }
  }

  public static Config getInstance() {
    return instance;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean isEnabled) {
    this.enabled = isEnabled;
    save();
  }

  public void unbind(ScrollDirection scrollDirection) {
    if (scrollDirection == ScrollDirection.BOTH) {
      return;
    }
    bindMap.remove(scrollDirection);
    save();
  }

  public void bind(ScrollDirection scrollDirection, String keybindKey) {
    if (scrollDirection == ScrollDirection.BOTH) {
      return;
    }
    bindMap.put(scrollDirection, keybindKey);
    save();
  }
  public String getBind(ScrollDirection scrollDirection) {
    return bindMap.get(scrollDirection);
  }

}
