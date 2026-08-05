package ml.pluto7073.plutonium.networking.serverbound;

import ml.pluto7073.plutonium.PlutoniumConfig;
import ml.pluto7073.plutonium.config.ServerConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import ml.pluto7073.plutonium.networking.clientbound.ClientboundUpdateConfigPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

@MethodsReturnNonnullByDefault
public class ServerboundPackets {

    public static void registerPackets() {
        PayloadTypeRegistry.playC2S().register(UpdateConfigPacket.TYPE, UpdateConfigPacket.STREAM_CODEC);
    }

    public static void registerReceivers() {
        ServerPlayConnectionEvents.INIT.register((listener, server) -> {
            ServerPlayNetworking.registerGlobalReceiver(UpdateConfigPacket.TYPE, (payload, context) -> {
                ServerPlayer player = context.player();
                if (!player.hasPermissions(2) && !server.isSingleplayer()) {
                    payload.config.logger.warn("Unauthorized user {} tried to update config {}", player.getGameProfile().getName(), payload.config.configName);
                    return;
                }

                payload.config.logger.info("Received updated server config for {} from {}", payload.config.configName, player.getGameProfile().getName());
                if (payload.config.getType().isManaged()) return;
                ClientboundUpdateConfigPacket packet = new ClientboundUpdateConfigPacket(payload.config);
                for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                    if (p == player) continue;
                    ServerPlayNetworking.send(p, packet);
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

    public record UpdateConfigPacket(ServerConfig config) implements CustomPacketPayload {

        public static final Type<UpdateConfigPacket> TYPE =
                new Type<>(PlutoniumConfig.id("serverbound/update_config"));
        public static final StreamCodec<RegistryFriendlyByteBuf, UpdateConfigPacket> STREAM_CODEC =
                StreamCodec.of(UpdateConfigPacket::write, UpdateConfigPacket::read);

        public static void write(FriendlyByteBuf buf, UpdateConfigPacket packet) {
            buf.writeResourceLocation(PlutoniumConfig.SERVER_CONFIG_TYPES.getKey(packet.config.getType()));
            packet.config.writeToPacket(buf);
        }

        public static UpdateConfigPacket read(FriendlyByteBuf buf) {
            ResourceLocation configId = buf.readResourceLocation();
            ServerConfigType<?> type = PlutoniumConfig.SERVER_CONFIG_TYPES.get(configId);
            if (type == null) {
                throw new IllegalStateException("Unknown config type");
            }
            return new UpdateConfigPacket(type.updateOriginal(buf));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

}
