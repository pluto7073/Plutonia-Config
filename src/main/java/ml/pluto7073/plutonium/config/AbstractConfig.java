package ml.pluto7073.plutonium.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import ml.pluto7073.plutonium.annotations.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected final Map<String, OptionInstance> fields;
    public final String configName;
    public final String modid;
    public final Logger logger;

    public AbstractConfig(String modid, String name, Logger logger) {
        HashMap<String, OptionInstance> fields = new HashMap<>();
        this.configName = modid + "_" + name;
        this.modid = modid;
        this.logger = logger;

        for (Field field : this.getClass().getFields()) {
            OptionInstance instance = null;
            if (field.isAnnotationPresent(BooleanOption.class)) {
                instance = new BooleanInstance(field, field.getAnnotation(BooleanOption.class));
            } else if (field.isAnnotationPresent(DoubleOption.class)) {
                instance = new DoubleInstance(field, field.getAnnotation(DoubleOption.class));
            } else if (field.isAnnotationPresent(EnumOption.class)) {
                instance = new EnumInstance(field, field.getAnnotation(EnumOption.class));
            } else if (field.isAnnotationPresent(IntOption.class)) {
                instance = new IntInstance(field, field.getAnnotation(IntOption.class));
            } else if (field.isAnnotationPresent(LongOption.class)) {
                instance = new LongInstance(field, field.getAnnotation(LongOption.class));
            } else if (field.isAnnotationPresent(StringOption.class)) {
                instance = new StringInstance(field, field.getAnnotation(StringOption.class));
            }

            if (instance != null) {
                if (!field.canAccess(this)) {
                    throw new RuntimeException("Unable to access field " + field.getName() + ", field must be public and non-final");
                }
                fields.put(field.getName(), instance);
            }
        }

        this.fields = ImmutableMap.copyOf(fields);
    }

    public @Nullable List<Pair<String, Map<String, OptionInstance>>> getSubCategories() {
        return null;
    }

    public @Nullable Map<String, String> getSubCategoryNames() {
        return null;
    }

    public void load() {
        File cfgFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), configName + ".json");
        if (!cfgFile.exists()) {
            loadValues(Maps.newHashMap());
            return;
        }

        try (FileReader reader = new FileReader(cfgFile)) {
            JsonObject data = GsonHelper.parse(reader);
            HashMap<String, Object> deserialized = new HashMap<>();

            for (String s : data.keySet()) {
                JsonElement value = data.get(s);
                if (value instanceof JsonPrimitive prim) {
                    if (prim.isBoolean()) {
                        deserialized.put(s, prim.getAsBoolean());
                    } else if (prim.isString()) {
                        deserialized.put(s, prim.getAsString());
                    } else if (prim.isNumber()) {
                        deserialized.put(s, prim.getAsNumber());
                    }
                }
            }

            loadValues(deserialized);
        } catch (Exception e) {
            logger.error("Failed to load config {}", configName, e);
            loadValues(Maps.newHashMap());
        }
    }

    public void save() {
        File cfgFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), configName + ".json");
        if (!cfgFile.exists()) {
            try {
                cfgFile.createNewFile();
            } catch (IOException e) {
                logger.error("Failed to create config file for {}", configName, e);
                return;
            }
        }

        JsonObject serialized = new JsonObject();
        for (String key : fields.keySet()) {
            OptionInstance inst = fields.get(key);
            if (inst instanceof BooleanInstance bool) {
                serialized.addProperty(key, bool.getValue());
            } else if (inst instanceof DoubleInstance d) {
                serialized.addProperty(key, d.getValue());
            } else if (inst instanceof EnumInstance e) {
                serialized.addProperty(key, e.getValueStr());
            } else if (inst instanceof IntInstance i) {
                serialized.addProperty(key, i.getValue());
            } else if (inst instanceof LongInstance l) {
                serialized.addProperty(key, l.getValue());
            } else if (inst instanceof StringInstance str) {
                serialized.addProperty(key, str.getValue());
            }
        }

        try (FileWriter writer = new FileWriter(cfgFile)) {
            GSON.toJson(serialized, writer);
        } catch (Exception e) {
            logger.error("Failed to save config for {}", configName, e);
        }
    }

    public Map<String, OptionInstance> getFields() {
        return fields;
    }

    public void loadValues(Map<String, Object> values) {
        loadValues(values, true);
    }

    public void loadValues(Map<String, Object> values, boolean reset) {
        for (String field : fields.keySet()) {
            if (values.containsKey(field)) {
                fields.get(field).setValue(values.get(field));
            } else if (reset) {
                fields.get(field).setValue(fields.get(field).getDefaultVal());
            }
        }
    }

    public abstract class OptionInstance {
        protected Field field;

        protected OptionInstance(Field field) {
            this.field = field;
        }

        public void setValue(Object value) {
            try {
                field.set(AbstractConfig.this, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to set config value", e);
            }
        }

        protected Object getRawValue() {
            try {
                return field.get(AbstractConfig.this);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to retrieve config value", e);
            }
        }

        public abstract Object getDefaultVal();
        public abstract boolean hasTooltip();
    }

    public class BooleanInstance extends OptionInstance {

        protected final boolean defaultVal;
        protected final boolean hasTooltip;

        protected BooleanInstance(Field field, BooleanOption data) {
            super(field);
            if (field.getType() != Boolean.class && field.getType() != boolean.class) {
                throw new IllegalArgumentException("@BooleanOption only allowed on Boolean type fields");
            }
            defaultVal = data.defaultVal();
            hasTooltip = data.hasTooltip();
        }

        @Override
        public Object getDefaultVal() {
            return defaultVal;
        }

        @Override
        public boolean hasTooltip() {
            return hasTooltip;
        }

        public boolean getValue() {
            return (boolean) getRawValue();
        }

        @Override
        public void setValue(Object value) {
            if (value instanceof Byte b) {
                super.setValue(b == 1);
            } else if (value instanceof Integer i) {
                super.setValue(i == 1);
            } else {
                super.setValue(value);
            }
        }
    }

    public class DoubleInstance extends OptionInstance {

        protected final double minVal;
        protected final double maxVal;
        protected final double defaultVal;
        protected final boolean hasTooltip;

        protected DoubleInstance(Field field, DoubleOption data) {
            super(field);
            minVal = data.min();
            maxVal = data.max();
            defaultVal = data.defaultVal();
            hasTooltip = data.hasTooltip();
            if (field.getType() != Double.class && field.getType() != double.class) {
                throw new IllegalArgumentException("@DoubleOption only allowed on Double type fields");
            }
        }

        @Override
        public void setValue(Object value) {
            if (!(value instanceof Double d)) {
                if (value instanceof Number n) {
                    super.setValue(n.doubleValue());
                } else {
                    super.setValue(Double.parseDouble(value.toString()));
                }
            } else super.setValue(d);
        }

        public double getMinVal() {
            return minVal;
        }

        public double getMaxVal() {
            return maxVal;
        }

        @Override
        public Object getDefaultVal() {
            return defaultVal;
        }

        @Override
        public boolean hasTooltip() {
            return hasTooltip;
        }

        public double getValue() {
            return (double) getRawValue();
        }
    }

    public class EnumInstance extends OptionInstance {

        protected final Class<? extends Enum<?>> enumClass;
        protected final Object defaultVal;
        protected final boolean hasTooltip;

        protected EnumInstance(Field field, EnumOption data) {
            super(field);

            //noinspection unchecked
            enumClass = (Class<? extends Enum<?>>) field.getType();

            defaultVal = getEnumConstant(data.value());
            hasTooltip = data.hasTooltip();
        }

        public Class<? extends Enum<?>> getEnumClass() {
            return enumClass;
        }

        @Override
        public Object getDefaultVal() {
            return defaultVal;
        }

        @Override
        public boolean hasTooltip() {
            return hasTooltip;
        }

        @Override
        public void setValue(Object value) {
            if (value instanceof String name) {
                super.setValue(getEnumConstant(name));
            } else {
                super.setValue(value);
            }
        }

        private Enum<?> getEnumConstant(String name) {
            Enum<?> val = null;

            for (Enum<? extends Enum<?>> enumConstant : enumClass.getEnumConstants()) {
                if (enumConstant.name().equals(name)) {
                    val = enumConstant;
                }
            }

            if (val == null) {
                throw new IllegalArgumentException("Couldn't find enum value " + name + " in enum class " + enumClass.getTypeName());
            }

            return val;
        }

        public Enum<?> getValue() {
            return (Enum<?>) getRawValue();
        }

        public String getValueStr() {
            return getValue().name();
        }
    }

    public class IntInstance extends OptionInstance {

        protected final int minVal;
        protected final int maxVal;
        protected final int defaultVal;
        protected final boolean hasTooltip;

        protected IntInstance(Field field, IntOption data) {
            super(field);
            minVal = data.min();
            maxVal = data.max();
            defaultVal = data.defaultVal();
            hasTooltip = data.hasTooltip();

            if (field.getType() != int.class && field.getType() != Integer.class) {
                throw new IllegalArgumentException("@IntOption only allowed on int type fields");
            }
        }

        @Override
        public void setValue(Object value) {
            if (!(value instanceof Integer i)) {
                if (value instanceof Number n) {
                    super.setValue(n.intValue());
                } else {
                    super.setValue(Integer.parseInt(value.toString()));
                }
            } else super.setValue(i);
        }

        @Override
        public Object getDefaultVal() {
            return defaultVal;
        }

        @Override
        public boolean hasTooltip() {
            return hasTooltip;
        }

        public int getMinVal() {
            return minVal;
        }

        public int getMaxVal() {
            return maxVal;
        }

        public int getValue() {
            return (int) getRawValue();
        }
    }

    public class LongInstance extends OptionInstance {

        protected final long minVal;
        protected final long maxVal;
        protected final long defaultVal;
        protected final boolean hasTooltip;

        protected LongInstance(Field field, LongOption data) {
            super(field);
            minVal = data.min();
            maxVal = data.max();
            defaultVal = data.defaultVal();
            hasTooltip = data.hasTooltip();

            if (field.getType() != long.class && field.getType() != Long.class) {
                throw new IllegalArgumentException("@LongOption only allowed on long type fields");
            }
        }

        @Override
        public void setValue(Object value) {
            if (!(value instanceof Long l)) {
                if (value instanceof Number n) {
                    super.setValue(n.longValue());
                } else {
                    super.setValue(Long.parseLong(value.toString()));
                }
            } else super.setValue(l);
        }

        public long getMinVal() {
            return minVal;
        }

        public long getMaxVal() {
            return maxVal;
        }

        @Override
        public Object getDefaultVal() {
            return defaultVal;
        }

        @Override
        public boolean hasTooltip() {
            return hasTooltip;
        }

        public long getValue() {
            return (long) getRawValue();
        }
    }

    public class StringInstance extends OptionInstance {

        protected final String defaultVal;
        protected final int maxLength;
        protected final boolean hasTooltip;

        protected StringInstance(Field field, StringOption data) {
            super(field);
            defaultVal = data.defaultVal();
            maxLength = data.maxLength();
            hasTooltip = data.hasTooltip();

            if (field.getType() != String.class) {
                throw new IllegalArgumentException("@StringOption only allowed on String type fields");
            }
        }

        @Override
        public boolean hasTooltip() {
            return hasTooltip;
        }

        @Override
        public Object getDefaultVal() {
            return defaultVal;
        }

        public String getValue() {
            return (String) getRawValue();
        }
    }


}
