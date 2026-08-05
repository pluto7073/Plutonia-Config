package ml.pluto7073.plutonium.networking.serverbound;

import ml.pluto7073.plutonium.PlutoniumConfig;
import ml.pluto7073.plutonium.config.ServerConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import ml.pluto7073.plutonium.networking.clientbound.ClientboundUpdateConfigPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class ServerboundPackets {

    public static final PacketType<UpdateConfigPacket> UPDATE_CONFIG =
            PacketType.create(PlutoniumConfig.id("serverbound/update_config"), UpdateConfigPacket::read);

    public static void init() {
        ServerPlayConnectionEvents.INIT.register((listener, server) -> {

            ServerPlayNetworking.registerGlobalReceiver(UPDATE_CONFIG.getId(), (server1, player, handler, buf, responseSender) -> {
                if (!player.hasPermissions(2) && !server1.isSingleplayer()) {
                    ServerConfig config = Objects.requireNonNull(PlutoniumConfig.SERVER_CONFIG_TYPES.get(buf.readResourceLocation())).serverConfig;
                    config.logger.warn("Unauthorized user {} tried to update config {}", player.getGameProfile().getName(), config.configName);
                    buf.readNbt();
                    return;
                }

                UpdateConfigPacket packet = UPDATE_CONFIG.read(buf);
                packet.config.logger.info("Received updated server config for {} from {}", packet.config().configName, player.getGameProfile().getName());
                if (packet.config.getType().isManaged()) return;
                ClientboundUpdateConfigPacket newPacket = new ClientboundUpdateConfigPacket(packet.config);
                for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                    ServerPlayNetworking.send(p, newPacket);
                }
            });

        });

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, manager) -> {
            PlutoniumConfig.SERVER_CONFIG_TYPES.forEach(type -> {
                if (type.serverConfig == null) return;
                if (type.isManaged()) return;
                type.serverConfig.logger.info("Reloading config {}", type.serverConfig.configName);
                type.serverConfig.load();
            });
        });

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, b) -> {
            PlutoniumConfig.SERVER_CONFIG_TYPES.forEach(type -> {
                if (type.serverConfig == null) return;
                if (type.isManaged()) return;
                ClientboundUpdateConfigPacket packet = new ClientboundUpdateConfigPacket(type.serverConfig);
                ServerPlayNetworking.send(player, packet);
            });
        });
    }

    public record UpdateConfigPacket(ServerConfig config) implements FabricPacket {
        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeResourceLocation(PlutoniumConfig.SERVER_CONFIG_TYPES.getKey(config.getType()));
            config.writeToPacket(buf);
        }

        @Override
        public PacketType<?> getType() {
            return UPDATE_CONFIG;
        }

        public static UpdateConfigPacket read(FriendlyByteBuf buf) {
            ResourceLocation configId = buf.readResourceLocation();
            ServerConfigType<?> type = PlutoniumConfig.SERVER_CONFIG_TYPES.get(configId);
            if (type == null) {
                throw new IllegalStateException("Unknown config type");
            }
            return new UpdateConfigPacket(type.updateOriginal(buf));
        }
    }

}
