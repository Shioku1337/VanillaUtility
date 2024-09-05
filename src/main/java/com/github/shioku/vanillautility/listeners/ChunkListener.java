package com.github.shioku.vanillautility.listeners;

import com.github.shioku.vanillautility.VanillaUtility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.World;
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
    World world = event.getWorld();

    List<String> chunkXZ = plugin.getChunkConfig().getStringList("chunks." + world.getUID());

    Chunk[] chunks = new Chunk[chunkXZ.size()];

    for (int i = 0; i < chunks.length; i++) {
      int x;
      int z;

      try {
        x = Integer.parseInt(chunkXZ.get(i).split(",")[0]);
        z = Integer.parseInt(chunkXZ.get(i).split(",")[1]);
      } catch (NumberFormatException e) {
        throw new RuntimeException("Could not parse chunk coordinates from chunks.yml", e);
      }

      chunks[i] = world.getChunkAt(x, z);
    }

    for (Chunk chunk : chunks) {
      if (!chunk.getPluginChunkTickets().contains(plugin)) {
        chunk.addPluginChunkTicket(plugin);
      }
    }
  }

  @EventHandler
  public void onWorldUnload(WorldUnloadEvent event) {
    World world = event.getWorld();

    List<String> chunkXZ = new ArrayList<>();

    for (Chunk chunk : world.getPluginChunkTickets().get(plugin)) {
      chunkXZ.add(chunk.getX() + "," + chunk.getZ());
    }

    plugin.getChunkConfig().set("chunks." + world.getUID(), chunkXZ);

    try {
      plugin.getChunkConfig().save(plugin.getChunkFile());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    plugin.setChunkConfig(YamlConfiguration.loadConfiguration(plugin.getChunkFile()));
  }
}
