package ru.radviger.damageindicators.textures;

import ru.radviger.damageindicators.DamageIndicators;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;

public class JarSkinRegistration extends AbstractSkin {
    File file;

    public JarSkinRegistration(String skinName) {
        this.setInternalName(skinName);
        this.setSkinValue(EnumSkinPart.FRAMENAME, skinName + "DIFrameSkin.png");
        this.setSkinValue(EnumSkinPart.TYPEICONSNAME, skinName + "DITypeIcons.png");
        this.setSkinValue(EnumSkinPart.DAMAGENAME, skinName + "damage.png");
        this.setSkinValue(EnumSkinPart.HEALTHNAME, skinName + "health.png");
        this.setSkinValue(EnumSkinPart.BACKGROUNDNAME, skinName + "background.png");
        this.setSkinValue(EnumSkinPart.NAMEPLATENAME, skinName + "NamePlate.png");
        this.setSkinValue(EnumSkinPart.LEFTPOTIONNAME, skinName + "leftPotions.png");
        this.setSkinValue(EnumSkinPart.RIGHTPOTIONNAME, skinName + "rightPotions.png");
        this.setSkinValue(EnumSkinPart.CENTERPOTIONNAME, skinName + "centerPotions.png");

        try {
            this.file = File.createTempFile("skin", ".tmp");

            try {
                if (this.file.exists() && !this.file.delete()) {
                    this.file.deleteOnExit();
                    this.file = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".skin.tmp");
                }

                if (!this.file.createNewFile()) {
                }

                URL url = Minecraft.class.getResource(skinName + "skin.cfg");
                InputStream cfg = url.openStream();
                FileOutputStream fos = new FileOutputStream(this.file);

                for (int bite = cfg.read(); bite != -1; bite = cfg.read()) {
                    fos.write(bite);
                }

                fos.flush();
                fos.close();
                cfg.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception t) {
            t.printStackTrace();
        }

    }

    private static void checkEntry(JarEntry jEntry) {
        if (jEntry.getName().contains("defaultskins") && jEntry.getName().contains("skin.cfg")) {
            String thisSkin = jEntry.getName().substring(0, jEntry.getName().lastIndexOf("/"));
            if (!thisSkin.startsWith("/")) {
                thisSkin = "/" + thisSkin;
            }

            if (!thisSkin.endsWith("/")) {
                thisSkin = thisSkin + "/";
            }

            if (!AbstractSkin.AVAILABLESKINS.contains(thisSkin)) {
                AbstractSkin.AVAILABLESKINS.add(thisSkin);
            }
        }

    }

    private static void giveDebuggingInfo(Object test) {
    }

    public static void scanJarForSkins(Class clazz) {
        try {
            URL url = clazz.getResource("/assets");
            if (url != null) {
                Object test = url.openConnection();
                if (test instanceof JarURLConnection) {
                    JarURLConnection juc = (JarURLConnection) test;
                    juc.setUseCaches(false);
                    juc.setDoInput(true);
                    juc.setDoOutput(false);
                    juc.setAllowUserInteraction(true);
                    juc.connect();
                    Enumeration<JarEntry> jEnum = juc.getJarFile().entries();

                    while (jEnum.hasMoreElements()) {
                        try {
                            checkEntry(jEnum.nextElement());
                        } catch (Exception ignored) {
                        }
                    }
                } else if (!World.class.getName().endsWith("World")) {
                    giveDebuggingInfo(test);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!AbstractSkin.AVAILABLESKINS.contains("/assets/defaultskins/default/")) {
            AbstractSkin.AVAILABLESKINS.add("/assets/defaultskins/default/");
        }

        if (!AbstractSkin.AVAILABLESKINS.contains("/assets/defaultskins/wowlike/")) {
            AbstractSkin.AVAILABLESKINS.add("/assets/defaultskins/wowlike/");
        }

        if (!AbstractSkin.AVAILABLESKINS.contains("/assets/defaultskins/minimal/")) {
            AbstractSkin.AVAILABLESKINS.add("/assets/defaultskins/minimal/");
        }

    }

    @Override
    public void loadConfig() {
        this.loadConfig(new Configuration(this.file));
        this.file.deleteOnExit();
    }

    private DynamicTexture checkAndReload(EnumSkinPart enumID, EnumSkinPart enumName) {
        DynamicTexture ret = (DynamicTexture) this.getSkinValue(enumID);
        if (ret == null) {
            try {
                String tmp = (String) this.getSkinValue(enumName);
                ret = this.setupTexture(fixDim(ImageIO.read(DamageIndicators.class.getResourceAsStream(tmp))), enumID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    @Override
    public final void loadSkin() {
        this.setSkinValue(EnumSkinPart.FRAMEID, this.checkAndReload(EnumSkinPart.FRAMEID, EnumSkinPart.FRAMENAME));
        this.setSkinValue(EnumSkinPart.TYPEICONSID, this.checkAndReload(EnumSkinPart.TYPEICONSID, EnumSkinPart.TYPEICONSNAME));
        this.setSkinValue(EnumSkinPart.DAMAGEID, this.checkAndReload(EnumSkinPart.DAMAGEID, EnumSkinPart.DAMAGENAME));
        this.setSkinValue(EnumSkinPart.HEALTHID, this.checkAndReload(EnumSkinPart.HEALTHID, EnumSkinPart.HEALTHNAME));
        this.setSkinValue(EnumSkinPart.BACKGROUNDID, this.checkAndReload(EnumSkinPart.BACKGROUNDID, EnumSkinPart.BACKGROUNDNAME));
        this.setSkinValue(EnumSkinPart.NAMEPLATEID, this.checkAndReload(EnumSkinPart.NAMEPLATEID, EnumSkinPart.NAMEPLATENAME));
        this.setSkinValue(EnumSkinPart.LEFTPOTIONID, this.checkAndReload(EnumSkinPart.LEFTPOTIONID, EnumSkinPart.LEFTPOTIONNAME));
        this.setSkinValue(EnumSkinPart.RIGHTPOTIONID, this.checkAndReload(EnumSkinPart.RIGHTPOTIONID, EnumSkinPart.RIGHTPOTIONNAME));
        this.setSkinValue(EnumSkinPart.CENTERPOTIONID, this.checkAndReload(EnumSkinPart.CENTERPOTIONID, EnumSkinPart.CENTERPOTIONNAME));
        this.loadConfig();
    }
}
