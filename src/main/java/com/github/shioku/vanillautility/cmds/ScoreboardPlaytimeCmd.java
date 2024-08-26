package com.github.shioku.vanillautility.cmds;

import com.github.shioku.vanillautility.VanillaUtility;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RequiredArgsConstructor
public class ScoreboardPlaytimeCmd implements TabExecutor {

  private final VanillaUtility plugin;

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    ScoreboardManager manager = Bukkit.getScoreboardManager();

    assert manager != null : "Scoreboard manager is null";

    Scoreboard scoreboard = manager.getNewScoreboard();

    Objective objective = scoreboard.registerNewObjective("test", Criteria.statistic(Statistic.PLAY_ONE_MINUTE), "dummy");

    objective.setDisplayName("Playtime");
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    Bukkit.getScheduler().runTaskTimer(this.plugin, task -> {
      Bukkit.getOnlinePlayers().forEach(player -> {
        Score score = objective.getScore(player.getUniqueId().toString());
        score.setScore(player.getStatistic(Statistic.PLAY_ONE_MINUTE));

      });
    }, 0, 60 * 20);


    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

    
    return List.of();
  }
}
