package com.github.shioku.vanillautility.cmds;

import com.github.shioku.vanillautility.VanillaUtility;
import com.github.shioku.vanillautility.misc.StringUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class SharedInvCmd implements TabExecutor {

  private final VanillaUtility plugin;

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player)) {
      this.plugin.getLogger().info("The shared inventory can only be opened by players!");
      return true;
    }

    Audience audience = plugin.adventure().player(player);

    if (args.length != 0) {
      audience.sendMessage(StringUtil.getSyntaxError(cmd));
      return true;
    }

    player.openInventory(this.plugin.getInventoryUtil().getInventory());
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    return List.of();
  }
}
