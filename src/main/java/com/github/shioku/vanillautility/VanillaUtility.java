package com.github.shioku.vanillautility;

import com.github.shioku.vanillautility.cmds.AdvancementListCmd;
import com.github.shioku.vanillautility.cmds.ChunkLoaderCmd;
import com.github.shioku.vanillautility.listeners.ScoreboardListener;
import com.github.shioku.vanillautility.updatechecker.UpdateChecker;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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

  private File chunkFile;

  @Setter
  private YamlConfiguration chunkConfig;

  public static final Set<String> VALID_COLORS = Set.of(
    "BLACK",
    "DARK_BLUE",
    "DARK_GREEN",
    "DARK_AQUA",
    "DARK_RED",
    "DARK_PURPLE",
    "GOLD",
    "GRAY",
    "DARK_GRAY",
    "BLUE",
    "GREEN",
    "AQUA",
    "RED",
    "LIGHT_PURPLE",
    "YELLOW",
    "WHITE"
  );

  public static Map<String, List<String>> PERSISTED_CHUNKS = new HashMap<>();

  @Override
  public void onLoad() {
    saveDefaultConfig();

    boolean enableUpdateCheck = getConfig().getBoolean("enableUpdateCheck", true);

    if (!enableUpdateCheck) return;

    this.enablePlugin = !UpdateChecker.checkUpdates(this);
  }

  @Override
  public void onEnable() {
    if (!enablePlugin) {
      this.setEnabled(false);
      getLogger().severe("There is an update for the plugin and the checks are enabled. The plugin has been disabled.");
      getLogger().severe("The checks can be disabled in your config.yml");
      return;
    }

    setupChunkFile();

    setUsages();

    this.enableHealth = getConfig().getBoolean("enableHealth", true);

    loadPersistedChunksToMemory();

    registerCommands();

    registerScoreboards();

    getLogger().info(this.getDescription().getName() + " has been enabled with v" + this.getDescription().getVersion() + ".");
  }

  @Override
  public void onDisable() {
    persistChunksToYAML();
  }

  private void registerScoreboardPlaytime() {
    String configColor = this.getConfig().getString("playtimeTitleColor", "AQUA");

    if (!VALID_COLORS.contains(configColor)) {
      configColor = "AQUA";
      getLogger().warning("Invalid color for \"playtimeTitleColor\" in config.yml.");
      getLogger().warning("The default has been set to AQUA. To get rid of this message, enter a valid color name in the field.");
    }

    Objective objective = this.scoreboard.registerNewObjective("playtime", Criteria.DUMMY, ChatColor.valueOf(configColor) + "Playtime");

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

  private void setupChunkFile() {
    chunkFile = new File(this.getDataFolder(), "data/chunks.yml");

    if (!chunkFile.exists()) {
      saveResource("data/chunks.yml", false);
    }

    this.chunkConfig = YamlConfiguration.loadConfiguration(chunkFile);
  }

  private void setUsages() {
    PluginDescriptionFile descriptionFile = this.getDescription();

    for (String commandName : descriptionFile.getCommands().keySet()) {
      PluginCommand command = this.getCommand(commandName);

      if (command == null) continue;

      command.setUsage(command.getUsage().replace("<label>", command.getLabel()));
    }

    getLogger().info("Set permission and usage messages.");
  }

  @SuppressWarnings("ConstantConditions")
  private void registerCommands() {
    // Register commands
    getCommand("chunkloader").setExecutor(new ChunkLoaderCmd(this));
    getCommand("advancementlist").setExecutor(new AdvancementListCmd(this.getLogger()));
    getCommand("chunkloader").setTabCompleter(new ChunkLoaderCmd(this));
    getCommand("advancementlist").setTabCompleter(new AdvancementListCmd(this.getLogger()));
    getLogger().info("Registered Commands.");
  }

  @SuppressWarnings("ConstantConditions")
  private void registerScoreboards() {
    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    registerScoreboardPlaytime();
    if (this.enableHealth) registerHealthScoreboard();
    registerScoreboardDeaths();
    getLogger().info("Registered Scoreboards.");

    Bukkit.getServer().getPluginManager().registerEvents(new ScoreboardListener(this), this);
  }

  private void loadPersistedChunksToMemory() {
    ConfigurationSection worldSection = chunkConfig.getConfigurationSection("chunks");

    if (worldSection == null) return;

    Set<String> worldIds = worldSection.getKeys(false);

    for (String worldId : worldIds) {
      List<String> chunkList = worldSection.getStringList(worldId);
      PERSISTED_CHUNKS.put(worldId, chunkList);
    }

    getLogger().info("Loaded persisted chunks to memory.");
  }

  private void persistChunksToYAML() {
    chunkConfig.set("chunks", PERSISTED_CHUNKS);

    try {
      chunkConfig.save(chunkFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    chunkConfig = YamlConfiguration.loadConfiguration(chunkFile);
  }

  public static String formatColors(String arg) {
    return ChatColor.translateAlternateColorCodes('&', arg);
  }
}
