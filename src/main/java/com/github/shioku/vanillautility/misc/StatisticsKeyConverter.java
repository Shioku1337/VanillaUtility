package com.github.shioku.vanillautility.misc;

import java.util.Map;
import org.bukkit.Statistic;

public class StatisticsKeyConverter {

  private static final Map<Statistic, String> statistics = Map.<Statistic, String>ofEntries(
    Map.entry(Statistic.DROP, "dropped"),
    Map.entry(Statistic.PICKUP, "picked_up"),
    Map.entry(Statistic.USE_ITEM, "used"),
    Map.entry(Statistic.BREAK_ITEM, "broken"),
    Map.entry(Statistic.CRAFT_ITEM, "crafted"),
    Map.entry(Statistic.KILL_ENTITY, "killed"),
    Map.entry(Statistic.ENTITY_KILLED_BY, "killed_by"),
    Map.entry(Statistic.MINE_BLOCK, "mined"),
    Map.entry(Statistic.DAMAGE_DEALT, "damage_dealt"),
    Map.entry(Statistic.DAMAGE_TAKEN, "damage_taken"),
    Map.entry(Statistic.DAMAGE_DEALT_ABSORBED, "damage_dealt_absorbed"),
    Map.entry(Statistic.DAMAGE_DEALT_RESISTED, "damage_dealt_resisted"),
    Map.entry(Statistic.DAMAGE_RESISTED, "damage_resisted"),
    Map.entry(Statistic.DAMAGE_ABSORBED, "damage_absorbed"),
    Map.entry(Statistic.DAMAGE_BLOCKED_BY_SHIELD, "damage_blocked_by_shield"),
    Map.entry(Statistic.TALKED_TO_VILLAGER, "talked_to_villager"),
    Map.entry(Statistic.TRADED_WITH_VILLAGER, "traded_with_villager"),
    Map.entry(Statistic.DEATHS, "deaths"),
    Map.entry(Statistic.MOB_KILLS, "mob_kills"),
    Map.entry(Statistic.PLAYER_KILLS, "player_kills"),
    Map.entry(Statistic.FISH_CAUGHT, "fish_caught"),
    Map.entry(Statistic.ANIMALS_BRED, "animals_bred"),
    Map.entry(Statistic.LEAVE_GAME, "leave_game"),
    Map.entry(Statistic.JUMP, "jump"),
    Map.entry(Statistic.DROP_COUNT, "drop"),
    Map.entry(Statistic.PLAY_ONE_MINUTE, "play_time"),
    Map.entry(Statistic.TOTAL_WORLD_TIME, "total_world_time"),
    Map.entry(Statistic.SNEAK_TIME, "sneak_time"),
    Map.entry(Statistic.TIME_SINCE_DEATH, "time_since_death"),
    Map.entry(Statistic.RAID_TRIGGER, "raid_trigger"),
    Map.entry(Statistic.ARMOR_CLEANED, "clean_armor"),
    Map.entry(Statistic.BANNER_CLEANED, "clean_banner"),
    Map.entry(Statistic.ITEM_ENCHANTED, "enchant_item"),
    Map.entry(Statistic.TIME_SINCE_REST, "time_since_rest"),
    Map.entry(Statistic.RAID_WIN, "raid_win"),
    Map.entry(Statistic.TARGET_HIT, "target_hit"),
    Map.entry(Statistic.CLEAN_SHULKER_BOX, "clean_shulker_box"),
    Map.entry(Statistic.CAKE_SLICES_EATEN, "eat_cake_slice"),
    Map.entry(Statistic.CAULDRON_FILLED, "fill_cauldron"),
    Map.entry(Statistic.BREWINGSTAND_INTERACTION, "interact_with_brewingstand"),
    Map.entry(Statistic.BEACON_INTERACTION, "interact_with_beacon"),
    Map.entry(Statistic.NOTEBLOCK_PLAYED, "play_noteblock"),
    Map.entry(Statistic.CAULDRON_USED, "use_cauldron"),
    Map.entry(Statistic.NOTEBLOCK_TUNED, "tune_noteblock"),
    Map.entry(Statistic.FLOWER_POTTED, "pot_flower"),
    Map.entry(Statistic.TRAPPED_CHEST_TRIGGERED, "trigger_trapped_chest"),
    Map.entry(Statistic.RECORD_PLAYED, "play_record"),
    Map.entry(Statistic.FURNACE_INTERACTION, "interact_with_furnace"),
    Map.entry(Statistic.CRAFTING_TABLE_INTERACTION, "interact_with_crafting_table"),
    Map.entry(Statistic.SLEEP_IN_BED, "sleep_in_bed"),
    Map.entry(Statistic.SHULKER_BOX_OPENED, "open_shulker_box"),
    Map.entry(Statistic.INTERACT_WITH_BLAST_FURNACE, "interact_with_blast_furnace"),
    Map.entry(Statistic.INTERACT_WITH_SMOKER, "interact_with_blast_furnace"),
    Map.entry(Statistic.INTERACT_WITH_LECTERN, "interact_with_lectern"),
    Map.entry(Statistic.INTERACT_WITH_CAMPFIRE, "interact_with_campfire"),
    Map.entry(Statistic.INTERACT_WITH_CARTOGRAPHY_TABLE, "interact_with_cartography_table"),
    Map.entry(Statistic.INTERACT_WITH_LOOM, "interact_with_loom"),
    Map.entry(Statistic.INTERACT_WITH_STONECUTTER, "interact_with_stonecutter"),
    Map.entry(Statistic.BELL_RING, "bell_ring"),
    Map.entry(Statistic.INTERACT_WITH_ANVIL, "interact_with_anvil"),
    Map.entry(Statistic.INTERACT_WITH_GRINDSTONE, "interact_with_grindstone"),
    Map.entry(Statistic.INTERACT_WITH_SMITHING_TABLE, "interact_with_smithing_table"),
    Map.entry(Statistic.OPEN_BARREL, "open_barrel"),
    Map.entry(Statistic.CHEST_OPENED, "open_chest"),
    Map.entry(Statistic.ENDERCHEST_OPENED, "open_enderchest"),
    Map.entry(Statistic.HOPPER_INSPECTED, "inspect_hopper"),
    Map.entry(Statistic.DROPPER_INSPECTED, "inspect_dropper"),
    Map.entry(Statistic.DISPENSER_INSPECTED, "inspect_dispenser"),
    Map.entry(Statistic.STRIDER_ONE_CM, "strider_one_cm"),
    Map.entry(Statistic.MINECART_ONE_CM, "minecart_one_cm"),
    Map.entry(Statistic.CLIMB_ONE_CM, "climb_one_cm"),
    Map.entry(Statistic.FLY_ONE_CM, "fly_one_cm"),
    Map.entry(Statistic.WALK_UNDER_WATER_ONE_CM, "walk_under_water_one_cm"),
    Map.entry(Statistic.BOAT_ONE_CM, "boat_one_cm"),
    Map.entry(Statistic.PIG_ONE_CM, "pig_one_cm"),
    Map.entry(Statistic.HORSE_ONE_CM, "horse_one_cm"),
    Map.entry(Statistic.CROUCH_ONE_CM, "crouch_one_cm"),
    Map.entry(Statistic.AVIATE_ONE_CM, "aviate_one_cm"),
    Map.entry(Statistic.WALK_ONE_CM, "walk_one_cm"),
    Map.entry(Statistic.WALK_ON_WATER_ONE_CM, "walk_on_water_one_cm"),
    Map.entry(Statistic.SWIM_ONE_CM, "swim_one_cm"),
    Map.entry(Statistic.FALL_ONE_CM, "fall_one_cm"),
    Map.entry(Statistic.SPRINT_ONE_CM, "sprint_one_cm")
  );

  /**
   * @return translation key usable when translating statistics in translation components
   */
  public static String getMinecraftTranslationKey(Statistic statistic) {
    return statistics.getOrDefault(statistic, statistic.name());
  }
}
