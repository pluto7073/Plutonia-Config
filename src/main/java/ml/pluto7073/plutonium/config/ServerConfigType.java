package ml.pluto7073.plutonium.config;

import ml.pluto7073.plutonium.PlutoniumConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class ServerConfigType {

    public static final ServerConfigType DEFAULT =
            Registry.register(PlutoniumConfig.SERVER_CONFIG_TYPES, PlutoniumConfig.id("plutonium"), new ServerConfigType(null, null));

    public final ServerConfig serverConfig;
    private final Supplier<ServerConfig> factory;
    private final boolean managed;

    @Environment(EnvType.CLIENT)
    private ServerConfig copy;

    public ServerConfigType(ServerConfig existing, Supplier<ServerConfig> factory) {
        this(existing, factory, false);
    }

    public ServerConfigType(ServerConfig existing, Supplier<ServerConfig> factory, boolean managed) {
        this.serverConfig = existing;
        this.factory = factory;
        this.managed = managed;
        if (existing != null) existing.type = this;
    }

    @Environment(EnvType.CLIENT)
    public ServerConfig updateCopy(FriendlyByteBuf buf) {
        if (copy == null) {
            copy = factory.get();
            copy.copy = true;
            copy.type = this;
        }

        copy.loadFromPacket(buf);

        return copy;
    }

    @Environment(EnvType.CLIENT)
    public ServerConfig getCopy() {
        return copy;
    }

    public ServerConfig updateOriginal(FriendlyByteBuf buf) {
        serverConfig.loadFromPacket(buf);

        return serverConfig;
    }

    public boolean isManaged() {
        return managed;
    }
}
