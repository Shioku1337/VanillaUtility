package com.github.shioku.vanillautility.cmds;

import static com.github.shioku.vanillautility.VanillaUtility.PREFIX;
import static com.github.shioku.vanillautility.misc.StringUtil.mm;

import com.github.shioku.vanillautility.VanillaUtility;
import com.github.shioku.vanillautility.misc.StringUtil;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class PingCmd implements TabExecutor {

  private final VanillaUtility plugin;

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player)) {
      Audience audience = plugin.adventure().sender(sender);

      int ping = new Random().nextInt(100);

      String pingColor = ping < 25 ? "green" : ping > 30 && ping < 60 ? "gold" : "red";

      audience.sendMessage(mm(PREFIX + "Your current ping is: <" + pingColor + ">" + ping + "ms"));

      plugin.getLogger().info("Only players can display their ping.");
      return true;
    }

    Audience audience = plugin.adventure().player(player);

    if (args.length != 0) {
      audience.sendMessage(StringUtil.getSyntax(cmd));
      return true;
    }

    int ping = player.getPing();

    String pingColor = ping < 25 ? "green" : ping > 30 && ping < 60 ? "gold" : "red";

    audience.sendMessage(mm(PREFIX + "Your current ping is: <" + pingColor + ">" + ping + "ms"));
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    return List.of();
  }
}
