package com.github.shioku.vanillautility.cmds;

import static com.github.shioku.vanillautility.VanillaUtility.PREFIX;
import static com.github.shioku.vanillautility.misc.StringUtil.mm;

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

@RequiredArgsConstructor
public class AdvancementListCmd implements TabExecutor {

  // TODO / Add: grepable -> /advancementlist some/advancement-id <grep-exp> -> /advl husbandry/bred-all-animals sniffer

  private final VanillaUtility plugin;

  private final Component infoComponent = mm(
    PREFIX +
    "You can find the needed IDs on the " +
    "<u><aqua><click:open_url:https://minecraft.fandom.com/wiki/Advancement#List_of_advancements>Minecraft Advancement List</click></aqua></u>! " +
    "The column \"Resource Location\" is the correct input!"
  );

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player)) {
      plugin.getLogger().info("Only players can list advancements.");
      return true;
    }

    Audience audience = plugin.adventure().player(player);

    if (args.length != 1 && args.length != 2) {
      audience.sendMessage(StringUtil.getSyntaxError(cmd));
      audience.sendMessage(this.infoComponent);
      return true;
    }

    Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft(args[0]));

    if (advancement == null) {
      audience.sendMessage(mm(PREFIX + "Syntaxerror, wrong advancement key! Please use: <red>" + cmd.getUsage()));
      audience.sendMessage(this.infoComponent);
      return true;
    }

    TranslatableComponent advancementTitle = ComponentUtil.advancementTitle(advancement);
    AdvancementProgress advancementProgress = player.getAdvancementProgress(advancement);
    List<String> remainingCriteria = new ArrayList<>(advancementProgress.getRemainingCriteria());

    if (advancementProgress.isDone() || remainingCriteria.isEmpty()) {
      audience.sendMessage(mm(PREFIX + "<green>Congratulations! <gray>You have already finished this advancement!"));
      return true;
    }

    audience.sendMessage(
      mm(PREFIX + "The following progress is still missing from the advancement: \"<green>").append(advancementTitle).append(mm("<gray>\""))
    );

    int amount = 0; // needed if grep is used

    for (String criteria : remainingCriteria) {
      if (args.length == 2) {
        if (criteria.contains(args[1])) {
          audience.sendMessage(mm(PREFIX + "Criteria: <gold>" + criteria.replace("minecraft:", "") + "</gold>"));
          amount++;
        }
      } else {
        amount = -1;
        audience.sendMessage(mm(PREFIX + "Criteria: <gold>" + criteria.replace("minecraft:", "") + "</gold>"));
      }
    }

    if (amount == 0) {
      audience.sendMessage(mm(PREFIX + "There were <red>no criteria found <gray>when filtering using \"<gold>" + args[1] + "<gray>\"."));
      audience.sendMessage(mm(PREFIX + "Please check your filter or remove it entirely."));
    }

    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player) || (args.length != 1 && args.length != 2)) return List.of();

    if (args.length == 1) {
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

    return List.of("<filter-keyword>");
  }
}
