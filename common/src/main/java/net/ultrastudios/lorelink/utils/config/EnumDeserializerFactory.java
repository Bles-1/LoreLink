package net.ultrastudios.lorelink.utils.config;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

class EnumDeserializerFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, @NotNull TypeToken<T> typeToken) {
        Class<? super T> rawType = typeToken.getRawType();

        if (!Enum.class.isAssignableFrom(rawType)) {
            return null;
        }

        final Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) rawType.asSubclass(Enum.class);

        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(((Enum<?>) value).name());
                }
            }

            @Override
            public T read(JsonReader in) throws IOException {
                JsonToken peek = in.peek();
                if (peek == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }

                String str = in.nextString();
                if (str == null) return null;

                for (Enum<?> c : enumClass.getEnumConstants()) {
                    if (c.name().equals(str)) {
                        return (T) c;
                    }
                }

                if (IFallbackEnum.class.isAssignableFrom(enumClass)) {
                    Enum<?>[] constants = enumClass.getEnumConstants();
                    if (constants != null && constants.length > 0) {
                        IFallbackEnum<?> holder = (IFallbackEnum<?>) constants[0];
                        Object fb = holder.getFallback();
                        return (T) fb;
                    }
                }

                return null;
            }
        };
    }
}
