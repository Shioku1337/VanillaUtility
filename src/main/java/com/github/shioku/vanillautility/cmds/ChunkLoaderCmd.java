package com.github.shioku.vanillautility.cmds;

import static com.github.shioku.vanillautility.VanillaUtility.PREFIX;
import static com.github.shioku.vanillautility.VanillaUtility.formatColors;

import com.github.shioku.vanillautility.VanillaUtility;
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
      sender.sendMessage(PREFIX + formatColors("Chunks can only be loaded by players!"));
      return true;
    }

    if (args.length != 1) {
      sender.sendMessage(formatColors("Syntaxerror! Please use: &c" + cmd.getUsage()));
      return true;
    }

    if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove")) {
      player.sendMessage(formatColors("Syntaxerror! Please use: &c" + cmd.getUsage()));
      return true;
    }

    if (args[0].equalsIgnoreCase("add")) {
      if (player.getWorld().getChunkAt(player.getLocation()).getPluginChunkTickets().contains(this.plugin)) {
        player.sendMessage(PREFIX + formatColors("Your chunk is &calready loaded&7. You can &cremove &7it using &c/chunkloader remove"));
        return true;
      }

      player.getWorld().getChunkAt(player.getLocation()).addPluginChunkTicket(this.plugin);
      player.sendMessage(PREFIX + formatColors("Your current chunk has been &aloaded&7."));
      return true;
    }

    if (!player.getWorld().getChunkAt(player.getLocation()).getPluginChunkTickets().contains(this.plugin)) {
      player.sendMessage(PREFIX + formatColors("Your chunk is &cnot loaded&7. You can &aadd &7it using &c/chunkloader add"));
      return true;
    }

    player.getWorld().getChunkAt(player.getLocation()).removePluginChunkTicket(this.plugin);
    player.sendMessage(PREFIX + formatColors("Your current chunk has been &cunloaded&7."));
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    if (args.length != 1) return List.of();

    return new ArrayList<>() {
      {
        add("add");
        add("remove");
      }
    };
  }
}
