package com.github.shioku.vanillautility;

import com.github.shioku.vanillautility.cmds.AdvancementListCmd;
import com.github.shioku.vanillautility.cmds.ChunkLoaderCmd;
import com.github.shioku.vanillautility.listeners.ScoreboardListener;
import com.github.shioku.vanillautility.updatechecker.UpdateChecker;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

@Getter
public final class VanillaUtility extends JavaPlugin {

  private boolean enablePlugin = true;

  public static String PREFIX = formatColors("&8[&3Admin&8] &7");

  private boolean enableHealth = false;

  private Scoreboard scoreboard = null;

  @Override
  public void onLoad() {
    saveDefaultConfig();

    boolean enableUpdateCheck = getConfig().getBoolean("enableUpdateCheck");

    if (!enableUpdateCheck) return;

    this.enablePlugin = !UpdateChecker.checkUpdates(this);
  }

  @Override
  @SuppressWarnings("ConstantConditions")
  public void onEnable() {
    if (!enablePlugin) {
      this.setEnabled(false);
      getLogger().severe("There is an update for the plugin and the checks are enabled. The plugin has been disabled.");
      getLogger().severe("The checks can be disabled in your config.yml");
      return;
    }

    Logger logger = getLogger();

    PluginDescriptionFile descriptionFile = this.getDescription();
    for (String commandName : descriptionFile.getCommands().keySet()) {
      PluginCommand command = this.getCommand(commandName);
      if (command == null) continue;
      command.setUsage(command.getUsage().replace("<label>", command.getLabel()));
    }
    logger.info("Set permission and usage messages.");

    this.enableHealth = getConfig().getBoolean("enableHealth");

    getCommand("chunkloader").setExecutor(new ChunkLoaderCmd(this));
    getCommand("advancementlist").setExecutor(new AdvancementListCmd(this.getLogger()));
    getCommand("chunkloader").setTabCompleter(new ChunkLoaderCmd(this));
    getCommand("advancementlist").setTabCompleter(new AdvancementListCmd(this.getLogger()));
    logger.info("Registered commands.");

    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    registerScoreboardPlaytime();
    if (this.enableHealth) registerHealthScoreboard();
    registerScoreboardDeaths();
    logger.info("Registered Scoreboards.");

    Bukkit.getServer().getPluginManager().registerEvents(new ScoreboardListener(this), this);

    Bukkit.getOnlinePlayers()
      .forEach(player -> {
        player.setScoreboard(this.scoreboard);
      });

    logger.info(this.getDescription().getName() + " has been enabled with v" + this.getDescription().getVersion() + ".");
  }

  private void registerScoreboardPlaytime() {
    Objective objective =
      this.scoreboard.registerNewObjective(
          "playtime",
          Criteria.DUMMY,
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
            }),
        0,
        60 * 20
      );
  }

  private void registerHealthScoreboard() {
    Objective obj = this.scoreboard.registerNewObjective("health", Criteria.DUMMY, ChatColor.RED + "❤");

    obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
  }

  private void registerScoreboardDeaths() {
    Objective objective = this.scoreboard.registerNewObjective("deaths", Criteria.DUMMY, "Deaths");

    objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
  }

  public static String formatColors(String arg) {
    return ChatColor.translateAlternateColorCodes('&', arg);
  }
}
