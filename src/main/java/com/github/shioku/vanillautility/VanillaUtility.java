package com.github.shioku.vanillautility;

import com.github.shioku.vanillautility.cmds.ChunkLoaderCmd;
import com.github.shioku.vanillautility.listeners.ScoreboardListener;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

@Getter
public final class VanillaUtility extends JavaPlugin {

  public static String PREFIX = formatColors("&8[&3Admin&8] &7");

  private boolean enableHealth = false;

  private Scoreboard scoreboard = null;

  @Override
  @SuppressWarnings("ConstantConditions")
  public void onEnable() {
    saveDefaultConfig();
    // Plugin startup logic
    ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();

    sender.sendMessage(
      PREFIX + formatColors("&aEnabling &6" + getDescription().getName() + " &7with &6v" + getDescription().getVersion() + "&7.&r")
    );

    PluginDescriptionFile descriptionFile = this.getDescription();
    for (String commandName : descriptionFile.getCommands().keySet()) {
      PluginCommand command = this.getCommand(commandName);
      if (command == null) continue;
      command.setUsage(command.getUsage().replace("<label>", command.getLabel()));
    }
    sender.sendMessage(PREFIX + formatColors("Set permission and usage messages."));

    this.enableHealth = getConfig().getBoolean("enableHealth");

    getCommand("chunkloader").setExecutor(new ChunkLoaderCmd(this));
    getCommand("chunkloader").setTabCompleter(new ChunkLoaderCmd(this));
    sender.sendMessage(PREFIX + formatColors("Registered commands."));

    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    registerScoreboardPlaytime();
    if (this.enableHealth) registerHealthScoreboard();
    registerScoreboardDeaths();
    sender.sendMessage(PREFIX + formatColors("Registered Scoreboards."));

    Bukkit.getServer().getPluginManager().registerEvents(new ScoreboardListener(this), this);

    sender.sendMessage(
      PREFIX +
      formatColors("&6" + this.getDescription().getName() + " &7has been &aenabled &7with &6v" + this.getDescription().getVersion() + "&7.&r")
    );
  }

  private void registerScoreboardPlaytime() {
    Objective objective =
      this.scoreboard.registerNewObjective(
          "playtime",
          Criteria.statistic(Statistic.PLAY_ONE_MINUTE),
          formatColors("&" + this.getConfig().getString("playtimeTitleColor") + "Playtime")
        );

    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    List<OfflinePlayer> players = List.of(Bukkit.getOfflinePlayers());
    players.forEach(offlinePlayer -> {
      String offlinePlayerName = offlinePlayer.getName();

      assert offlinePlayerName != null : "Offline player name is null";

      Score score = objective.getScore(offlinePlayerName);
      score.setScore(Math.round((float) offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60));
    });

    Bukkit.getScheduler()
      .runTaskTimer(
        this,
        task ->
          Bukkit.getOnlinePlayers()
            .forEach(player -> {
              Score score = objective.getScore(player.getName());
              score.setScore(Math.round((float) player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60));
              player.setScoreboard(this.scoreboard);
            }),
        0,
        5 * 20
      );
  }

  private void registerHealthScoreboard() {
    Objective obj = this.scoreboard.registerNewObjective("health", Criteria.DUMMY, "Health");

    obj.setDisplaySlot(DisplaySlot.BELOW_NAME);

    Bukkit.getOnlinePlayers()
      .forEach(player -> {
        Score score = obj.getScore(player.getName());
        score.setScore(Math.round((float) player.getHealth()));
        player.setScoreboard(this.scoreboard);
      });
  }

  private void registerScoreboardDeaths() {
    Objective objective = this.scoreboard.registerNewObjective("deaths", Criteria.statistic(Statistic.DEATHS), "Deaths");

    objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

    Bukkit.getOnlinePlayers()
      .forEach(player -> {
        Score score = objective.getScore(player.getName());
        score.setScore(player.getStatistic(Statistic.DEATHS));
        player.setScoreboard(this.scoreboard);
      });
  }

  public static String formatColors(String arg) {
    return ChatColor.translateAlternateColorCodes('&', arg);
  }
}
