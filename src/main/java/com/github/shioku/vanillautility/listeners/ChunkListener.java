package com.github.shioku.vanillautility.listeners;

import com.github.shioku.vanillautility.VanillaUtility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    Chunk[] chunks = plugin.getChunkConfig().getObject("chunks", Chunk[].class, new Chunk[0]);

    for (Chunk chunk : chunks) {
      chunk.addPluginChunkTicket(this.plugin);
    }
  }

  @EventHandler
  public void onWorldUnload(WorldUnloadEvent event) {
    List<Chunk> chunks = new ArrayList<>(event.getWorld().getPluginChunkTickets().get(this.plugin));

    plugin.getChunkConfig().set("chunks", chunks);
    try {
      plugin.getChunkConfig().save(plugin.getChunkFile());
    } catch (IOException e) {
      plugin.getLogger().severe("Failed to save chunks config file.");
    }
  }
}
