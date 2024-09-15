package com.github.shioku.vanillautility.cmds;

import static com.github.shioku.vanillautility.VanillaUtility.PREFIX;
import static com.github.shioku.vanillautility.misc.StringUtil.mm;

import com.github.shioku.vanillautility.VanillaUtility;
import com.github.shioku.vanillautility.misc.StringUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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
      Audience audience1 = plugin.adventure().sender(Bukkit.getConsoleSender());

      audience1.sendMessage(
        MiniMessage.miniMessage()
          .deserialize(
            PREFIX + "Your chunk is <red>already chunk-loaded<gray>. You can <red>remove <gray>it using <red>/" + cmd.getLabel() + " remove<gray>."
          )
      );
      this.plugin.getLogger().info("Chunks can only be chunk-loaded by players!");
      return true;
    }

    Audience audience = plugin.adventure().player(player);

    if (args.length < 1 || args.length > 3) {
      audience.sendMessage(StringUtil.getSyntax(cmd));
      return true;
    }

    if (!args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove")) {
      audience.sendMessage(StringUtil.getSyntax(cmd));
      return true;
    }

    // Handling for /chunkloader add
    if (args[0].equalsIgnoreCase("add")) {
      if (args.length == 1) {
        if (!player.getWorld().getChunkAt(player.getLocation()).addPluginChunkTicket(this.plugin)) {
          audience.sendMessage(
            mm(
              PREFIX +
              "Your chunk is <red>already chunk-loaded<gray>. You can <red>remove <gray>it using <red>/" +
              cmd.getLabel() +
              " remove<gray>."
            )
          );
          return true;
        }

        audience.sendMessage(mm(PREFIX + "Your current chunk has been <green>chunk-loaded<gray>."));
        return true;
      }

      handleXZChunkLoading(cmd, args, player, audience);
      return true;
    }

    // Handling for /chunkloader remove
    if (args.length == 1) {
      if (!player.getWorld().getChunkAt(player.getLocation()).removePluginChunkTicket(this.plugin)) {
        audience.sendMessage(
          mm(PREFIX + "Your chunk is <red>not chunk-loaded<gray>. You can <green>add <gray>it using <red>/" + cmd.getLabel() + " add<gray>.")
        );
        return true;
      }

      audience.sendMessage(mm(PREFIX + "Your current chunk has been <red>chunk-unloaded<green>."));
      return true;
    }

    // Handling for /chunkloader add,remove all,(x,z)
    handleXZChunkLoading(cmd, args, player, audience);
    return true;
  }

  private void handleXZChunkLoading(@NotNull Command cmd, @NotNull String[] args, Player player, Audience audience) {
    // Handling for /chunkloader remove all
    if (args.length == 2) {
      if (args[0].equalsIgnoreCase("add")) {
        audience.sendMessage(StringUtil.getSyntax(cmd));
        audience.sendMessage(mm(PREFIX + "Using \"<red>all<gray>\" is only possible when <red>chunk-unloading <gray>chunks."));
        return;
      }

      if (!args[1].equalsIgnoreCase("all")) {
        audience.sendMessage(StringUtil.getSyntax(cmd));
        return;
      }

      player.getWorld().removePluginChunkTickets(this.plugin);
      audience.sendMessage(mm(PREFIX + "All chunks in your current world have been <red>chunk-unloaded<gray>."));
      return;
    }

    int x;
    int z;

    // set X,Z
    try {
      x = Integer.parseInt(args[1]) >> 4; // convert to chunk coordinates
      z = Integer.parseInt(args[2]) >> 4; // convert to chunk coordinates
    } catch (NumberFormatException ex) {
      audience.sendMessage(StringUtil.getSyntax(cmd));
      return;
    }

    Chunk chosenChunk = player.getWorld().getChunkAt(x, z);

    // Handling for /chunkloader add (x,z)
    if (args[0].equalsIgnoreCase("add")) {
      if (!player.getWorld().addPluginChunkTicket(x, z, this.plugin)) {
        audience.sendMessage(
          mm(
            PREFIX +
            "The chunk at <gold>(Chunk: " +
            chosenChunk.getX() +
            ", " +
            chosenChunk.getZ() +
            ") <gray>is <red>already chunk-loaded<gray>. You can <red>remove <gray>it using <red>/" +
            cmd.getLabel() +
            " remove (x,z)"
          )
        );
        return;
      }

      audience.sendMessage(
        mm(PREFIX + "The chunk at <gold>(Chunk: " + chosenChunk.getX() + ", " + chosenChunk.getZ() + ") <gray>has been <green>chunk-loaded.")
      );
      return;
    }

    // Handling for /chunkloader remove (x,z)
    if (!player.getWorld().removePluginChunkTicket(x, z, this.plugin)) {
      audience.sendMessage(
        mm(
          PREFIX +
          "The chunk at <gold>(Chunk: " +
          chosenChunk.getX() +
          ", " +
          chosenChunk.getZ() +
          ") <gray>is <red>not chunk-loaded<gray>. You can <green>add <gray>it using <red>/" +
          cmd.getLabel() +
          " add (x,z)<gray>."
        )
      );
      return;
    }

    audience.sendMessage(
      mm(PREFIX + "The chunk at <gold>(Chunk: " + chosenChunk.getX() + ", " + chosenChunk.getZ() + ") <gray>has been <red>chunk-unloaded<gray>.")
    );
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
