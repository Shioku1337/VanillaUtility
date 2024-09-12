package com.github.shioku.vanillautility.listeners;

import static com.github.shioku.vanillautility.VanillaUtility.PREFIX;
import static com.github.shioku.vanillautility.misc.inventory.SharedInventoryUtil.SHARED_INVENTORY_TITLE;

import com.github.shioku.vanillautility.VanillaUtility;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@RequiredArgsConstructor
public class InventoryListener implements Listener {

  private final VanillaUtility plugin;

  @EventHandler
  public void onClick(InventoryClickEvent event) {
    HumanEntity humanEntity = event.getWhoClicked();

    if (
      (event.getView().getTitle().equals(SHARED_INVENTORY_TITLE) && humanEntity.getItemOnCursor().getType() == Material.WRITABLE_BOOK) ||
      humanEntity.getItemOnCursor().getType() == Material.WRITTEN_BOOK
    ) return;

    event.setCancelled(true);
    Bukkit.getScheduler().runTask(plugin, humanEntity::closeInventory);

    Audience audience = plugin.adventure().sender(event.getWhoClicked());

    audience.sendMessage(
      MiniMessage.miniMessage()
        .deserialize(PREFIX + "You may not put any other items than <red>Book & Quill</red> or <red>Signed Book</red> into the shared inventory!")
    );
  }
}
