package DamageIndicatorsMod.configuration;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DIConfig {
    private static DIConfig diConfig;
    public final File CONFIG_FILE;
    public float Size = 3.0F;
    public float Gravity = 0.8F;
    public float BounceStrength = 1.5F;
    public float ScaleFilter = 0.0F;
    public float transparency = 1.0F;
    public float guiScale = 0.76F;
    public int DIColor = 16755200;
    public int Lifespan = 12;
    public boolean CustomFont = true;
    public int packetrange = 30;
    public RenderingHints hints;
    public boolean alwaysRender = false;
    public boolean portraitEnabled = true;
    public boolean popOffsEnabled = true;
    public boolean enablePotionEffects = true;
    public int mouseoverRange = 30;
    public int healColor = 65280;
    public int portraitLifetime = 1600;
    public int locX = 15;
    public int locY = 15;
    public boolean lockPosition = true;
    public byte checkForUpdates = 2;
    public boolean DebugHidesWindow = true;
    public String selectedSkin = "/assets/defaultskins/default/";
    public boolean alternateRenderingMethod = false;
    public boolean highCompatibilityMod = false;
    public boolean supressBossUI = false;
    public boolean showCriticalStrikes = true;
    public boolean useDropShadows = true;
    private String formattedDIColor = "FFAA00";
    private String formattedHealColor = "00FF00";

    private DIConfig(File file, int check) {
        this.CONFIG_FILE = file;
        this.hints = this.populateHints();
        if (check == 1) {
            if (!file.delete()) {
                file.deleteOnExit();
            }
        } else {
            this.loadConfig();
        }

    }

    public static void loadConfig(File file) {
        if (file == null) {
            diConfig = new DIConfig(mainInstance().CONFIG_FILE, 0);
        } else {
            diConfig = new DIConfig(file, 0);
        }

    }

    public static DIConfig useNewConfig(DIConfig newConfig) {
        diConfig = newConfig;
        return mainInstance();
    }

    public static void overrideConfigAndSave(DIConfig newConfig) {
        try {
            Configuration config = new Configuration(newConfig.CONFIG_FILE);
            config.load();
            config.addCustomCategoryComment("PopOffs", "These Settings effect the digits that bounce off mobs when they take damage.");
            config.addCustomCategoryComment("Portrait", "These Settings effect the current health portrait window on the hud.");
            config.addCustomCategoryComment("PopOffs.Behavior", "This subcategory holds behavioral settings to do with PopOffs.");
            config.addCustomCategoryComment("Portrait.Behavior", "This subcategory holds behavioral settings to do with Portraits.");
            config.addCustomCategoryComment("PopOffs.Appearance", "This subcategory holds appearance settings to do with PopOffs.");
            config.addCustomCategoryComment("Portrait.Appearance", "This subcategory holds appearance settings to do with Portraits.");
            config.addCustomCategoryComment("Internal", "Don't Modify these settings unless explicitly told to or if you know what you are doing.\n This could result in the lose of settings or an unexpected crash.");
            Property prop = config.get("PopOffs.Behavior", "Lifespan", newConfig.Lifespan);
            prop.set(newConfig.Lifespan);
            prop = config.get("Portrait.Appearance", "Portrait_xPos", newConfig.locX);
            prop.set(newConfig.locX);
            prop = config.get("Portrait.Appearance", "Portrait_Skin", newConfig.selectedSkin);
            prop.set(newConfig.selectedSkin);
            prop = config.get("Portrait.Appearance", "Portrait_yPos", newConfig.locY);
            prop.set(newConfig.locY);
            prop = config.get("PopOffs.Appearance", "Always_Render", newConfig.alwaysRender);
            prop.set(newConfig.alwaysRender);
            prop = config.get("PopOffs.Behavior", "UpdateBehavior", newConfig.checkForUpdates);
            prop.set(newConfig.checkForUpdates);
            prop = config.get("PopOffs.Appearance", "Transparency", (double) newConfig.transparency);
            prop.set((double) newConfig.transparency);
            prop = config.get("Portrait.Appearance", "Lock_Mob_Position", newConfig.lockPosition);
            prop.set(newConfig.lockPosition);
            prop = config.get("Portrait.Appearance", "Gui_Scale", (double) newConfig.guiScale);
            prop.set((double) newConfig.guiScale);
            prop = config.get("PopOffs.Appearance", "Build_TexturePack_Font", newConfig.CustomFont);
            prop.set(newConfig.CustomFont);
            prop = config.get("PopOffs.Appearance", "Scale_Smoothing_Filter", (double) newConfig.ScaleFilter);
            prop.set((double) newConfig.ScaleFilter);
            newConfig.formattedDIColor = Integer.toHexString(newConfig.DIColor);
            prop = config.get("PopOffs.Appearance", "Color", newConfig.formattedDIColor);
            prop.set(newConfig.formattedDIColor);
            prop = config.get("PopOffs.Appearance", "Heal_Color", Integer.toHexString(newConfig.healColor & 16777215));
            prop.set(Integer.toHexString(newConfig.healColor));
            prop = config.get("Portrait.Appearance", "Range", newConfig.mouseoverRange);
            prop.set(newConfig.mouseoverRange);
            prop = config.get("Portrait.Behavior", "Enable", newConfig.portraitEnabled);
            prop.set(newConfig.portraitEnabled);
            prop = config.get("PopOffs.Behavior", "Gravity", (double) newConfig.Gravity);
            prop.set((double) newConfig.Gravity);
            prop = config.get("PopOffs.Behavior", "Enabled", newConfig.popOffsEnabled);
            prop.set(newConfig.popOffsEnabled);
            prop = config.get("PopOffs.Behavior", "Bounce_Strength", (double) newConfig.BounceStrength);
            prop.set((double) newConfig.BounceStrength);
            prop = config.get("PopOffs.Behavior", "Range", newConfig.packetrange);
            prop.set(newConfig.packetrange);
            prop = config.get("PopOffs.Behavior", "Size", (double) newConfig.Size);
            prop.set((double) newConfig.Size);
            prop = config.get("Portrait.Behavior", "Portrait_Lifetime", newConfig.portraitLifetime);
            prop.set(newConfig.portraitLifetime);
            prop = config.get("Portrait.Behavior", "Show Potion Effects", newConfig.enablePotionEffects);
            prop.set(newConfig.enablePotionEffects);
            prop = config.get("Portrait.Behavior", "DebugHidesWindow", newConfig.DebugHidesWindow);
            prop.set(newConfig.DebugHidesWindow);
            prop = config.get("Portrait.Behavior", "SupressBossHealth", newConfig.supressBossUI);
            prop.set(newConfig.supressBossUI);
            prop = config.get("Portrait.Behavior", "AlternateRenderMethod", newConfig.alternateRenderingMethod);
            prop.set(newConfig.alternateRenderingMethod);
            prop = config.get("Portrait.Behavior", "HighCompatibilityMode", newConfig.highCompatibilityMod);
            prop.set(newConfig.highCompatibilityMod);
            prop = config.get("PopOffs.Behavior", "ShowCriticalHits", newConfig.showCriticalStrikes);
            prop.set(newConfig.showCriticalStrikes);
            prop = config.get("PopOffs.Appearance", "useDropShadows", newConfig.useDropShadows);
            prop.set(newConfig.useDropShadows);
            prop = config.get("Internal", "version", "");
            prop.set("1");
            config.save();
        } catch (Throwable var3) {
            var3.printStackTrace();
        }

    }

    public static DIConfig mainInstance() {
        return diConfig;
    }

    private void loadConfig() {
        try {
            boolean flag = this.CONFIG_FILE.exists();
            Configuration config = new Configuration(this.CONFIG_FILE);
            config.load();
            if (flag && !config.get("Internal", "version", "0").getString().equals("1")) {
                overrideConfigAndSave(new DIConfig(this.CONFIG_FILE, 1));
                return;
            }

            config.addCustomCategoryComment("PopOffs", "These Settings effect the digits that bounce off mobs when they take damage.");
            config.addCustomCategoryComment("Portrait", "These Settings effect the current health portrait window on the hud.");
            config.addCustomCategoryComment("PopOffs.Behavior", "This subcategory holds behavioral settings to do with PopOffs.");
            config.addCustomCategoryComment("Portrait.Behavior", "This subcategory holds behavioral settings to do with Portraits.");
            config.addCustomCategoryComment("PopOffs.Appearance", "This subcategory holds appearance settings to do with PopOffs.");
            config.addCustomCategoryComment("Portrait.Appearance", "This subcategory holds appearance settings to do with Portraits.");
            config.addCustomCategoryComment("Internal", "Don't Modify these settings unless explicitly told to or if you know what you are doing.\n This could result in the lose of settings or an unexpected crash.");
            Property prop = config.get("PopOffs.Behavior", "Lifespan", this.Lifespan);
            this.Lifespan = prop.getInt(this.Lifespan);
            prop.set(this.Lifespan);
            prop = config.get("Portrait.Appearance", "Portrait_xPos", this.locX);
            this.locX = prop.getInt(this.locX);
            prop = config.get("Portrait.Appearance", "Portrait_yPos", this.locY);
            this.locY = prop.getInt(this.locY);
            prop = config.get("Portrait.Appearance", "Portrait_Skin", this.selectedSkin);
            this.selectedSkin = prop.getString();
            prop = config.get("PopOffs.Appearance", "Enable_Depth_Test", this.alwaysRender);
            this.alwaysRender = prop.getBoolean(this.alwaysRender);
            prop = config.get("PopOffs.Behavior", "UpdateBehavior", this.checkForUpdates);
            this.checkForUpdates = (byte) prop.getInt(this.checkForUpdates);
            prop = config.get("Portrait.Behavior", "SupressBossHealth", this.supressBossUI);
            this.supressBossUI = prop.getBoolean(this.supressBossUI);
            prop = config.get("PopOffs.Appearance", "Transparency", (double) this.transparency);
            this.transparency = (float) prop.getDouble((double) this.transparency);
            prop = config.get("Portrait.Appearance", "Gui_Scale", (double) this.guiScale);
            this.guiScale = (float) prop.getDouble((double) this.guiScale);
            prop.set((double) this.guiScale);
            prop = config.get("Portrait.Appearance", "Lock_Mob_Position", this.lockPosition);
            this.lockPosition = prop.getBoolean(this.lockPosition);
            prop.set(this.lockPosition);
            prop = config.get("PopOffs.Appearance", "Build_TexturePack_Font", this.CustomFont);
            this.CustomFont = prop.getBoolean(this.CustomFont);
            prop.set(this.CustomFont);
            prop = config.get("PopOffs.Appearance", "Scale_Smoothing_Filter", (double) this.ScaleFilter);
            this.ScaleFilter = (float) prop.getDouble((double) this.ScaleFilter);
            prop = config.get("PopOffs.Appearance", "Color", this.formattedDIColor);
            this.formattedDIColor = prop.getString();
            this.DIColor = (int) Long.parseLong(this.formattedDIColor, 16);
            prop = config.get("PopOffs.Appearance", "Heal_Color", this.formattedHealColor);
            this.formattedHealColor = prop.getString();
            this.healColor = (int) Long.parseLong(this.formattedHealColor, 16);
            prop = config.get("Portrait.Appearance", "Range", this.mouseoverRange);
            this.mouseoverRange = prop.getInt(this.mouseoverRange);
            if (this.mouseoverRange <= 0) {
                this.mouseoverRange = 20;
            }

            if (this.mouseoverRange > 200) {
                this.mouseoverRange = 200;
            }

            prop.set(this.mouseoverRange);
            prop = config.get("Portrait.Behavior", "Enable", this.portraitEnabled);
            this.portraitEnabled = prop.getBoolean(this.portraitEnabled);
            prop = config.get("PopOffs.Behavior", "Gravity", "1.600");

            try {
                this.Gravity = Float.valueOf(prop.getString());
            } catch (NumberFormatException var7) {
                this.Gravity = 0.8F;
                prop.set((double) this.Gravity);
            }

            prop = config.get("PopOffs.Behavior", "Enabled", this.popOffsEnabled);
            this.popOffsEnabled = prop.getBoolean(this.popOffsEnabled);
            prop = config.get("PopOffs.Behavior", "Bounce_Strength", (double) this.BounceStrength);

            try {
                this.BounceStrength = Float.valueOf(prop.getString());
            } catch (NumberFormatException var6) {
                this.BounceStrength = 1.5F;
                prop.set((double) this.BounceStrength);
            }

            prop = config.get("PopOffs.Behavior", "Range", this.packetrange);
            this.packetrange = prop.getInt(this.packetrange);
            prop = config.get("PopOffs.Behavior", "Size", (double) this.Size);

            try {
                this.Size = Float.valueOf(prop.getString());
            } catch (NumberFormatException var5) {
                this.Size = 3.0F;
                prop.set((double) this.Size);
            }

            prop = config.get("Portrait.Behavior", "Portrait_Lifetime", this.portraitLifetime);
            this.portraitLifetime = prop.getInt(this.portraitLifetime);
            prop = config.get("Portrait.Behavior", "Show Potion Effects", this.enablePotionEffects);
            this.enablePotionEffects = prop.getBoolean(this.enablePotionEffects);
            prop = config.get("Portrait.Behavior", "DebugHidesWindow", this.DebugHidesWindow);
            this.DebugHidesWindow = prop.getBoolean(this.DebugHidesWindow);
            prop = config.get("Portrait.Behavior", "AlternateRenderMethod", this.alternateRenderingMethod);
            this.alternateRenderingMethod = prop.getBoolean(this.alternateRenderingMethod);
            prop = config.get("Portrait.Behavior", "HighCompatibilityMode", this.highCompatibilityMod);
            this.highCompatibilityMod = prop.getBoolean(this.highCompatibilityMod);
            prop = config.get("PopOffs.Behavior", "ShowCriticalHits", this.showCriticalStrikes);
            this.showCriticalStrikes = prop.getBoolean(this.showCriticalStrikes);
            prop = config.get("PopOffs.Appearance", "useDropShadows", this.useDropShadows);
            this.useDropShadows = prop.getBoolean(this.useDropShadows);
            prop = config.get("Internal", "version", "");
            prop.set("1");
            config.save();
        } catch (Throwable var8) {
            var8.printStackTrace();
        }

    }

    private RenderingHints populateHints() {
        Map hintsMap = new HashMap();
        hintsMap.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hintsMap.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return new RenderingHints(hintsMap);
    }
}
