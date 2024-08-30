package com.github.shioku.vanillautility.cmds;

import static com.github.shioku.vanillautility.VanillaUtility.PREFIX;

import com.github.shioku.vanillautility.misc.ComponentUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancementListCmd implements TabExecutor {

  @Override
  @SuppressWarnings("")
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(PREFIX + "Only players can use this command.");
      return true;
    }

    if (args.length != 1) {
      sender.sendMessage(PREFIX + "Syntaxerror! Please use: &c" + cmd.getUsage());
      sender
        .spigot()
        .sendMessage(
          new ComponentBuilder(PREFIX + "You can find the needed ids on the ")
            .append("Minecraft Advancement List")
            .color(ChatColor.DARK_AQUA)
            .underlined(true)
            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft.fandom.com/wiki/Advancement#List_of_advancements"))
            .append("! The column \"Resource Location\" is the correct input!")
            .color(ChatColor.GRAY)
            .underlined(false)
            .create()
        );
      return false;
    }

    Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft(args[0]));

    if (advancement == null) {
      sender.sendMessage(PREFIX + "Syntaxerror, wrong advancement key! Please use: &c" + cmd.getUsage());
      sender
        .spigot()
        .sendMessage(
          new ComponentBuilder(PREFIX + "You can find the needed ids on the ")
            .append("Minecraft Advancement List")
            .color(ChatColor.DARK_AQUA)
            .underlined(true)
            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft.fandom.com/wiki/Advancement#List_of_advancements"))
            .append("! The column \"Resource Location\" is the correct input!")
            .color(ChatColor.GRAY)
            .underlined(false)
            .create()
        );
      return true;
    }

    String advancementTitle = ComponentUtil.advancementTitle(advancement).getTranslate();
    List<String> remainingCriteria = new ArrayList<>(player.getAdvancementProgress(advancement).getRemainingCriteria());

    player.sendMessage(PREFIX + "The following progess is still missing from the advancement: \"" + advancementTitle + "\"");
    for (String criteria : remainingCriteria) {
      player.sendMessage(PREFIX + criteria);
    }

    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    if (args.length != 1) return List.of();

    Iterator<Advancement> advancementIterator = Bukkit.advancementIterator();

    List<String> allAdvKeys = new ArrayList<>();

    while (advancementIterator.hasNext()) {
      Advancement adv = advancementIterator.next();
      allAdvKeys.add(adv.getKey().getKey());
    }

    return allAdvKeys;
  }
}
