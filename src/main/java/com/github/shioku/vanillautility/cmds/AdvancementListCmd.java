package com.github.shioku.vanillautility.cmds;

import static com.github.shioku.vanillautility.VanillaUtility.PREFIX;
import static com.github.shioku.vanillautility.VanillaUtility.formatColors;

import com.github.shioku.vanillautility.misc.ComponentUtil;
import com.github.shioku.vanillautility.misc.StringUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class AdvancementListCmd implements TabExecutor {

  private final Logger logger;

  private final BaseComponent[] infoComponent = new ComponentBuilder(PREFIX + "You can find the needed ids on the ")
    .append("Minecraft Advancement List")
    .color(ChatColor.DARK_AQUA)
    .underlined(true)
    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft.fandom.com/wiki/Advancement#List_of_advancements"))
    .append("! The column \"Resource Location\" is the correct input!")
    .color(ChatColor.GRAY)
    .underlined(false)
    .create();

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player)) {
      logger.info("Only players can list advancements.");
      return true;
    }

    if (args.length != 1) {
      player.sendMessage(StringUtil.getSyntaxError(cmd));
      player.spigot().sendMessage(this.infoComponent);
      return true;
    }

    Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft(args[0]));

    if (advancement == null) {
      player.sendMessage(PREFIX + "Syntaxerror, wrong advancement key! Please use: &c" + cmd.getUsage());
      player.spigot().sendMessage(this.infoComponent);
      return true;
    }

    TranslatableComponent advancementTitle = ComponentUtil.advancementTitle(advancement);
    List<String> remainingCriteria = new ArrayList<>(player.getAdvancementProgress(advancement).getRemainingCriteria());

    if (remainingCriteria.isEmpty()) {
      player.sendMessage(PREFIX + formatColors("&aContratulations! &7You have already finished this advancement!"));
      return true;
    }

    player
      .spigot()
      .sendMessage(
        new ComponentBuilder(PREFIX + "The following progess is still missing from the advancement: \"")
          .append(advancementTitle)
          .color(ChatColor.GREEN)
          .append("\"")
          .color(ChatColor.GRAY)
          .create()
      );
    for (String criteria : remainingCriteria) {
      player.sendMessage(PREFIX + formatColors("Criteria: &6") + criteria);
    }

    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player) || args.length != 1) return List.of();

    Iterator<Advancement> advancementIterator = Bukkit.advancementIterator();

    List<String> allAdvKeys = new ArrayList<>();

    while (advancementIterator.hasNext()) {
      Advancement adv = advancementIterator.next();
      if (player.getAdvancementProgress(adv).isDone()) continue;
      if (adv.getKey().getKey().startsWith("recipe")) continue;
      allAdvKeys.add(adv.getKey().getKey());
    }

    return allAdvKeys.stream().filter(str -> str.contains(args[0])).toList();
  }
}
