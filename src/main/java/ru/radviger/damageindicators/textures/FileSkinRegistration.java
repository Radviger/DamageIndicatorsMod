package ru.radviger.damageindicators.textures;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraftforge.common.config.Configuration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileSkinRegistration extends AbstractSkin {
    private File file;

    public FileSkinRegistration(String path) {
        String pathName = cleanup(path.replace("file:", ""));
        this.setInternalName(pathName);
        this.setSkinValue(EnumSkinPart.FRAMENAME, pathName + "DIFrameSkin.png");
        this.setSkinValue(EnumSkinPart.TYPEICONSNAME, pathName + "DITypeIcons.png");
        this.setSkinValue(EnumSkinPart.DAMAGENAME, pathName + "damage.png");
        this.setSkinValue(EnumSkinPart.HEALTHNAME, pathName + "health.png");
        this.setSkinValue(EnumSkinPart.BACKGROUNDNAME, pathName + "background.png");
        this.setSkinValue(EnumSkinPart.NAMEPLATENAME, pathName + "NamePlate.png");
        this.setSkinValue(EnumSkinPart.LEFTPOTIONNAME, pathName + "leftPotions.png");
        this.setSkinValue(EnumSkinPart.RIGHTPOTIONNAME, pathName + "rightPotions.png");
        this.setSkinValue(EnumSkinPart.CENTERPOTIONNAME, pathName + "centerPotions.png");
        this.file = new File(pathName + "skin.cfg");
    }

    private static String cleanup(String string) {
        String ret = string;
        if (string.contains(File.separator + "." + File.separator)) {
            ret = string.replace(File.separator + "." + File.separator, File.separator);
        } else if (string.contains("\\.\\")) {
            ret = string.replaceAll("\\\\.\\\\", "\\\\");
        } else if (string.contains("/./")) {
            ret = string.replace("/./", "/");
        }

        return ret;
    }

    public static void scanFilesForSkins(File path) {
        try {
            File[] var1 = path.listFiles();

            for (File file : var1) {
                if (file.isDirectory()) {
                    File[] var5 = file.listFiles();

                    for (File files : var5) {
                        if (files.getAbsolutePath().endsWith("skin.cfg")) {
                            String thisSkin = files.getAbsolutePath().substring(0, files.getAbsolutePath().lastIndexOf(File.separator));
                            if (!thisSkin.endsWith(File.separator)) {
                                thisSkin = thisSkin + File.separator;
                            }

                            AVAILABLESKINS.add("file:" + thisSkin);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    @Override
    public void loadConfig() {
        Configuration config = new Configuration(this.file);
        this.loadConfig(config);
        config.save();
    }

    private InputStream getFileInputStream(String path) throws FileNotFoundException {
        return new FileInputStream(path);
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

    private DynamicTexture checkAndReload(EnumSkinPart enumID, EnumSkinPart enumName) {
        DynamicTexture ret = (DynamicTexture) getSkinValue(enumID);
        if (ret == null) {
            try {
                String tmp = (String) getSkinValue(enumName);
                return setupTexture(fixDim(ImageIO.read(getFileInputStream(tmp))), enumID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }
}
