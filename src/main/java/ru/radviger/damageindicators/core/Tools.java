package ru.radviger.damageindicators.core;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Tools {
    private static final Map<Class<? extends Entity>, EntityConfigurationEntry> entityMap = new HashMap<>();
    private static Tools instance;
    boolean lasttimefailed = false;

    public static Tools getInstance() {
        if (instance == null) {
            instance = new Tools();
        }

        return instance;
    }

    public static Map<Class<? extends Entity>, String> getEntityList() {
        Map<Class<? extends Entity>, String> ret = new HashMap<>();

        for (ResourceLocation rl : EntityList.getEntityNameList()) {
            ret.put(EntityList.getClass(rl), EntityList.getTranslationName(rl));
        }

        ret.put(EntityOtherPlayerMP.class, "OtherPlayers");
        return ret;
    }

    public Map<Class<? extends Entity>, EntityConfigurationEntry> getEntityMap() {
        if (entityMap.isEmpty()) {
            this.scanforEntities();
        }

        return entityMap;
    }

    public void registerCommands() {
    }

    public void RegisterRenders() {
        this.scanforEntities();
        MinecraftForge.EVENT_BUS.register(DITicker.instance);
    }

    public void scanforEntities() {
        this.searchMapForEntities(getEntityList());
    }

    private void searchMapForEntities(Map<Class<? extends Entity>, String> theMap) {
        Configuration config = EntityConfigurationEntry.getEntityConfiguration();
        this.lasttimefailed = false;
        Set<Class<? extends Entity>> set = theMap.keySet();

        for (Class<? extends Entity> entry : set) {
            if (entry != null && EntityLiving.class.isAssignableFrom(entry)) {
                entityMap.put(entry, EntityConfigurationEntry.generateDefaultConfiguration(config, entry));
            }
        }

        config.save();
    }
}
