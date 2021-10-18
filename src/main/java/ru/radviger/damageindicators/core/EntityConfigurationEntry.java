package ru.radviger.damageindicators.core;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import ru.radviger.damageindicators.DamageIndicators;
import ru.radviger.damageindicators.configuration.IndicatorsConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class EntityConfigurationEntry {
    public static Map<Integer, Integer> maxHealthOverride = new HashMap<>(200);
    private static boolean lastFailed = false;
    public final float babyScale;
    public final Class<? extends EntityLivingBase> clazz;
    public final float sizeScaling;
    public final boolean ignore;
    public final String nameOverride;
    public final float scale;
    public final float offsetX;
    public final float offsetY;
    public float eyeHeight;
    public int maxHP;

    public EntityConfigurationEntry(Class<? extends EntityLivingBase> clazz, float scale, float offsetX, float offsetY, float sizeScaling, float babyScale, String nameOverride, boolean ignore, int maxHP, float eyeHeight) {
        this.ignore = ignore;
        this.clazz = clazz;
        this.scale = scale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.sizeScaling = sizeScaling;
        this.babyScale = babyScale;
        if (nameOverride != null) {
            this.nameOverride = nameOverride;
        } else {
            this.nameOverride = "";
        }

        this.maxHP = maxHP;
        this.eyeHeight = eyeHeight;
    }

    public static EntityConfigurationEntry generateDefaultConfiguration(Configuration config, Class<? extends EntityLivingBase> entry) {
        boolean ignore = false;
        float scaleFactor = 22.0F;
        float xOffset = 0.0F;
        float yOffset = -5.0F;
        float SizeModifier = 0.0F;
        float BabyScaleFactor = 2.0F;
        if (entry == EntityIronGolem.class) {
            scaleFactor = 16.0F;
        } else if (entry == EntitySlime.class || entry == EntityMagmaCube.class) {
            scaleFactor = 5.0F;
            SizeModifier = 2.0F;
            yOffset = -5.0F;
        } else if (entry == EntityEnderman.class) {
            scaleFactor = 15.0F;
        } else if (entry == EntityGhast.class) {
            scaleFactor = 7.0F;
            yOffset = -20.0F;
        } else if (entry == EntitySquid.class) {
            yOffset = -17.0F;
        } else if (entry == EntityOcelot.class) {
            scaleFactor = 25.0F;
            yOffset = -5.0F;
        } else if (entry == EntityWither.class) {
            scaleFactor = 15.0F;
            yOffset = 5.0F;
        } else if (entry.getName().equalsIgnoreCase("thaumcraft.common.entities.EntityWisp")) {
            yOffset = -14.0F;
        } else if (entry.getName().equalsIgnoreCase("drzhark.mocreatures.MoCEntityWerewolf")) {
            scaleFactor = 20.0F;
            yOffset = -4.0F;
        } else if (entry.getName().equalsIgnoreCase("drzhark.mocreatures.MoCEntityOgre")) {
            scaleFactor = 12.0F;
        } else if (entry.getName().equalsIgnoreCase("xolova.blued00r.divinerpg.mobs.EntityCyclops")) {
            scaleFactor = 10.0F;
        } else if (entry.getName().equalsIgnoreCase("xolova.blued00r.divinerpg.mobs.EntityEnergyGolem")) {
            scaleFactor = 10.0F;
        } else if (entry.getName().equalsIgnoreCase("xolova.blued00r.divinerpg.mobs.EntityCaveclops")) {
            scaleFactor = 10.0F;
        } else if (Loader.isModLoaded("RDVehicleTools")) {
            try {
                Class<?> clazz = Class.forName("net.richdigitsmods.vehiclecore.vehicles.EntityVehicleCore");
                if (clazz.isAssignableFrom(entry)) {
                    ignore = true;
                }
            } catch (Throwable ignored) {
            }
        }

        return loadEntityConfig(config, new EntityConfigurationEntry(entry, scaleFactor, xOffset, yOffset, SizeModifier, BabyScaleFactor, "", ignore, 20, 1.5F));
    }

    public static EntityConfigurationEntry loadEntityConfig(Configuration config, EntityConfigurationEntry ece) {
        return loadEntityConfig(config, ece, null);
    }

    public static EntityConfigurationEntry loadEntityConfig(Configuration config, EntityConfigurationEntry ece, EntityLiving el) {
        Class<? extends EntityLivingBase> entry = ece.clazz;
        String mod = "Vanilla";
        EntityRegistration er = EntityRegistry.instance().lookupModSpawn(ece.clazz, true);
        if (er != null) {
            mod = er.getContainer().getMetadata().name.replaceAll(Pattern.quote("."), "");
        }

        String CatagoryName = entry.getName();
        if (CatagoryName.lastIndexOf(".") != -1) {
            CatagoryName = CatagoryName.substring(CatagoryName.lastIndexOf("."));
            CatagoryName = CatagoryName.replaceAll(Pattern.quote("."), "");
        }

        CatagoryName = mod + "." + CatagoryName;
        CatagoryName = CatagoryName.replaceAll("[^a-zA-Z0-9\\s\\!\\:\\.\\&\\$]", "");
        config.addCustomCategoryComment(CatagoryName, "These settings are to help other modders and users to make custom mobs fit correctly in the preview window.");
        Property prop = config.get(CatagoryName, "Scale_Factor", String.valueOf(ece.scale));

        float scaleFactor;
        try {
            scaleFactor = Float.parseFloat(prop.getString());
        } catch (NumberFormatException e) {
            System.err.println("Invalid or malformed configuration entry for " + prop.getName());
            scaleFactor = ece.scale;
            prop.set(String.valueOf(22.0F));
        }

        prop = config.get(CatagoryName, "Name", "");
        String entityName = prop.getString();
        prop = config.get(CatagoryName, "Ignore_This_Mob", ece.ignore);
        boolean ignore = prop.getBoolean(ece.ignore);
        prop = config.get(CatagoryName, "X_Offset", String.valueOf(ece.offsetX));

        float xOffset;
        try {
            xOffset = Float.parseFloat(prop.getString());
        } catch (NumberFormatException e) {
            System.err.println("Invalid or malformed configuration entry for " + prop.getName());
            prop.set(String.valueOf(ece.offsetX));
            xOffset = ece.offsetX;
        }

        prop = config.get(CatagoryName, "Y_Offset", String.valueOf(ece.offsetY));

        float yOffset;
        try {
            yOffset = Float.parseFloat(prop.getString());
        } catch (NumberFormatException e) {
            System.err.println("Invalid or malformed configuration entry for " + prop.getName());
            prop.set(String.valueOf(ece.offsetY));
            yOffset = ece.offsetY;
        }

        prop = config.get(CatagoryName, "Size_Modifier", String.valueOf(ece.sizeScaling));

        float SizeModifier;
        try {
            SizeModifier = Float.parseFloat(prop.getString());
        } catch (NumberFormatException e) {
            System.err.println("Invalid or malformed configuration entry for " + prop.getName());
            prop.set(String.valueOf(ece.sizeScaling));
            SizeModifier = ece.sizeScaling;
        }

        prop = config.get(CatagoryName, "Baby_Scale_Modifier", ece.babyScale);

        float babyScaleFactor;
        try {
            babyScaleFactor = Float.parseFloat(prop.getString());
        } catch (NumberFormatException e) {
            System.err.println("Invalid or malformed configuration entry for " + prop.getName());
            prop.set(String.valueOf(ece.babyScale));
            babyScaleFactor = ece.babyScale;
        }

        return new EntityConfigurationEntry(entry, scaleFactor, xOffset, yOffset, SizeModifier, babyScaleFactor, entityName, ignore, ece.maxHP, ece.eyeHeight);
    }

    public static Configuration getEntityConfiguration() {
        File file = new File(IndicatorsConfig.mainInstance().CONFIG_FILE.getParentFile(), "DIAdvancedCompatibility.cfg");

        try {
            file.createNewFile();
            return new Configuration(file);
        } catch (IOException e) {
            if (file.exists()) {
                if (!lastFailed) {
                    DamageIndicators.log.warn("Per mob configuration file was corrupt! Attempting to purge and recreate...");
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }

                    lastFailed = true;
                    return getEntityConfiguration();
                } else {
                    DamageIndicators.log.warn("Failed to recreate configuration! Configuration should be deleted when minecraft closes.");
                    throw new RuntimeException("DIAdvancedCompatibility was currupt and was unable to recreate the file.");
                }
            } else {
                throw new RuntimeException("Exception while creating " + file.getAbsolutePath(), e);
            }
        }
    }

    public static void saveEntityConfig(EntityConfigurationEntry ece) {
        String mod = "Vanilla";
        EntityRegistration er = EntityRegistry.instance().lookupModSpawn(ece.clazz, true);
        if (er != null) {
            mod = er.getContainer().getMetadata().name.replaceAll(Pattern.quote("."), "_");
        }

        String catagoryName = ece.clazz.getName();
        if (catagoryName.lastIndexOf(".") != -1) {
            catagoryName = catagoryName.substring(catagoryName.lastIndexOf("."));
            catagoryName = catagoryName.replaceAll(Pattern.quote("."), "");
        }

        catagoryName = mod + "." + catagoryName;
        catagoryName = catagoryName.replaceAll("[^a-zA-Z0-9\\s\\!\\:\\.\\&\\$]", "");
        Configuration config = getEntityConfiguration();
        config.addCustomCategoryComment(catagoryName, "These settings are to help other modders and users to make custom mobs fit correctly in the preview window.");
        config.get(catagoryName, "Scale_Factor", String.valueOf(ece.scale)).set(String.valueOf(ece.scale));
        if (ece.nameOverride != null && !"".equals(ece.nameOverride)) {
            config.get(catagoryName, "Name", ece.nameOverride).set(ece.nameOverride);
        } else {
            config.get(catagoryName, "Name", ece.nameOverride).set("");
        }

        config.get(catagoryName, "Ignore_This_Mob", ece.ignore).set(ece.ignore);
        config.get(catagoryName, "X_Offset", String.valueOf(ece.offsetX)).set(ece.offsetX);
        config.get(catagoryName, "Y_Offset", String.valueOf(ece.offsetY)).set(ece.offsetY);
        config.get(catagoryName, "Size_Modifier", String.valueOf(ece.sizeScaling)).set(ece.sizeScaling);
        config.save();
    }

    public void save() {
        saveEntityConfig(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityConfigurationEntry that = (EntityConfigurationEntry) o;
        return Float.compare(that.babyScale, babyScale) == 0 && Float.compare(that.sizeScaling, sizeScaling) == 0 && ignore == that.ignore && Float.compare(that.scale, scale) == 0 && Float.compare(that.offsetX, offsetX) == 0 && Float.compare(that.offsetY, offsetY) == 0 && Float.compare(that.eyeHeight, eyeHeight) == 0 && maxHP == that.maxHP && clazz.equals(that.clazz) && nameOverride.equals(that.nameOverride);
    }

    @Override
    public int hashCode() {
        return Objects.hash(babyScale, clazz, sizeScaling, ignore, nameOverride, scale, offsetX, offsetY, eyeHeight, maxHP);
    }

    @Override
    public String toString() {
        return "EntityConfigurationEntry{" +
            "babyScale=" + babyScale +
            ", clazz=" + clazz +
            ", sizeScaling=" + sizeScaling +
            ", ignore=" + ignore +
            ", nameOverride='" + nameOverride + '\'' +
            ", scale=" + scale +
            ", offsetX=" + offsetX +
            ", offsetY=" + offsetY +
            ", eyeHeight=" + eyeHeight +
            ", maxHP=" + maxHP +
            '}';
    }
}
