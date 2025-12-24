package ml.pluto7073.plutonium.client.gui;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import ml.pluto7073.plutonium.config.AbstractConfig;
import ml.pluto7073.plutonium.config.ClientConfig;
import ml.pluto7073.plutonium.config.ServerConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.function.Function;

/**
 * Requires ClothConfig for this functionality to work
 */
public class PlutoniumConfigScreen implements Function<Screen, Screen> {

    private final ClientConfig client;
    private final ServerConfigType serverType;
    private final String modid;

    public PlutoniumConfigScreen(ClientConfig client, ServerConfigType serverType, String modid) {
        this.client = client;
        this.serverType = serverType;
        this.modid = modid;
    }

    @Override
    public Screen apply(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title." + modid + ".config"))
                .setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/gui/options_background.png"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        if (client != null) {
            ConfigCategory clientCategory =
                    builder.getOrCreateCategory(Component.translatable("title.plutonium.client"));

            client.fields.forEach((key, instance) ->
                    addConfigEntry("client." + key, instance, clientCategory, entryBuilder));
        }

        ServerConfig server = null;

        if (serverType != null && Minecraft.getInstance().getConnection() == null) {
            server = serverType.serverConfig;
        } else if (serverType != null && Minecraft.getInstance().player != null && (Minecraft.getInstance().player.hasPermissions(2) || Minecraft.getInstance().hasSingleplayerServer())) {
            server = serverType.getCopy();
        }

        ServerConfig cfg = server;

        if (server != null) {
            ConfigCategory serverCategory =
                    builder.getOrCreateCategory(Component.translatable("title.plutonium.server"));

            server.fields.forEach((key, instance) ->
                    addConfigEntry("common." + key, instance, serverCategory, entryBuilder));
        }

        builder.setSavingRunnable(() -> {
            if (client != null) {
                client.save();
            }
            if (cfg != null) {
                cfg.save();
            }
        });

        return builder.build();
    }

    private void addConfigEntry(String key, AbstractConfig.OptionInstance instance, ConfigCategory category, ConfigEntryBuilder builder) {
        if (instance instanceof AbstractConfig.BooleanInstance bool) {
            category.addEntry(builder.startBooleanToggle(translatable(key), bool.getValue())
                    .setDefaultValue((boolean) bool.getDefaultVal())
                    .setTooltip(instance.hasTooltip() ? translatable(key + ".desc") : null)
                    .setSaveConsumer(bool::setValue)
                    .build());
        } else if (instance instanceof AbstractConfig.DoubleInstance doubleInst) {
            category.addEntry(builder.startDoubleField(translatable(key), doubleInst.getValue())
                    .setDefaultValue((double) doubleInst.getDefaultVal())
                    .setTooltip(instance.hasTooltip() ? translatable(key + ".desc") : null)
                    .setSaveConsumer(d -> {
                        doubleInst.setValue(d > doubleInst.getMaxVal() && doubleInst.getMaxVal() > doubleInst.getMinVal() ? doubleInst.getMaxVal() :
                                (d < doubleInst.getMinVal() && doubleInst.getMaxVal() > doubleInst.getMinVal() ? doubleInst.getMinVal() : d));
                    })
                    .build());
        } else if (instance instanceof AbstractConfig.EnumInstance enumInst) {
            //noinspection unchecked
            category.addEntry(builder.startEnumSelector(translatable(key), (Class<Enum<?>>) enumInst.getEnumClass(), enumInst.getValue())
                    .setDefaultValue((Enum<?>) enumInst.getDefaultVal())
                    .setTooltip(instance.hasTooltip() ? translatable(key + ".desc") : null)
                    .setSaveConsumer(enumInst::setValue)
                    .setEnumNameProvider(anEnum -> translatable(key + "." + anEnum.name().toLowerCase(Locale.ROOT)))
                    .build());
        } else if (instance instanceof AbstractConfig.IntInstance intInst) {
            if (intInst.getMaxVal() > intInst.getMinVal()) {
                category.addEntry(builder.startIntSlider(translatable(key), intInst.getValue(), intInst.getMinVal(), intInst.getMaxVal())
                        .setDefaultValue((Integer) intInst.getDefaultVal())
                        .setTooltip(instance.hasTooltip() ? translatable(key + ".desc") : null)
                        .setSaveConsumer(intInst::setValue)
                        .build());
            } else {
                category.addEntry(builder.startIntField(translatable(key), intInst.getValue())
                        .setDefaultValue((Integer) intInst.getDefaultVal())
                        .setTooltip(instance.hasTooltip() ? translatable(key + ".desc") : null)
                        .setSaveConsumer(intInst::setValue)
                        .build());
            }
        } else if (instance instanceof AbstractConfig.LongInstance longInst) {
            if (longInst.getMaxVal() > longInst.getMinVal()) {
                category.addEntry(builder.startLongSlider(translatable(key), longInst.getValue(), longInst.getMinVal(), longInst.getMaxVal())
                        .setDefaultValue((Long) longInst.getDefaultVal())
                        .setTooltip(instance.hasTooltip() ? translatable(key + ".desc") : null)
                        .setSaveConsumer(longInst::setValue)
                        .build());
            } else {
                category.addEntry(builder.startLongField(translatable(key), longInst.getValue())
                        .setDefaultValue((Long) longInst.getDefaultVal())
                        .setTooltip(instance.hasTooltip() ? translatable(key + ".desc") : null)
                        .setSaveConsumer(longInst::setValue)
                        .build());
            }
        } else if (instance instanceof AbstractConfig.StringInstance str) {
            category.addEntry(builder.startStrField(translatable(key), str.getValue())
                    .setDefaultValue((String) str.getDefaultVal())
                    .setTooltip(instance.hasTooltip() ? translatable(key + ".desc") : null)
                    .setSaveConsumer(str::setValue)
                    .build());
        }
    }

    private Component translatable(String name) {
        return Component.translatable("option." + modid + "." + name);
    }

}
