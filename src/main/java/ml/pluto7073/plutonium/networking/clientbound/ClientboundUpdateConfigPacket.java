package ml.pluto7073.plutonium.networking.clientbound;

import ml.pluto7073.plutonium.PlutoniumConfig;
import ml.pluto7073.plutonium.config.ServerConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

@MethodsReturnNonnullByDefault
public record ClientboundUpdateConfigPacket(ServerConfig config) implements CustomPacketPayload {

    public static final Type<ClientboundUpdateConfigPacket> TYPE = new Type<>(PlutoniumConfig.id("clientbound/update_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateConfigPacket> STREAM_CODEC =
            StreamCodec.of(ClientboundUpdateConfigPacket::write, ClientboundUpdateConfigPacket::read);

    public static void write(FriendlyByteBuf buf, ClientboundUpdateConfigPacket packet) {
        buf.writeResourceLocation(PlutoniumConfig.SERVER_CONFIG_TYPES.getKey(packet.config.getType()));

        packet.config.writeToPacket(buf);
    }

    public static ClientboundUpdateConfigPacket read(FriendlyByteBuf buf) {
        ResourceLocation configId = buf.readResourceLocation();
        ServerConfigType<?> type = PlutoniumConfig.SERVER_CONFIG_TYPES.get(configId);
        if (type == null) {
            throw new IllegalStateException("Unknown config " + configId);
        }

        return new ClientboundUpdateConfigPacket(type.updateCopy(buf));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
