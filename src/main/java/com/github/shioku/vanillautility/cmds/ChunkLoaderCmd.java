package com.github.shioku.vanillautility.cmds;

import static com.github.shioku.vanillautility.VanillaUtility.PREFIX;
import static com.github.shioku.vanillautility.VanillaUtility.formatColors;

import com.github.shioku.vanillautility.VanillaUtility;
import com.github.shioku.vanillautility.misc.StringUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class ChunkLoaderCmd implements TabExecutor {

  private final VanillaUtility plugin;

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(PREFIX + formatColors("Chunks can only be chunk-loaded by players!"));
      return true;
    }

    if (args.length < 1 || args.length > 3) {
      sender.sendMessage(StringUtil.getSyntaxError(cmd));
      return true;
    }

    if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove")) {
      player.sendMessage(StringUtil.getSyntaxError(cmd));
      return true;
    }

    // Handling for /chunkloader add
    if (args[0].equalsIgnoreCase("add")) {
      if (args.length == 1) {
        if (player.getWorld().getChunkAt(player.getLocation()).getPluginChunkTickets().contains(this.plugin)) {
          player.sendMessage(PREFIX + formatColors("Your chunk is &calready chunk-loaded&7. You can &cremove &7it using &c/chunkloader remove"));
          return true;
        }

        player.getWorld().getChunkAt(player.getLocation()).addPluginChunkTicket(this.plugin);
        player.sendMessage(PREFIX + formatColors("Your current chunk has been &achunk-loaded&7."));
        return true;
      }

      handleXZChunkLoading(cmd, args, player);
      return true;
    }

    // Handling for /chunkloader remove
    if (args.length == 1) {
      if (!player.getWorld().getChunkAt(player.getLocation()).getPluginChunkTickets().contains(this.plugin)) {
        player.sendMessage(PREFIX + formatColors("Your chunk is &cnot chunk-loaded&7. You can &aadd &7it using &c/chunkloader add"));
        return true;
      }

      player.getWorld().getChunkAt(player.getLocation()).removePluginChunkTicket(this.plugin);
      player.sendMessage(PREFIX + formatColors("Your current chunk has been &cchunk-unloaded&7."));
      return true;
    }

    // Handling for /chunkloader add,remove all,(x,z)
    handleXZChunkLoading(cmd, args, player);
    return true;
  }

  private void handleXZChunkLoading(@NotNull Command cmd, @NotNull String[] args, Player player) {
    int x;
    int z;

    // pre-checks for /chunkloader remove all
    if (args.length == 2) {
      if (args[0].equalsIgnoreCase("add")) {
        player.sendMessage(StringUtil.getSyntaxError(cmd));
        player.sendMessage(PREFIX + formatColors("Using \"&call\" &7is only possible when &cchunk-unloading &7chunks."));
        return;
      }

      if (!args[1].equalsIgnoreCase("all")) {
        player.sendMessage(StringUtil.getSyntaxError(cmd));
        return;
      }

      player.getWorld().removePluginChunkTickets(this.plugin);
      player.sendMessage(PREFIX + formatColors("All chunks in your current world have been &cchunk-unloaded&7."));
      return;
    }

    // set X,Z
    try {
      x = Integer.parseInt(args[1]);
      z = Integer.parseInt(args[2]);
    } catch (NumberFormatException ex) {
      player.sendMessage(StringUtil.getSyntaxError(cmd));
      return;
    }

    // Handling for /chunkloader add (x,z)
    if (args[0].equalsIgnoreCase("add")) {
      if (player.getWorld().getChunkAt(x, z).getPluginChunkTickets().contains(this.plugin)) {
        player.sendMessage(
          PREFIX +
          formatColors(
            "The chunk at &6(" + x + ", " + z + ") &7is &calready chunk-loaded&7. You can &cremove &7it using &c/chunkloader remove (x,z)"
          )
        );
        return;
      }

      player.getWorld().addPluginChunkTicket(x, z, this.plugin);
      player.sendMessage(PREFIX + formatColors("The chunk at &6(" + x + ", " + z + ") &7has been &achunk-loaded."));
      return;
    }

    // Handling for /chunkloader remove (x,z)
    if (!player.getWorld().getChunkAt(x, z).getPluginChunkTickets().contains(this.plugin)) {
      player.sendMessage(
        PREFIX + formatColors("The chunk at &6(" + x + ", " + z + ") &7is &cnot chunk-loaded&7. You can &aadd &7it using &c/chunkloader add (x,z)")
      );
      return;
    }

    player.getWorld().removePluginChunkTicket(x, z, this.plugin);
    player.sendMessage(PREFIX + formatColors("The chunk at &6(" + x + ", " + z + ") &7has been &cchunk-unloaded."));
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player) || args.length < 1 || args.length > 3) return List.of();

    if (args.length == 1) {
      return new ArrayList<>() {
        {
          add("add");
          add("remove");
        }
      };
    }

    if (args.length == 2) {
      return new ArrayList<>() {
        {
          add("" + (int) player.getLocation().getX());
        }
      };
    }

    return new ArrayList<>() {
      {
        add("" + (int) player.getLocation().getZ());
      }
    };
  }
}
