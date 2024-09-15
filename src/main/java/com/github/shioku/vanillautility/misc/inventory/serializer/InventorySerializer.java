package com.github.shioku.vanillautility.misc.inventory.serializer;

import com.github.shioku.vanillautility.VanillaUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jeff_media.jsonconfigurationserialization.JsonConfigurationSerialization;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class InventorySerializer {

  private final VanillaUtility plugin;

  private final Gson gson = new GsonBuilder()
    .disableHtmlEscaping()
    .registerTypeHierarchyAdapter(ItemStack.class, JsonConfigurationSerialization.TYPE_HIERARCHY_ADAPTER)
    .create();

  public void serialize(File json, Inventory inventory) {
    final ItemStack[] contents = inventory.getContents();

    try (FileWriter writer = new FileWriter(json)) {
      gson.toJson(contents, writer);
    } catch (IOException ex) {
      plugin.getLogger().severe("Could not serialize Inventory (IOException). Are the correct permissions set? Does the file exist?");
    }
  }

  @Nullable
  public ItemStack[] deserialize(File json) {
    try (FileReader reader = new FileReader(json)) {
      return gson.fromJson(reader, ItemStack[].class);
    } catch (IOException e) {
      plugin.getLogger().severe("Could not deserialize Inventory (IOException). Are the correct permissions set? Does the file exist?");
      return null;
    }
  }
}
