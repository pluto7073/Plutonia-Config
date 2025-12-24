package ml.pluto7073.plutonium.networking.clientbound;

import ml.pluto7073.plutonium.PlutoniumConfig;
import ml.pluto7073.plutonium.config.ServerConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundUpdateConfigPacket(ServerConfig config) implements FabricPacket {
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(PlutoniumConfig.SERVER_CONFIG_TYPES.getKey(config.getType()));

        config.writeToPacket(buf);
    }

    @Override
    public PacketType<?> getType() {
        return ClientboundPackets.UPDATE_CONFIG;
    }

    public static ClientboundUpdateConfigPacket read(FriendlyByteBuf buf) {
        ResourceLocation configId = buf.readResourceLocation();
        ServerConfigType type = PlutoniumConfig.SERVER_CONFIG_TYPES.get(configId);
        if (type == null) {
            throw new IllegalStateException("Unknown config " + configId);
        }

        return new ClientboundUpdateConfigPacket(type.updateCopy(buf));
    }
}
