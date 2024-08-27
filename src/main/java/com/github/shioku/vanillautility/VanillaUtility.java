package com.github.shioku.vanillautility;

import com.github.shioku.vanillautility.cmds.ChunkLoaderCmd;
import com.github.shioku.vanillautility.listeners.ScoreboardListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class VanillaUtility extends JavaPlugin {

  public static String PREFIX = formatColors("&8[&3Admin&8] &7");

  private Scoreboard scoreboard = null;

  @Override
  @SuppressWarnings("ConstantConditions")
  public void onEnable() {
    // Plugin startup logic
    ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();

    sender.sendMessage(PREFIX + formatColors("&aEnabling &6" + getDescription().getName() + " &7with version &6" + getDescription().getVersion() + "&7.&r"));

    PluginDescriptionFile descriptionFile = this.getDescription();
    for (String commandName : descriptionFile.getCommands().keySet()) {
      PluginCommand command = this.getCommand(commandName);
      if (command == null) continue;
      command.setUsage(command.getUsage().replace("<label>", command.getLabel()));
    }
    sender.sendMessage(PREFIX + formatColors("Set permission and usage messages."));

    getCommand("chunkloader").setExecutor(new ChunkLoaderCmd(this));
    getCommand("chunkloader").setTabCompleter(new ChunkLoaderCmd(this));
    sender.sendMessage(PREFIX + formatColors("Registered commands."));

    registerScoreboardPlaytime();
    sender.sendMessage(PREFIX + formatColors("Registered scoreboard timer."));

    Bukkit.getServer().getPluginManager().registerEvents(new ScoreboardListener(this), this);

    sender.sendMessage(PREFIX + formatColors("&6" + this.getDescription().getName() + " &7has been &aenabled &7with version &6" + this.getDescription().getVersion() + "&7.&r"));
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  private void registerScoreboardPlaytime() {
    ScoreboardManager manager = Bukkit.getScoreboardManager();

    assert manager != null : "Scoreboard manager is null";

    Scoreboard scoreboard = manager.getNewScoreboard();
    this.scoreboard = scoreboard;

    Objective objective = scoreboard.registerNewObjective("playtime", Criteria.statistic(Statistic.PLAY_ONE_MINUTE), "Playtime");

    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    objective.setDisplayName("Playtime");

    Bukkit.getScheduler().runTaskTimer(this, task -> {
      List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
      players.forEach(player -> {
        Score score = objective.getScore(player.getName());
        score.setScore(Math.round((float) player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60));
        player.setScoreboard(scoreboard);
      });
    }, 0, 5 * 20);
  }

  public static String formatColors(String arg) {
    return ChatColor.translateAlternateColorCodes('&', arg);
  }
}
