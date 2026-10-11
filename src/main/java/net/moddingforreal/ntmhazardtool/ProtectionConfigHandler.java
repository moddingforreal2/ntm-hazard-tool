package net.moddingforreal.ntmhazardtool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.common.FMLLog;
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

public class ProtectionConfigHandler {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File configFile;

    // -1 metadata represents a wildcard (matches all subtypes)
    private static final int WILDCARD_META = -1;
    public static final Map<String, List<String>> HAZARD_REGISTRY = new HashMap<>();

    public static void init(File configDir) {
        // Creates and targets the 'config/ntmhazardtool/' folder
        File customDir = new File(configDir, "ntmhazardtool");
        if (!customDir.exists()) {
            customDir.mkdirs();
        }

        configFile = new File(customDir, "protections.json");

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
            Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
            Map<String, List<String>> rawData = GSON.fromJson(reader, type);

            if (rawData != null) {
                HAZARD_REGISTRY.putAll(rawData);
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
            Map<String, List<String>> defaultData = new HashMap<>();

            // Stone serves as a default wildcard template
            List<String> defaultProtection = new ArrayList<>();
            defaultProtection.add("light");
            defaultProtection.add("particle_coarse");
            defaultData.put("minecraft:pumpkin", defaultProtection);

            writer = new FileWriter(configFile);
            GSON.toJson(defaultData, writer);
        } catch (IOException e) {
            FMLLog.log.error("[NTMHazardTool] Failed to create default hazard config file!", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
}
