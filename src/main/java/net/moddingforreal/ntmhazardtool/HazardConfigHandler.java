package net.moddingforreal.ntmhazardtool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HazardConfigHandler {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File configFile;

    // -1 metadata represents a wildcard (matches all subtypes)
    private static final int WILDCARD_META = -1;
    public static final Map<ItemMetaPair, Map<String, Double>> HAZARD_REGISTRY = new HashMap<>();

    public static void init(File configDir) {
        // Creates and targets the 'config/ntmhazardtool/' folder
        File customDir = new File(configDir, "ntmhazardtool");
        if (!customDir.exists()) {
            customDir.mkdirs();
        }

        configFile = new File(customDir, "hazards.json");

        if (!configFile.exists()) {
            createDefaultConfig();
        }

        loadConfig();
    }

    private static void loadConfig() {
        HAZARD_REGISTRY.clear();
        FileReader reader = null;
        try {
            reader = new FileReader(configFile);
            Type type = new TypeToken<Map<String, Map<String, Double>>>(){}.getType();
            Map<String, Map<String, Double>> rawData = GSON.fromJson(reader, type);

            if (rawData != null) {
                for (Map.Entry<String, Map<String, Double>> entry : rawData.entrySet()) {
                    String[] parts = entry.getKey().split(",");
                    String registryName = parts[0].trim();

                    // If no comma is provided, assign WILDCARD_META (-1)
                    int meta = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : WILDCARD_META;

                    HAZARD_REGISTRY.put(new ItemMetaPair(registryName, meta), entry.getValue());
                }
            }
        } catch (Exception e) {
            FMLLog.log.error("[NTMHazardTool] Failed to load hazard configuration file!", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private static void createDefaultConfig() {
        FileWriter writer = null;
        try {
            Map<String, Map<String, Double>> defaultData = new HashMap<>();

            // Stone serves as a default wildcard template
            Map<String, Double> defaultHazard = new HashMap<>();
            defaultHazard.put("blinding", 2.0);
            defaultHazard.put("hot", 3.0);
            defaultData.put("minecraft:blaze_rod", defaultHazard);

            writer = new FileWriter(configFile);
            GSON.toJson(defaultData, writer);
        } catch (IOException e) {
            FMLLog.log.error("[NTMHazardTool] Failed to create default hazard config file!", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public static class ItemMetaPair {
        private final String item;
        private final int meta;

        public ItemMetaPair(@Nonnull String item, int meta) {
            this.item = item;
            this.meta = meta;
        }

        public String getItem() { return item; }
        public int getMeta() { return meta; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemMetaPair that = (ItemMetaPair) o;
            return meta == that.meta && item.equals(that.item);
        }

        @Override
        public int hashCode() {
            return 31 * item.hashCode() + meta;
        }
    }
}
