package com.github.shioku.vanillautility;

import static com.github.shioku.vanillautility.misc.StringUtil.VALID_COLORS;

import com.github.shioku.vanillautility.cmds.AdvancementListCmd;
import com.github.shioku.vanillautility.cmds.ChunkLoaderCmd;
import com.github.shioku.vanillautility.cmds.SaveChunksCmd;
import com.github.shioku.vanillautility.cmds.SharedInvCmd;
import com.github.shioku.vanillautility.listeners.ChunkListener;
import com.github.shioku.vanillautility.listeners.InventoryListener;
import com.github.shioku.vanillautility.listeners.ScoreboardListener;
import com.github.shioku.vanillautility.misc.inventory.SharedInventoryUtil;
import com.github.shioku.vanillautility.misc.inventory.serializer.InventorySerializer;
import com.github.shioku.vanillautility.updatechecker.UpdateChecker;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.World;
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

public final class VanillaUtility extends JavaPlugin {

  private boolean enablePlugin = true;

  // Use with adventure!
  public static String PREFIX = "<color:#666666>[<aqua>Admin<color:#666666>] <gray>";

  @Getter
  private boolean enableHealth = false;

  @Getter
  private Scoreboard scoreboard = null;

  private BukkitAudiences adventure;

  @Getter
  private File chunkFile;

  @Setter
  @Getter
  private YamlConfiguration chunkConfig;

  public static Map<String, List<String>> LOADED_CHUNKS = new HashMap<>();

  @Getter
  private SharedInventoryUtil inventoryUtil;

  private InventorySerializer inventorySerializer;

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
      Bukkit.getPluginManager().disablePlugin(this);
      getLogger().severe("There is an update for the plugin and the checks are enabled. The plugin has been disabled.");
      getLogger().severe("The checks can be disabled in your config.yml");
      return;
    }

    this.enableHealth = getConfig().getBoolean("enableHealth", true);

    this.inventoryUtil = new SharedInventoryUtil();
    this.inventorySerializer = new InventorySerializer(this);

    loadSharedInventory();

    setUsages();

    setupChunkFile();

    loadPersistedChunksToMemory();

    this.adventure = BukkitAudiences.create(this);

    registerCommands();

    registerScoreboards();
    registerListeners();

    Bukkit.getConsoleSender().sendMessage(formatColors("&8[&3Admin&8] &7"));

    getLogger().info(this.getDescription().getName() + " has been enabled with v" + this.getDescription().getVersion() + ".");
  }

  @Override
  public void onDisable() {
    persistChunksToYAML();

    saveSharedInventory();

    if (this.adventure == null) return;

    this.adventure.close();
    this.adventure = null;
  }

  public BukkitAudiences adventure() {
    if (this.adventure == null) {
      throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
    }

    return this.adventure;
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
    getCommand("chunkloader").setExecutor(new ChunkLoaderCmd(this));
    getCommand("advancementlist").setExecutor(new AdvancementListCmd(this.getLogger()));
    getCommand("chunkloader").setTabCompleter(new ChunkLoaderCmd(this));
    getCommand("advancementlist").setTabCompleter(new AdvancementListCmd(this.getLogger()));
    getCommand("savechunks").setExecutor(new SaveChunksCmd(this));
    getCommand("savechunks").setTabCompleter(new SaveChunksCmd(this));
    getCommand("sharedinv").setExecutor(new SharedInvCmd(this));
    getCommand("sharedinv").setTabCompleter(new SharedInvCmd(this));
    getLogger().info("Registered Commands.");
  }

  @SuppressWarnings("ConstantConditions")
  private void registerScoreboards() {
    this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    registerScoreboardPlaytime();
    if (this.enableHealth) registerHealthScoreboard();
    registerScoreboardDeaths();
    getLogger().info("Registered Scoreboards.");
  }

  private void registerListeners() {
    Bukkit.getPluginManager().registerEvents(new ScoreboardListener(this), this);
    Bukkit.getPluginManager().registerEvents(new ChunkListener(this), this);
    Bukkit.getPluginManager().registerEvents(new InventoryListener(this), this);
  }

  private void loadPersistedChunksToMemory() {
    chunkConfig = YamlConfiguration.loadConfiguration(chunkFile);

    ConfigurationSection worldSection = chunkConfig.getConfigurationSection("chunks");

    if (worldSection == null) return;

    Set<String> worldIds = worldSection.getKeys(false);

    for (String worldId : worldIds) {
      List<String> chunkList = worldSection.getStringList(worldId);
      LOADED_CHUNKS.put(worldId, chunkList);
    }

    Bukkit.getWorlds()
      .forEach(world -> {
        for (Map.Entry<String, List<String>> entry : LOADED_CHUNKS.entrySet()) {
          String worldId = entry.getKey();
          List<String> chunkList = entry.getValue();

          if (!worldId.equals(world.getUID().toString())) continue;

          for (String chunkXZ : chunkList) {
            String[] xz = chunkXZ.split(",");

            int x = Integer.parseInt(xz[0]);
            int z = Integer.parseInt(xz[1]);

            world.addPluginChunkTicket(x, z, this);
          }
        }
      });

    getLogger().info("Loaded persisted chunks to memory.");
  }

  public void persistChunksToYAML() {
    for (World world : Bukkit.getWorlds()) {
      List<String> chunkXZ = new ArrayList<>();

      if (!world.getPluginChunkTickets().containsKey(this)) continue;

      for (Chunk chunk : world.getPluginChunkTickets().get(this)) {
        chunkXZ.add(chunk.getX() + "," + chunk.getZ());
      }

      LOADED_CHUNKS.computeIfAbsent(world.getName(), k -> new ArrayList<>());

      LOADED_CHUNKS.put(world.getUID().toString(), chunkXZ);
    }

    chunkConfig.set("chunks", LOADED_CHUNKS);

    try {
      chunkConfig.save(chunkFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void saveSharedInventory() {
    File file = new File(this.getDataFolder(), "data/shared_inventory.json");

    if (!file.exists()) {
      saveResource("data/shared_inventory.json", false);
    }

    this.inventorySerializer.serialize(file, this.inventoryUtil.getInventory());
  }

  private void loadSharedInventory() {
    File file = new File(this.getDataFolder(), "data/shared_inventory.json");

    if (!file.exists()) {
      saveResource("data/shared_inventory.json", false);
    }

    this.inventoryUtil.getInventory().setContents(this.inventorySerializer.deserialize(file));
  }

  public static String formatColors(String arg) {
    return ChatColor.translateAlternateColorCodes('&', arg);
  }
}
