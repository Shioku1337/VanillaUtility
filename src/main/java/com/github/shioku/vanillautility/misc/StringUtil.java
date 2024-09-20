package com.github.shioku.vanillautility.misc;

import static com.github.shioku.vanillautility.VanillaUtility.PREFIX;

import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;

public class StringUtil {

  public static Component getSyntaxError(Command command) {
    return MiniMessage.miniMessage().deserialize(PREFIX + "Syntaxerror! Please use: <red>" + command.getUsage() + "</red>");
  }

  public static Component mm(String message) {
    return MiniMessage.miniMessage().deserialize(message);
  }

  public static final Set<String> VALID_COLORS = Set.of(
    "BLACK",
    "DARK_BLUE",
    "DARK_GREEN",
    "DARK_AQUA",
    "DARK_RED",
    "DARK_PURPLE",
    "GOLD",
    "GRAY",
    "DARK_GRAY",
    "BLUE",
    "GREEN",
    "AQUA",
    "RED",
    "LIGHT_PURPLE",
    "YELLOW",
    "WHITE"
  );
}
