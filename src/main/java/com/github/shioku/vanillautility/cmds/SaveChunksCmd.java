package com.github.shioku.vanillautility.cmds;

import static com.github.shioku.vanillautility.VanillaUtility.PREFIX;

import com.github.shioku.vanillautility.VanillaUtility;
import com.github.shioku.vanillautility.misc.StringUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class SaveChunksCmd implements TabExecutor {

  private final VanillaUtility plugin;

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    Audience audience = plugin.adventure().sender(sender);

    if (args.length != 0) {
      audience.sendMessage(StringUtil.getSyntaxError(cmd));
      return true;
    }

    plugin.persistChunksToYAML();
    audience.sendMessage(MiniMessage.miniMessage().deserialize(PREFIX + "Successfully <green>saved <gray>chunk-loaded chunks to yaml."));
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
    return List.of();
  }
}
