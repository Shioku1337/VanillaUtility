package com.github.shioku.vanillautility.listeners;

import com.github.shioku.vanillautility.VanillaUtility;
import lombok.RequiredArgsConstructor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
    Objective playTimeObjective = this.plugin.getScoreboard().getObjective("playtime");

    assert playTimeObjective != null : "Playtime objective is null";

    playTimeObjective.getScore(event.getPlayer().getName()).setScore(event.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60);

    Objective healthObjective = this.plugin.getScoreboard().getObjective("health");

    assert healthObjective != null : "Health objective is null";

    healthObjective.getScore(event.getPlayer().getName()).setScore(Math.round((float) event.getPlayer().getHealth()));

    event.getPlayer().setScoreboard(this.plugin.getScoreboard());
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Objective obj = this.plugin.getScoreboard().getObjective("deaths");

    assert obj != null : "Deaths objective is null";

    obj.getScore(event.getEntity().getName()).setScore(event.getEntity().getStatistic(Statistic.DEATHS));
  }

  @EventHandler
  public void onPlayerDamage(EntityDamageEvent event) {
    // Maybe only need to set for event player as in onJoin?
    if (!this.plugin.isEnableHealth()) return;
    if (!(event.getEntity() instanceof Player player)) return;

    Objective obj = this.plugin.getScoreboard().getObjective("health");

    assert obj != null : "Health objective is null";

    double finalHealth = player.getHealth() - event.getFinalDamage();

    if (finalHealth < 0) finalHealth = 0;

    obj.getScore(player.getName()).setScore(Math.round((float) finalHealth));
  }

  @EventHandler
  public void onRegainHealth(EntityRegainHealthEvent event) {
    if (!this.plugin.isEnableHealth()) return;
    if (!(event.getEntity() instanceof Player player)) return;

    Objective obj = this.plugin.getScoreboard().getObjective("health");

    assert obj != null : "Health objective is null";

    double finalHealth = player.getHealth() + event.getAmount();

    if (finalHealth > 20.0) finalHealth = 20.0;

    obj.getScore(player.getName()).setScore(Math.round((float) finalHealth));
  }
}
