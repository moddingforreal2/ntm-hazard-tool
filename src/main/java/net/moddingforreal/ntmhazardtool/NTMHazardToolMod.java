package net.moddingforreal.ntmhazardtool;

import com.hbm.hazard.HazardData;
import com.hbm.hazard.HazardRegistry;
import com.hbm.hazard.HazardSystem;
import com.hbm.util.ArmorRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class NTMHazardToolMod {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);
        HazardConfigHandler.init(event.getSuggestedConfigurationFile());
        ProtectionConfigHandler.init(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        for (Map.Entry<HazardConfigHandler.ItemMetaPair,Map<String, Double>> entry :
                HazardConfigHandler.HAZARD_REGISTRY.entrySet()) {
            Item item = Item.getByNameOrId(entry.getKey().getItem());
            if (item == null) {
                LOGGER.warn("[NTMHazardTool] Could not find item with name {}", entry.getKey().getItem());
                continue;
            }
            ItemStack stack = new ItemStack(item, 1, entry.getKey().getMeta());

            // get all hazards
            // asbestos blinding coal cold contaminating digamma explosive hot hydroactive radiation toxic
            HazardData hazards = new HazardData();
            for (Map.Entry<String, Double> hazard : entry.getValue().entrySet()) {
                String name = hazard.getKey();
                switch (name) {
                    case "asbestos":
                        hazards.addEntry(HazardRegistry.ASBESTOS, hazard.getValue());
                        break;
                    case "blinding":
                        hazards.addEntry(HazardRegistry.BLINDING, hazard.getValue());
                        break;
                    case "coal":
                        hazards.addEntry(HazardRegistry.COAL, hazard.getValue());
                        break;
                    case "cold":
                        hazards.addEntry(HazardRegistry.COLD, hazard.getValue());
                        break;
                    case "contaminating":
                        hazards.addEntry(HazardRegistry.CONTAMINATING, hazard.getValue());
                        break;
                    case "digamma":
                        hazards.addEntry(HazardRegistry.DIGAMMA, hazard.getValue());
                        break;
                    case "explosive":
                        hazards.addEntry(HazardRegistry.EXPLOSIVE, hazard.getValue());
                        break;
                    case "hot":
                        hazards.addEntry(HazardRegistry.HOT, hazard.getValue());
                        break;
                    case "hydroactive":
                        hazards.addEntry(HazardRegistry.HYDROACTIVE,  hazard.getValue());
                        break;
                    case "radiation":
                        hazards.addEntry(HazardRegistry.RADIATION, hazard.getValue());
                        break;
                    case "toxic":
                        hazards.addEntry(HazardRegistry.TOXIC, hazard.getValue());
                        break;
                    default:
                        LOGGER.warn("[NTMHazardTool] Could not find hazard corresponding to \"{}\"", name);
                }
            }
            HazardSystem.register(stack, hazards);
        }


        for (Map.Entry<String,List<String>> entry :
                ProtectionConfigHandler.HAZARD_REGISTRY.entrySet()) {
            Item item = Item.getByNameOrId(entry.getKey());
            if (item == null) {
                LOGGER.warn("[NTMHazardTool] Could not find item with name {}", entry.getKey());
                continue;
            }

            // get all hazards
            // gas_lung gas_monoxide gas_inert particle_coarse particle_fine bacteria nerve_agent gas_blistering sand light
            ArrayList<ArmorRegistry.HazardClass> protections = new ArrayList<>();
            for (String protection : entry.getValue()) {
                switch (protection) {
                    case "gas_lung":
                        protections.add(ArmorRegistry.HazardClass.GAS_LUNG);
                        break;
                    case "gas_monoxide":
                        protections.add(ArmorRegistry.HazardClass.GAS_MONOXIDE);
                        break;
                    case "gas_inert":
                        protections.add(ArmorRegistry.HazardClass.GAS_INERT);
                        break;
                    case "particle_coarse":
                        protections.add(ArmorRegistry.HazardClass.PARTICLE_COARSE);
                        break;
                    case "particle_fine":
                        protections.add(ArmorRegistry.HazardClass.PARTICLE_FINE);
                        break;
                    case "bacteria":
                        protections.add(ArmorRegistry.HazardClass.BACTERIA);
                        break;
                    case "nerve_agent":
                        protections.add(ArmorRegistry.HazardClass.NERVE_AGENT);
                        break;
                    case "gas_blistering":
                        protections.add(ArmorRegistry.HazardClass.GAS_BLISTERING);
                        break;
                    case "sand":
                        protections.add(ArmorRegistry.HazardClass.SAND);
                        break;
                    case "light":
                        protections.add(ArmorRegistry.HazardClass.LIGHT);
                        break;
                    default:
                        LOGGER.warn("[NTMHazardTool] Could not find hazard protection corresponding to \"{}\"", protection);
                }
            }
            ArmorRegistry.hazardClasses.put(item, protections);
        }
    }

}
