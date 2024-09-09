package com.github.shioku.vanillautility.listeners;

import static com.github.shioku.vanillautility.VanillaUtility.LOADED_CHUNKS;

import com.github.shioku.vanillautility.VanillaUtility;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.World;
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

    List<String> chunkXZ = LOADED_CHUNKS.get(world.getUID().toString());

    if (chunkXZ == null) return;

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

    if (!world.getPluginChunkTickets().containsKey(plugin)) return;

    for (Chunk chunk : world.getPluginChunkTickets().get(plugin)) {
      chunkXZ.add(chunk.getX() + "," + chunk.getZ());
    }

    LOADED_CHUNKS.computeIfAbsent(world.getName(), k -> new ArrayList<>());

    LOADED_CHUNKS.put(world.getUID().toString(), chunkXZ);
  }
}
