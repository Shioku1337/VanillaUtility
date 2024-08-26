package com.github.shioku.vanillautility;

import com.github.shioku.vanillautility.cmds.ChunkLoaderCmd;
import com.github.shioku.vanillautility.cmds.ScoreboardPlaytimeCmd;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class VanillaUtility extends JavaPlugin {

  public static String PREFIX = formatColors("&8[&3Admin&8] &7");

  public static List<UUID> ACTIVE_PLAYERS = new ArrayList<>();

  @Override
  @SuppressWarnings("ConstantConditions deprecation")
  public void onEnable() {
    // Plugin startup logic

    PluginDescriptionFile descriptionFile = this.getDescription();
    for (String commandName : descriptionFile.getCommands().keySet()) {
      PluginCommand command = this.getCommand(commandName);
      if (command == null) continue;
      command.setPermissionMessage(PREFIX + formatColors("You do &cnot have permission &7to perform this command!")).setUsage(command.getUsage().replace("<label>", command.getLabel()));
    }

    getCommand("chunkloader").setExecutor(new ChunkLoaderCmd(this));
    getCommand("chunkloader").setTabCompleter(new ChunkLoaderCmd(this));
    getCommand("scoreboardplaytime").setExecutor(new ScoreboardPlaytimeCmd(this));
    getCommand("scoreboardplaytime").setTabCompleter(new ScoreboardPlaytimeCmd(this));
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  public static String formatColors(String arg) {
    return ChatColor.translateAlternateColorCodes('&', arg);
  }
}
