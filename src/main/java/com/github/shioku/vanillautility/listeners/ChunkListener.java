package com.github.shioku.vanillautility.listeners;

import com.github.shioku.vanillautility.VanillaUtility;
import java.io.IOException;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

@RequiredArgsConstructor
public class ChunkListener implements Listener {

  private final VanillaUtility plugin;

  @EventHandler
  public void onWorldLoad(WorldLoadEvent event) {
    plugin.setChunkConfig(YamlConfiguration.loadConfiguration(plugin.getChunkFile()));
    Chunk[] chunks = plugin.getChunkConfig().getObject("chunks." + event.getWorld().getUID(), Chunk[].class, new Chunk[0]);

    for (Chunk chunk : chunks) {
      if (!event.getWorld().getUID().equals(chunk.getWorld().getUID())) continue;

      chunk.addPluginChunkTicket(this.plugin);
    }
  }

  @EventHandler
  public void onWorldUnload(WorldUnloadEvent event) {
    Chunk[] chunks = new ArrayList<>(event.getWorld().getPluginChunkTickets().get(this.plugin)).toArray(new Chunk[0]);

    plugin.getChunkConfig().set("chunks." + event.getWorld().getUID(), chunks);

    try {
      plugin.getChunkConfig().save(plugin.getChunkFile());
    } catch (IOException e) {
      plugin.getLogger().severe("Failed to save chunks config file.");
    }
  }
}
