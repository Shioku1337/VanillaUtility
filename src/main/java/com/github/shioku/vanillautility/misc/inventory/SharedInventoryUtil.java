package com.github.shioku.vanillautility.misc.inventory;

import static com.github.shioku.vanillautility.VanillaUtility.formatColors;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

@Getter
public class SharedInventoryUtil {

  public static String SHARED_INVENTORY_TITLE = formatColors("&c&rCoordinate Storage");

  private final Inventory inventory;

  public SharedInventoryUtil() {
    this.inventory = Bukkit.createInventory(null, 9, SHARED_INVENTORY_TITLE);
  }
}
