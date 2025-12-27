package ml.pluto7073.plutonium.config;

import ml.pluto7073.plutonium.networking.serverbound.ServerboundPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

public class ServerConfig extends AbstractConfig {

    protected final boolean copy;
    protected ServerConfigType<?> type;

    public ServerConfig(String modid, Logger logger, ServerConfigType<?> type, boolean copy) {
        super(modid, "server", logger);
        this.type = type;
        this.copy = copy;
    }

    public ServerConfigType<?> getType() {
        return type;
    }

    public CompoundTag serialize() {
        CompoundTag serialized = new CompoundTag();

        for (String key : fields.keySet()) {
            OptionInstance inst = fields.get(key);
            if (inst instanceof BooleanInstance bool) {
                serialized.putBoolean(key, bool.getValue());
            } else if (inst instanceof DoubleInstance d) {
                serialized.putDouble(key, d.getValue());
            } else if (inst instanceof EnumInstance e) {
                serialized.putString(key, e.getValueStr());
            } else if (inst instanceof IntInstance i) {
                serialized.putInt(key, i.getValue());
            } else if (inst instanceof LongInstance l) {
                serialized.putLong(key, l.getValue());
            } else if (inst instanceof StringInstance str) {
                serialized.putString(key, str.getValue());
            }
        }

        return serialized;
    }

    public void writeToPacket(FriendlyByteBuf buf) {
        buf.writeNbt(serialize());
    }

    public final void saveRaw() {
        if (!copy) save();
    }

    @Override
    public void load() {
        if (!copy) {
            super.load();
        }
    }

    @Override
    public void save() {
        if (!copy) {
            super.save();
        } else if (type == null || !type.isManaged()) {
            ServerboundPackets.UpdateConfigPacket packet = new ServerboundPackets.UpdateConfigPacket(this);
            ClientPlayNetworking.send(packet);
        }
    }

    public void loadFromTag(CompoundTag serialized) {
        HashMap<String, Object> deserialized = new HashMap<>();

        for (String key : serialized.getAllKeys()) {
            Tag tag = serialized.get(key);
            if (tag instanceof NumericTag d) {
                deserialized.put(key, d.getAsNumber());
            } else if (tag instanceof StringTag str) {
                deserialized.put(key, str.getAsString());
            }
        }

        loadValues(deserialized, false);

        saveRaw();
    }

    public void loadFromPacket(FriendlyByteBuf buf) {
        CompoundTag serialized = buf.readAnySizeNbt();
        if (serialized == null) return;
        loadFromTag(serialized);
    }

}
