package com.github.shioku.vanillautility.misc.inventory.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

  private final Type mapType = new TypeToken<Map<String, Object>>() {}.getType();

  @Override
  public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src.serialize());
  }

  @Override
  public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    return ItemStack.deserialize(context.deserialize(json, this.mapType));
  }
}
