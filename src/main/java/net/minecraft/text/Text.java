package net.minecraft.text;

import com.google.gson.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public interface Text {
    class Serializer implements JsonDeserializer<MutableText>, JsonSerializer<Text> {
        @Override
        public MutableText deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return null;
        }
        @Override
        public JsonElement serialize(Text src, Type typeOfSrc, JsonSerializationContext context) {
            return null;
        }
        @Nullable
        public static MutableText fromJson(String json) {
            return null;
        }
    }
}
