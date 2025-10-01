package net.ultrastudios.lorelink.utils.config;

public interface IFallbackEnum<T extends Enum<T>> {
    T getFallback();
}
