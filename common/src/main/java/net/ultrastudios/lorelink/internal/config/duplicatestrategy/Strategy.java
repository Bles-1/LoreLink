package net.ultrastudios.lorelink.internal.config.duplicatestrategy;

import com.google.gson.JsonDeserializer;
import net.ultrastudios.lorelink.utils.config.IFallbackEnum;

public enum Strategy implements IFallbackEnum<Strategy> {
    FAIL,
    FIRST,
    LAST,
    ONLY,
    COMBINE;

    @Override
    public Strategy getFallback() {
        return FAIL;
    }
}
