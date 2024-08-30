package com.github.shioku.vanillautility.misc;

import static com.github.shioku.vanillautility.VanillaUtility.PREFIX;
import static com.github.shioku.vanillautility.VanillaUtility.formatColors;

import org.bukkit.command.Command;

public class StringUtil {

  public static String getSyntaxError(Command command) {
    return PREFIX + formatColors("Syntaxerror! Please use: &c" + command.getUsage());
  }
}
