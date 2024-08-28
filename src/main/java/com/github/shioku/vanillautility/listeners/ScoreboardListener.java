package com.github.shioku.vanillautility.listeners;

import com.github.shioku.vanillautility.VanillaUtility;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Objective;

@RequiredArgsConstructor
public class ScoreboardListener implements Listener {

  private final VanillaUtility plugin;

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Objective obj = this.plugin.getPlaytimeScoreboard().getObjective("playtime");

    assert obj != null : "Playtime objective is null";

    obj.getScore(event.getPlayer().getName()).setScore(event.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60);
    event.getPlayer().setScoreboard(this.plugin.getPlaytimeScoreboard());
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    // Maybe only need to set for event player as in onJoin?
    Objective obj = this.plugin.getPlaytimeScoreboard().getObjective("deaths");

    assert obj != null : "Deaths objective is null";

    obj.getScore(event.getEntity().getName()).setScore(event.getEntity().getStatistic(Statistic.DEATHS));
    Bukkit.getOnlinePlayers().forEach(player -> player.setScoreboard(this.plugin.getDeathScoreboard()));
  }

  @EventHandler
  public void onPlayerDamage(EntityDamageEvent event) {
    // Maybe only need to set for event player as in onJoin?
    if (!this.plugin.isEnableHealth()) return;
    if (!(event.getEntity() instanceof Player player)) return;

    Objective obj = this.plugin.getHealthScoreboard().getObjective("health");

    assert obj != null : "Health objective is null";

    obj.getScore(player.getName()).setScore(Math.round((float) player.getHealth()));
    Bukkit.getOnlinePlayers().forEach(player1 -> player1.setScoreboard(this.plugin.getHealthScoreboard()));
  }

  @EventHandler
  public void onRegainHealth(EntityRegainHealthEvent event) {
    // Maybe only need to set for event player as in onJoin?
    if (!this.plugin.isEnableHealth()) return;
    if (!(event.getEntity() instanceof Player player)) return;

    Objective obj = this.plugin.getHealthScoreboard().getObjective("health");

    assert obj != null : "Health objective is null";

    obj.getScore(player.getName()).setScore(Math.round((float) player.getHealth()));
    Bukkit.getOnlinePlayers().forEach(player1 -> player1.setScoreboard(this.plugin.getHealthScoreboard()));
  }
}
