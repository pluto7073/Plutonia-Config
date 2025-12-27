package ml.pluto7073.plutonium.config;

import ml.pluto7073.plutonium.PlutoniumConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerConfigType<C extends ServerConfig> {

    public static final ServerConfigType<ServerConfig> DEFAULT =
            Registry.register(PlutoniumConfig.SERVER_CONFIG_TYPES, PlutoniumConfig.id("plutonium"), new ServerConfigType<>(null, null));

    public final C serverConfig;
    private final BiFunction<ServerConfigType<?>, Boolean, C> factory;
    private final boolean managed;

    @Environment(EnvType.CLIENT)
    private C copy;

    public ServerConfigType(C existing, BiFunction<ServerConfigType<?>, Boolean, C> factory) {
        this(existing, factory, false);
    }

    public ServerConfigType(C existing, BiFunction<ServerConfigType<?>, Boolean, C> factory, boolean managed) {
        this.serverConfig = existing;
        this.factory = factory;
        this.managed = managed;
        if (existing != null) existing.type = this;
    }

    @Environment(EnvType.CLIENT)
    public C updateCopy(FriendlyByteBuf buf) {
        if (copy == null) {
            copy = factory.apply(this, true);
            copy.type = this;
        }

        copy.loadFromPacket(buf);

        return copy;
    }

    @Environment(EnvType.CLIENT)
    public C getCopy() {
        return copy;
    }

    public C updateOriginal(FriendlyByteBuf buf) {
        serverConfig.loadFromPacket(buf);

        return serverConfig;
    }

    public boolean isManaged() {
        return managed;
    }
}
