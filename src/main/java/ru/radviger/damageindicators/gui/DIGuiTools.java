package ru.radviger.damageindicators.gui;

import ru.radviger.damageindicators.textures.AbstractSkin;
import ru.radviger.damageindicators.textures.EnumSkinPart;
import ru.radviger.damageindicators.textures.JarSkinRegistration;
import ru.radviger.damageindicators.textures.Ordering;
import ru.radviger.damageindicators.configuration.IndicatorsConfig;
import ru.radviger.damageindicators.core.DIEventBus;
import ru.radviger.damageindicators.core.EntityConfigurationEntry;
import ru.radviger.damageindicators.core.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class DIGuiTools extends GuiIngame {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static DIGuiTools instance = new DIGuiTools(Minecraft.getMinecraft());
    public static DynamicTexture widgetsPNG;
    private static ScaledResolution scaledresolution;

    public DIGuiTools(Minecraft mc) {
        super(mc);
    }

    public static void addVertexWithUV(double x, double y, double z, double u, double v) {
        GL11.glTexCoord2d(u, v);
        GL11.glVertex3d(x, y, z);
    }

    public static void drawBackground(AbstractSkin skin, int locX, int locY) {
        int backgroundWidth = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH);
        int backgroundHeight = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT);
        int backgroundX = locX + (Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDX);
        int backgroundY = locY + (Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDY);
        skin.bindTexture(EnumSkinPart.BACKGROUNDID);
        GL11.glBegin(7);
        addVertexWithUV(backgroundX, backgroundY + backgroundHeight, 0.0D, 0.0D, 1.0D);
        addVertexWithUV(backgroundX + backgroundWidth, backgroundY + backgroundHeight, 0.0D, 1.0D, 1.0D);
        addVertexWithUV(backgroundX + backgroundWidth, backgroundY, 0.0D, 1.0D, 0.0D);
        addVertexWithUV(backgroundX, backgroundY, 0.0D, 0.0D, 0.0D);
        GL11.glEnd();
    }

    public static void drawFrame(AbstractSkin skin, int locX, int locY) {
        skin.bindTexture(EnumSkinPart.FRAMEID);
        int adjx = locX + (Integer) skin.getSkinValue(EnumSkinPart.CONFIGFRAMEX);
        int adjy = locY + (Integer) skin.getSkinValue(EnumSkinPart.CONFIGFRAMEY);
        int backgroundWidth = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGFRAMEWIDTH);
        int backgroundHeight = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGFRAMEHEIGHT);
        GL11.glBegin(7);
        addVertexWithUV(adjx, adjy + backgroundHeight, 0.0D, 0.0D, 1.0D);
        addVertexWithUV(adjx + backgroundWidth, adjy + backgroundHeight, 0.0D, 1.0D, 1.0D);
        addVertexWithUV(adjx + backgroundWidth, adjy, 0.0D, 1.0D, 0.0D);
        addVertexWithUV(adjx, adjy, 0.0D, 0.0D, 0.0D);
        GL11.glEnd();
    }

    public static void drawHealthBar(AbstractSkin skin, int locX, int locY, int health, int maxHealth, int entityID) {
        health = Math.min(health, maxHealth);
        int healthBarWidth = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARWIDTH);
        int healthBarHeight = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARHEIGHT);
        int healthBarX = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARX);
        int healthBarY = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARY);
        skin.bindTexture(EnumSkinPart.DAMAGEID);
        GL11.glBegin(7);
        addVertexWithUV(locX + healthBarX, locY + healthBarY + healthBarHeight, 0.0D, (float) health / (float) maxHealth, 1.0D);
        addVertexWithUV(locX + healthBarX + healthBarWidth, locY + healthBarY + healthBarHeight, 0.0D, 1.0D, 1.0D);
        addVertexWithUV(locX + healthBarX + healthBarWidth, locY + healthBarY, 0.0D, 1.0D, 0.0D);
        addVertexWithUV(locX + healthBarX, locY + healthBarY, 0.0D, (float) health / (float) maxHealth, 0.0D);
        GL11.glEnd();
        float w;
        float f;
        if (health < maxHealth) {
            f = (float) health / maxHealth * healthBarWidth;
            w = Math.max(f, 0.0F);
        } else {
            w = (float) healthBarWidth;
            EntityConfigurationEntry.maxHealthOverride.put(entityID, health);
        }

        f = (float) health / (float) maxHealth;
        skin.bindTexture(EnumSkinPart.HEALTHID);
        GL11.glBegin(7);
        addVertexWithUV(locX + healthBarX, locY + healthBarY + healthBarHeight, 0.0D, 0.0D, 1.0D);
        addVertexWithUV((float) (locX + healthBarX) + w, locY + healthBarY + healthBarHeight, 0.0D, f, 1.0D);
        addVertexWithUV((float) (locX + healthBarX) + w, locY + healthBarY, 0.0D, f, 0.0D);
        addVertexWithUV(locX + healthBarX, locY + healthBarY, 0.0D, 0.0D, 0.0D);
        GL11.glEnd();
    }

    public static void drawHealthText(AbstractSkin skin, int locX, int locY, int health, int maxHealth) {
        try {
            String Health = health + "/" + maxHealth;
            if (health > maxHealth) {
                Health = health + "/" + health;
            }

            int healthBarWidth = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARWIDTH);
            int healthBarHeight = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARHEIGHT);
            int healthBarX = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARX);
            int healthBarY = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARY);
            int packedRGB = Integer.parseInt("FFFFFF", 16);

            try {
                packedRGB = Integer.parseInt((String) skin.getSkinValue(EnumSkinPart.CONFIGTEXTEXTHEALTHCOLOR), 16);
            } catch (Exception ignored) {
            }

            if (mc.fontRenderer.FONT_HEIGHT + 2 > healthBarHeight) {
                GL11.glPushMatrix();

                try {
                    GL11.glTranslatef((float) (locX + healthBarX) + ((float) healthBarWidth - (float) mc.fontRenderer.getStringWidth(Health) * 0.7F) / 2.0F, (float) (locY + healthBarY + healthBarHeight) - (float) mc.fontRenderer.FONT_HEIGHT * 0.7F - 0.5F, 0.0F);
                    GL11.glScalef(0.7F, 0.7F, 1.0F);
                    mc.fontRenderer.drawStringWithShadow(Health, 0.0F, 0.0F, packedRGB);
                } catch (Throwable ignored) {
                }

                GL11.glPopMatrix();
            } else {
                try {
                    mc.fontRenderer.drawStringWithShadow(Health, (float) (locX + healthBarX + (healthBarWidth - mc.fontRenderer.getStringWidth(Health)) / 2), (float) (locY + healthBarY + (healthBarHeight - mc.fontRenderer.FONT_HEIGHT) / 2), packedRGB);
                } catch (Throwable ignored) {
                }
            }

            GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } catch (Throwable var15) {
            var15.printStackTrace();
        }

    }

    public static void drawMobPreview(EntityLivingBase el, AbstractSkin skin, int locX, int locY) {
        GL11.glPushAttrib(8192);
        int backgroundWidth = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH);
        int backgroundHeight = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT);
        int MobPreviewOffsetX = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWX);
        int MobPreviewOffsetY = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWY);
        GL11.glEnable(3089);

        try {
            int ex = MathHelper.floor((float) ((locX + MobPreviewOffsetX) * scaledresolution.getScaleFactor()));
            int boxWidth = MathHelper.floor((float) (backgroundWidth * scaledresolution.getScaleFactor()));
            int boxHeight = MathHelper.floor((float) (backgroundHeight * scaledresolution.getScaleFactor()));
            int boxLocY = MathHelper.floor((float) ((locY + MobPreviewOffsetY) * scaledresolution.getScaleFactor()));
            if (!(mc.currentScreen instanceof AdvancedGui)) {
                boxWidth = (int) ((float) boxWidth * IndicatorsConfig.mainInstance().guiScale);
                boxHeight = (int) ((float) boxHeight * IndicatorsConfig.mainInstance().guiScale);
            }

            GL11.glScissor(ex, Minecraft.getMinecraft().displayHeight - boxLocY - boxHeight, boxWidth, boxHeight);
            drawTargettedMobPreview(el, locX + MobPreviewOffsetX, locY + MobPreviewOffsetY);
        } catch (Throwable var12) {
            var12.printStackTrace();
        }

        GL11.glDisable(3089);
        GL11.glPopAttrib();
    }

    public static void drawMobTypes(EntityLivingBase el, AbstractSkin skin, int locX, int locY) {
        if (!DIEventBus.enemies.contains(el.getEntityId()) && !(el instanceof IMob)) {
            GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.6F);
            GL11.glColor4d(0.0D, 1.0D, 0.0D, 0.6000000238418579D);
        } else {
            GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.6F);
            GL11.glColor4d(1.0D, 0.0D, 0.0D, 0.6000000238418579D);
        }

        float step1 = 0.2F;
        float glTexX;
        if (!el.isNonBoss()) {
            glTexX = 4.0F * step1;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
            GL11.glColor4d(1.0D, 1.0D, 1.0D, 0.6000000238418579D);
        } else if (el.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD && !el.isEntityUndead()) {
            if (el.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD) {
                glTexX = 3.0F * step1;
            } else if (!(el instanceof EntityPlayer) && !(el instanceof EntityWitch) && !(el instanceof EntityVillager) && !(el instanceof EntityIronGolem)) {
                glTexX = 1.0F * step1;
            } else {
                glTexX = 2.0F * step1;
            }
        } else {
            glTexX = 0.0F * step1;
        }

        float adjX = (float) (locX + (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEX));
        float adjY = (float) (locY + (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEY));
        skin.bindTexture(EnumSkinPart.TYPEICONSID);
        GL11.glBegin(7);
        addVertexWithUV(adjX, adjY, 0.0D, glTexX, 0.0D);
        addVertexWithUV(adjX, adjY + (float) (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEHEIGHT), 0.0D, glTexX, 1.0D);
        addVertexWithUV(adjX + (float) (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEWIDTH), adjY + (float) (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEHEIGHT), 0.0D, glTexX + step1, 1.0D);
        addVertexWithUV(adjX + (float) (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEWIDTH), adjY, 0.0D, glTexX + step1, 0.0D);
        GL11.glEnd();
    }

    public static void drawNamePlate(AbstractSkin skin, int locX, int locY) {
        int NamePlateWidth = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEWIDTH);
        int NamePlateHeight = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEHEIGHT);
        int NamePlateX = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEX);
        int NamePlateY = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEY);
        skin.bindTexture(EnumSkinPart.NAMEPLATEID);
        GL11.glBegin(7);
        addVertexWithUV(locX + NamePlateX, locY + NamePlateY, 0.0D, 0.0D, 0.0D);
        addVertexWithUV(locX + NamePlateX, locY + NamePlateY + NamePlateHeight, 0.0D, 0.0D, 1.0D);
        addVertexWithUV(locX + NamePlateX + NamePlateWidth, locY + NamePlateY + NamePlateHeight, 0.0D, 1.0D, 1.0D);
        addVertexWithUV(locX + NamePlateX + NamePlateWidth, locY + NamePlateY, 0.0D, 1.0D, 0.0D);
        GL11.glEnd();
    }

    public static void drawNameText(AbstractSkin skin, String Name, int locX, int locY) {
        int NamePlateWidth = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEWIDTH);
        int NamePlateHeight = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEHEIGHT);
        int NamePlateX = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEX);
        int NamePlateY = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEY);
        int packedRGB = Integer.parseInt("FFFFFF", 16);

        try {
            packedRGB = Integer.parseInt((String) skin.getSkinValue(EnumSkinPart.CONFIGTEXTEXTNAMECOLOR), 16);
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        mc.fontRenderer.drawStringWithShadow(Name, (float) (locX + NamePlateX + (NamePlateWidth - mc.fontRenderer.getStringWidth(Name)) / 2), (float) (locY + NamePlateY + (NamePlateHeight - mc.fontRenderer.FONT_HEIGHT) / 2), packedRGB);
        GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void DrawPortraitSkinned(int locX, int locY, String Name, int health, int maxHealth, EntityLivingBase el) {
        scaledresolution = new ScaledResolution(mc);
        int depthzfun = GL11.glGetInteger(2932);
        boolean depthTest = GL11.glGetBoolean(2929);
        boolean blend = GL11.glGetBoolean(3042);

        try {
            AbstractSkin ex = AbstractSkin.getActiveSkin();
            Ordering[] ordering = (Ordering[]) ex.getSkinValue(EnumSkinPart.ORDERING);

            for (Ordering element : ordering) {
                GL11.glPushMatrix();

                try {
                    GL11.glDepthFunc(519);
                    if (element != Ordering.MOBPREVIEW) {
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    } else {
                        GL11.glDepthFunc(515);
                    }

                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
                    GL11.glEnable(3553);
                    GL11.glDisable(3042);
                    GL11.glDepthMask(true);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glEnable(3553);
                    GL11.glEnable(2929);
                    GL11.glDisable(2896);
                    GL11.glBlendFunc(770, 771);
                    GL11.glEnable(3042);
                    GL11.glEnable(3008);
                    boolean drawMobAndBackground = (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH) != 0;
                    switch (element) {
                        case BACKGROUND:
                            if (drawMobAndBackground) {
                                drawBackground(ex, locX, locY);
                            }
                            break;
                        case MOBPREVIEW:
                            if (drawMobAndBackground && el.getHealth() > 0.0F) {
                                drawMobPreview(el, ex, locX, locY);
                            }
                            break;
                        case NAMEPLATE:
                            drawNamePlate(ex, locX, locY);
                            break;
                        case HEALTHBAR:
                            drawHealthBar(ex, locX, locY, health, maxHealth, el != null ? el.getEntityId() : -1);
                            break;
                        case FRAME:
                            drawFrame(ex, locX, locY);
                            break;
                        case MOBTYPES:
                            drawMobTypes(el, ex, locX, locY);
                            break;
                        case POTIONS:
                            drawPotionBoxes(el);
                            break;
                        case HEALTHTEXT:
                            drawHealthText(ex, locX, locY, health, maxHealth);
                            break;
                        case NAMETEXT:
                            drawNameText(ex, Name, locX, locY);
                    }
                } catch (Throwable var16) {
                    var16.printStackTrace();
                }

                GL11.glPopMatrix();
            }
        } catch (Throwable var17) {
            var17.printStackTrace();
        }

        GL11.glDepthFunc(515);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthFunc(depthzfun);
        if (depthTest) {
            GL11.glEnable(2929);
        } else {
            GL11.glDisable(2929);
        }

        if (blend) {
            GL11.glEnable(3042);
        } else {
            GL11.glDisable(3042);
        }

        GL11.glClear(256);
    }

    public static void drawPotionBoxes(EntityLivingBase el) {
        AbstractSkin skin = JarSkinRegistration.getActiveSkin();
        int potionBoxSidesWidth = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXWIDTH);
        int PotionBoxHeight = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXHEIGHT);
        int PotionBoxOffsetX = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXX);
        int PotionBoxOffsetY = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXY);

        try {
            boolean ex = false;
            IndicatorsConfig config = IndicatorsConfig.mainInstance();
            if (config.enablePotionEffects && DIEventBus.potionEffects.get(el.getEntityId()) != null && !DIEventBus.potionEffects.get(el.getEntityId()).isEmpty()) {
                int position = 0;
                if (DIEventBus.potionEffects.containsKey(el.getEntityId())) {
                    for (PotionEffect adjy : DIEventBus.potionEffects.get(el.getEntityId())) {
                        int duration = adjy.getDuration();
                        if (duration > 0) {
                            Potion potion = adjy.getPotion();
                            if (potion.hasStatusIcon() && duration > 10) {
                                GL11.glPushMatrix();
                                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                                GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
                                int adjx1;
                                int adjy1;
                                if (!ex) {
                                    ex = true;
                                    adjx1 = config.locX + PotionBoxOffsetX;
                                    adjy1 = config.locY + PotionBoxOffsetY;
                                    skin.bindTexture(EnumSkinPart.LEFTPOTIONID);
                                    GL11.glBegin(7);
                                    addVertexWithUV(adjx1, adjy1, 0.0D, 0.0D, 0.0D);
                                    addVertexWithUV(adjx1, adjy1 + PotionBoxHeight, 0.0D, 0.0D, 1.0D);
                                    addVertexWithUV(adjx1 + potionBoxSidesWidth, adjy1 + PotionBoxHeight, 0.0D, 1.0D, 1.0D);
                                    addVertexWithUV(adjx1 + potionBoxSidesWidth, adjy1, 0.0D, 1.0D, 0.0D);
                                    GL11.glEnd();
                                }

                                adjx1 = config.locX + PotionBoxOffsetX + position * 20 + potionBoxSidesWidth;
                                adjy1 = config.locY + PotionBoxOffsetY;
                                skin.bindTexture(EnumSkinPart.CENTERPOTIONID);
                                GL11.glBegin(7);
                                addVertexWithUV(adjx1, adjy1, 0.0D, 0.0D, 0.0D);
                                addVertexWithUV(adjx1, adjy1 + PotionBoxHeight, 0.0D, 0.0D, 1.0D);
                                addVertexWithUV(adjx1 + 20, adjy1 + PotionBoxHeight, 0.0D, 1.0D, 1.0D);
                                addVertexWithUV(adjx1 + 20, adjy1, 0.0D, 1.0D, 0.0D);
                                GL11.glEnd();
                                int iconIndex = potion.getStatusIconIndex();
                                String formattedtime = Potion.getPotionDurationString(adjy, 1.0F);
                                int posx = config.locX + PotionBoxOffsetX + position * 20 + potionBoxSidesWidth + 2;
                                int posy = config.locY + PotionBoxOffsetY + 2;
                                int ioffx = (0 + iconIndex % 8) * 18;
                                int ioffy = (0 + iconIndex / 8) * 18 + 198;
                                int width = PotionBoxHeight - 4;

                                mc.getTextureManager().bindTexture(GuiInventory.INVENTORY_BACKGROUND);
                                instance.drawTexturedModalRect(posx, posy, ioffx, ioffy, width, width);

                                try {
                                    GL11.glTranslatef((float) (config.locX + PotionBoxOffsetX + position * 20 + potionBoxSidesWidth + 13 - mc.fontRenderer.getStringWidth(formattedtime) / 2), (float) (config.locY + PotionBoxOffsetY + PotionBoxHeight) - (float) mc.fontRenderer.FONT_HEIGHT * 0.815F, 0.1F);
                                    GL11.glScalef(0.815F, 0.815F, 0.815F);
                                    mc.fontRenderer.drawStringWithShadow(formattedtime, 0.0F, 0.0F, new Color(1.0F, 1.0F, 0.5F, 1.0F).getRGB());
                                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                                    GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
                                } catch (Throwable ignored) {
                                }

                                GL11.glPopMatrix();
                                ++position;
                            }
                        }
                    }

                    if (ex) {
                        int var28 = config.locX + PotionBoxOffsetX + position * 20 + potionBoxSidesWidth;
                        int duration = config.locY + PotionBoxOffsetY;
                        skin.bindTexture(EnumSkinPart.RIGHTPOTIONID);
                        GL11.glBegin(7);
                        addVertexWithUV(var28, duration, 0.0D, 0.0D, 0.0D);
                        addVertexWithUV(var28, duration + PotionBoxHeight, 0.0D, 0.0D, 1.0D);
                        addVertexWithUV(var28 + potionBoxSidesWidth, duration + PotionBoxHeight, 0.0D, 1.0D, 1.0D);
                        addVertexWithUV(var28 + potionBoxSidesWidth, duration, 0.0D, 1.0D, 0.0D);
                        GL11.glEnd();
                    }
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    public static void drawTargettedMobPreview(EntityLivingBase el, int locX, int locY) {
        IndicatorsConfig config = IndicatorsConfig.mainInstance();
        Class<? extends EntityLivingBase> cls = el.getClass();
        EntityConfigurationEntry configentry = Tools.getInstance().getEntityMap().get(cls);
        if (configentry == null) {
            Configuration configfile = EntityConfigurationEntry.getEntityConfiguration();
            configentry = EntityConfigurationEntry.generateDefaultConfiguration(configfile, cls);
            configentry.save();
            Tools.getInstance().getEntityMap().put(cls, configentry);
        }

        GL11.glPushMatrix();

        try {
            GL11.glTranslatef((float) (locX + 25) + configentry.XOffset, (float) (locY + 52) + configentry.YOffset, 1.0F);

            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            float ex = (3.0F - el.getEyeHeight()) * configentry.EntitySizeScaling;
            float finalScale = configentry.ScaleFactor + configentry.ScaleFactor * ex;
            if (el.isChild()) {
                finalScale = (configentry.ScaleFactor + configentry.ScaleFactor * ex) * configentry.BabyScaleFactor;
            }

            GL11.glScalef(finalScale * 0.85F, finalScale * 0.85F, 0.1F);
            int hurt = el.hurtTime;
            if (config.lockPosition) {
                float ex1 = el.prevRenderYawOffset;
                el.hurtTime = 0;
                el.prevRenderYawOffset = el.renderYawOffset - 360.0F;
                GL11.glRotatef(el.renderYawOffset - 360.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPushMatrix();

                try {
                    renderEntity(el);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                GL11.glPopMatrix();
                el.prevRenderYawOffset = ex1;
            } else {
                el.hurtTime = 0;
                GL11.glRotatef(180.0F - Minecraft.getMinecraft().player.rotationYaw, 0.0F, -1.0F, 0.0F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPushMatrix();

                try {
                    renderEntity(el);
                } catch (Throwable ignored) {
                }

                GL11.glPopMatrix();
            }
            el.hurtTime = hurt;
        } catch (Throwable ignored) {
        }

        GL11.glPopMatrix();
    }

    public static void renderEntity(EntityLivingBase el) {
        GL11.glDisable(3042);
        GL11.glEnable(2929);

        try {
            float backup = RenderLiving.NAME_TAG_RANGE;
            RenderLiving.NAME_TAG_RANGE = 0.0F;
            Render<EntityLivingBase> render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(el);
            if (render != null) {
                render.doRender(el, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
            }

            RenderLiving.NAME_TAG_RANGE = backup;
        } catch (Throwable ignored) {
        }

        GL11.glEnable(3042);
        GL11.glClear(256);
        GL11.glDisable(2929);
        GL11.glColor4f(255.0F, 255.0F, 255.0F, 255.0F);
        GL11.glBlendFunc(770, 771);
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
