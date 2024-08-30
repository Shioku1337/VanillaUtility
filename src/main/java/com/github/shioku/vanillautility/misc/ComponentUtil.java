package com.github.shioku.vanillautility.misc;

import java.awt.*;
import java.util.Arrays;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class ComponentUtil {

  public static BaseComponent[] createComponentsFromString(String... strings) {
    return Arrays.stream(strings).map(ComponentSerializer::deserialize).toList().toArray(new BaseComponent[0]);
  }

  public static TranslatableComponent itemName(Material item) {
    return new TranslatableComponent(itemKey(item));
  }

  public static TranslatableComponent advancementTitle(@NotNull Advancement advancement) {
    return new TranslatableComponent(advancementKey(advancement) + ".title");
  }

  public static TranslatableComponent advancementDescription(@NotNull Advancement advancement) {
    return new TranslatableComponent(advancementKey(advancement) + ".description");
  }

  public static TranslatableComponent statistic(Statistic statistic, Component... with) {
    return new TranslatableComponent(statisticKey(statistic), (Object[]) with);
  }

  public static TranslatableComponent entityName(EntityType entity) {
    return new TranslatableComponent(entityKey(entity));
  }

  // Internals
  private static String advancementKey(@NotNull Advancement advancement) {
    String result = advancement.getKey().getKey().replace("/", ".");
    result = switch (result) { // Needed to correct Spigot on some advancement names vs how they appear in the lang files
      case "husbandry.obtain_netherite_hoe" -> "husbandry.netherite_hoe";
      case "husbandry.bred_all_animals" -> "husbandry.breed_all_animals";
      default -> result;
    };
    return "advancements." + result;
  }

  private static String statisticKey(Statistic statistic) {
    String prefix = statistic.isSubstatistic() ? "stat_type.minecraft." : "stat.minecraft.";
    String result = StatisticsKeyConverter.getMinecraftTranslationKey(statistic);
    return !result.isEmpty() ? prefix + result : statistic.name();
  }

  private static String itemKey(Material item) {
    return (item.isBlock() ? "block" : "item") + ".minecraft." + item.getKey().getKey();
  }

  private static String entityKey(EntityType entity) {
    // Note: before 1.20.6 , mooshroom and snow_golem needed to be translated manually.
    return "entity.minecraft." + entity.name().toLowerCase();
  }
}
