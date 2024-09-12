package com.github.shioku.vanillautility.cmds;

import static com.github.shioku.vanillautility.VanillaUtility.PREFIX;

import com.github.shioku.vanillautility.VanillaUtility;
import com.github.shioku.vanillautility.misc.ComponentUtil;
import com.github.shioku.vanillautility.misc.StringUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancementListCmd implements TabExecutor {

  private final VanillaUtility plugin;

  public AdvancementListCmd(VanillaUtility plugin) {
    this.plugin = plugin;
  }

  private final Component infoComponent = MiniMessage.miniMessage().deserialize(PREFIX + "You can find the needed ids on the " +
      "<u><aqua><click:open_url:https://minecraft.fandom.com/wiki/Advancement#List_of_advancements>Minecraft Advancement List</click></aqua></u>! " +
      "The column \"Resource Location\" is the correct input!");

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.getLogger().info("Only players can list advancements.");
      return true;
    }

    Audience audience = plugin.adventure().player(player);
    MiniMessage miniMessage = MiniMessage.miniMessage();

    if (args.length != 1) {
      audience.sendMessage(StringUtil.getSyntax(cmd));
      audience.sendMessage(this.infoComponent);
      return true;
    }

    Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft(args[0]));

    if (advancement == null) {
      audience.sendMessage(miniMessage.deserialize(PREFIX + "Syntaxerror, wrong advancement key! Please use: <red>" + cmd.getUsage()));
      audience.sendMessage(this.infoComponent);
      return true;
    }

    TranslatableComponent advancementTitle = ComponentUtil.advancementTitle(advancement);
    AdvancementProgress advancementProgress = player.getAdvancementProgress(advancement);
    List<String> remainingCriteria = new ArrayList<>(advancementProgress.getRemainingCriteria());

    if (advancementProgress.isDone() || remainingCriteria.isEmpty()) {
      audience.sendMessage(miniMessage.deserialize(PREFIX + "<green>Congratilations! <gray>You have already finished this advancement!"));
      return true;
    }

    audience.sendMessage(
      miniMessage
        .deserialize(PREFIX + "The following progess is still missing from the advancement: \"<green>")
        .append(advancementTitle)
        .append(miniMessage.deserialize("<gray>\""))
    );

    for (String criteria : remainingCriteria) {
      audience.sendMessage(miniMessage.deserialize(PREFIX + "Criteria: <gold>" + criteria.replace("minecraft:", "") + "</gold>"));
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
