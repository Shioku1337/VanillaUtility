package com.github.shioku.vanillautility.listeners;

import com.github.shioku.vanillautility.VanillaUtility;
import lombok.RequiredArgsConstructor;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class ScoreboardListener implements Listener {

  private final VanillaUtility plugin;

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    this.plugin.getScoreboard().getObjective("playtime").getScore(event.getPlayer().getName()).setScore(event.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 /  60);
    event.getPlayer().setScoreboard(this.plugin.getScoreboard());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    this.plugin.getScoreboard().resetScores(event.getPlayer().getName());
  }
}
