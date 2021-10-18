package ru.radviger.damageindicators.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
import ru.radviger.damageindicators.configuration.IndicatorsConfig;
import ru.radviger.damageindicators.core.DIEventBus;
import ru.radviger.damageindicators.core.EntityConfigurationEntry;
import ru.radviger.damageindicators.core.Tools;
import ru.radviger.damageindicators.textures.AbstractSkin;
import ru.radviger.damageindicators.textures.EnumSkinPart;
import ru.radviger.damageindicators.textures.JarSkinRegistration;
import ru.radviger.damageindicators.textures.Ordering;

import java.awt.*;
import java.util.List;

public class DIGuiTools extends GuiIngame {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static DIGuiTools instance = new DIGuiTools(Minecraft.getMinecraft());
    public static DynamicTexture widgetsPNG;
    private static ScaledResolution scaledresolution;

    public DIGuiTools(Minecraft mc) {
        super(mc);
    }

    public static void drawRect(float x, float y, float w, float h) {
        drawRect(x, y, w, h, 0F, 1F);
    }

    public static void drawRect(float x, float y, float w, float h, float uStart, float uEnd) {
        Tessellator t = Tessellator.getInstance();
        BufferBuilder b = t.getBuffer();
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        b.pos(x, y + h, 0).tex(uStart, 1).endVertex();
        b.pos(x + w, y + h, 0).tex(uEnd, 1).endVertex();
        b.pos(x + w, y, 0).tex(uEnd, 0).endVertex();
        b.pos(x, y, 0).tex(uStart, 0).endVertex();
        t.draw();
    }

    public static void drawBackground(AbstractSkin skin, int locX, int locY) {
        int w = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH);
        int h = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT);
        int x = locX + (Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDX);
        int y = locY + (Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDY);
        skin.bindTexture(EnumSkinPart.BACKGROUNDID);
        drawRect(x, y, w, h);
    }

    public static void drawFrame(AbstractSkin skin, int locX, int locY) {
        int x = locX + (Integer) skin.getSkinValue(EnumSkinPart.CONFIGFRAMEX);
        int y = locY + (Integer) skin.getSkinValue(EnumSkinPart.CONFIGFRAMEY);
        int w = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGFRAMEWIDTH);
        int h = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGFRAMEHEIGHT);
        skin.bindTexture(EnumSkinPart.FRAMEID);
        drawRect(x, y, w, h);
    }

    public static void drawHealthBar(AbstractSkin skin, int locX, int locY, int health, int maxHealth, int entityID) {
        health = Math.min(health, maxHealth);
        int healthBarWidth = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARWIDTH);
        int healthBarHeight = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARHEIGHT);
        int healthBarX = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARX);
        int healthBarY = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARY);
        skin.bindTexture(EnumSkinPart.DAMAGEID);

        float hp = (float) health / (float) maxHealth;
        drawRect(locX + healthBarX, locY + healthBarY, healthBarWidth, healthBarHeight, 1F - hp, 1F);

        float w;
        if (health < maxHealth) {
            w = Math.max((float) health / maxHealth * healthBarWidth, 0.0F);
        } else {
            w = (float) healthBarWidth;
            EntityConfigurationEntry.maxHealthOverride.put(entityID, health);
        }

        skin.bindTexture(EnumSkinPart.HEALTHID);

        drawRect(locX + healthBarX, locY + healthBarY, w, healthBarHeight, 1F - hp, 1F);
    }

    public static void drawHealthText(AbstractSkin skin, int locX, int locY, int health, int maxHealth) {
        String Health = health + "/" + maxHealth;
        if (health > maxHealth) {
            Health = health + "/" + health;
        }

        int healthBarWidth = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARWIDTH);
        int healthBarHeight = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARHEIGHT);
        int healthBarX = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARX);
        int healthBarY = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARY);
        int packedRGB = 0xFFFFFF;

        try {
            packedRGB = Integer.parseInt((String) skin.getSkinValue(EnumSkinPart.CONFIGTEXTEXTHEALTHCOLOR), 16);
        } catch (NumberFormatException ignored) {}

        if (mc.fontRenderer.FONT_HEIGHT + 2 > healthBarHeight) {
            GlStateManager.pushMatrix();

            GlStateManager.translate((float) (locX + healthBarX) + ((float) healthBarWidth - (float) mc.fontRenderer.getStringWidth(Health) * 0.7F) / 2.0F, (float) (locY + healthBarY + healthBarHeight) - (float) mc.fontRenderer.FONT_HEIGHT * 0.7F - 0.5F, 0.0F);
            GlStateManager.scale(0.7F, 0.7F, 1F);
            mc.fontRenderer.drawStringWithShadow(Health, 0.0F, 0.0F, packedRGB);

            GlStateManager.popMatrix();
        } else {
            mc.fontRenderer.drawStringWithShadow(Health, (float) (locX + healthBarX + (healthBarWidth - mc.fontRenderer.getStringWidth(Health)) / 2), (float) (locY + healthBarY + (healthBarHeight - mc.fontRenderer.FONT_HEIGHT) / 2), packedRGB);
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    public static void drawMobPreview(EntityLivingBase el, AbstractSkin skin, int locX, int locY) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        int backgroundWidth = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH);
        int backgroundHeight = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT);
        int MobPreviewOffsetX = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWX);
        int MobPreviewOffsetY = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWY);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

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
            drawTargetedMobPreview(el, locX + MobPreviewOffsetX, locY + MobPreviewOffsetY);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopAttrib();
    }

    public static void drawMobTypes(EntityLivingBase el, AbstractSkin skin, int locX, int locY) {
        if (!DIEventBus.enemies.contains(el.getEntityId()) && !(el instanceof IMob)) {
            GlStateManager.color(0F, 1F, 0F, 0.6F);
        } else {
            GlStateManager.color(1F, 0F, 0F, 0.6F);
        }

        float step1 = 0.2F;
        float glTexX;
        if (el.isNonBoss()) {
            if (el.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD && !el.isEntityUndead()) {
                if (el.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD) {
                    glTexX = 3.0F * step1;
                } else if (el instanceof EntityPlayer || el instanceof EntityWitch || el instanceof EntityVillager || el instanceof EntityIronGolem) {
                    glTexX = 2.0F * step1;
                } else {
                    glTexX = 1.0F * step1;
                }
            } else {
                glTexX = 0.0F * step1;
            }
        } else {
            glTexX = 4.0F * step1;
            GlStateManager.color(1F, 1F, 1F, 0.6F);
        }

        float adjX = (float) (locX + (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEX));
        float adjY = (float) (locY + (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEY));
        skin.bindTexture(EnumSkinPart.TYPEICONSID);
        float w = (float) (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEWIDTH);
        float h = (float) (Integer) skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEHEIGHT);
        drawRect(adjX, adjY, w, h, glTexX, glTexX + step1);
    }

    public static void drawNamePlate(AbstractSkin skin, int locX, int locY) {
        int w = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEWIDTH);
        int h = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEHEIGHT);
        int x = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEX);
        int y = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEY);
        skin.bindTexture(EnumSkinPart.NAMEPLATEID);
        drawRect(locX + x, locY + y, w, h);
    }

    public static void drawNameText(AbstractSkin skin, String Name, int locX, int locY) {
        int NamePlateWidth = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEWIDTH);
        int NamePlateHeight = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEHEIGHT);
        int NamePlateX = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEX);
        int NamePlateY = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEY);
        int packedRGB = 0xFFFFFF;

        try {
            packedRGB = Integer.parseInt((String) skin.getSkinValue(EnumSkinPart.CONFIGTEXTEXTNAMECOLOR), 16);
        } catch (NumberFormatException ignored) {}

        mc.fontRenderer.drawStringWithShadow(Name, (float) (locX + NamePlateX + (NamePlateWidth - mc.fontRenderer.getStringWidth(Name)) / 2), (float) (locY + NamePlateY + (NamePlateHeight - mc.fontRenderer.FONT_HEIGHT) / 2), packedRGB);
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    public static void drawPortraitSkinned(int locX, int locY, String Name, int health, int maxHealth, EntityLivingBase el) {
        scaledresolution = new ScaledResolution(mc);
        int depthFun = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        boolean depthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
        boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);

        AbstractSkin ex = AbstractSkin.getActiveSkin();
        Ordering[] ordering = (Ordering[]) ex.getSkinValue(EnumSkinPart.ORDERING);

        for (Ordering element : ordering) {
            GlStateManager.pushMatrix();

            GlStateManager.depthFunc(GL11.GL_ALWAYS);

            if (element != Ordering.MOBPREVIEW) {
                GlStateManager.color(1F, 1F, 1F, 1F);
            } else {
                GlStateManager.depthFunc(GL11.GL_LEQUAL);
            }

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
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

            GlStateManager.popMatrix();
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.depthFunc(depthFun);
        if (depthTest) {
            GlStateManager.enableDepth();
        } else {
            GlStateManager.disableDepth();
        }

        if (blend) {
            GlStateManager.enableBlend();
        } else {
            GlStateManager.disableBlend();
        }

        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
    }

    public static void drawPotionBoxes(EntityLivingBase el) {
        AbstractSkin skin = JarSkinRegistration.getActiveSkin();
        int w = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXWIDTH);
        int h = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXHEIGHT);
        int offsetX = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXX);
        int offsetY = (Integer) skin.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXY);

        boolean ex = false;
        IndicatorsConfig config = IndicatorsConfig.mainInstance();
        List<PotionEffect> potionEffects = DIEventBus.potionEffects.get(el.getEntityId());
        if (config.enablePotionEffects && potionEffects != null && !potionEffects.isEmpty()) {
            int position = 0;
            for (PotionEffect e : potionEffects) {
                int duration = e.getDuration();
                if (duration > 0) {
                    Potion potion = e.getPotion();
                    if (potion.hasStatusIcon() && duration > 10) {
                        GlStateManager.pushMatrix();
                        GlStateManager.color(1F, 1F, 1F, 1F);

                        if (!ex) {
                            ex = true;
                            int x = config.locX + offsetX;
                            int y = config.locY + offsetY;
                            skin.bindTexture(EnumSkinPart.LEFTPOTIONID);
                            drawRect(x, y, w, h);
                        }

                        int x = config.locX + offsetX + position * 20 + w;
                        int y = config.locY + offsetY;
                        skin.bindTexture(EnumSkinPart.CENTERPOTIONID);
                        drawRect(x, y, 20, h);

                        int iconIndex = potion.getStatusIconIndex();
                        String formattedtime = Potion.getPotionDurationString(e, 1.0F);
                        int posx = config.locX + offsetX + position * 20 + w + 2;
                        int posy = config.locY + offsetY + 2;
                        int ioffx = (0 + iconIndex % 8) * 18;
                        int ioffy = (0 + iconIndex / 8) * 18 + 198;
                        int width = h - 4;

                        mc.getTextureManager().bindTexture(GuiInventory.INVENTORY_BACKGROUND);
                        instance.drawTexturedModalRect(posx, posy, ioffx, ioffy, width, width);

                        GlStateManager.translate((float) (config.locX + offsetX + position * 20 + w + 13 - mc.fontRenderer.getStringWidth(formattedtime) / 2), (float) (config.locY + offsetY + h) - (float) mc.fontRenderer.FONT_HEIGHT * 0.815F, 0.1F);
                        GlStateManager.scale(0.815F, 0.815F, 0.815F);
                        mc.fontRenderer.drawStringWithShadow(formattedtime, 0.0F, 0.0F, new Color(1.0F, 1.0F, 0.5F, 1.0F).getRGB());
                        GlStateManager.color(1F, 1F, 1F, 1F);

                        GlStateManager.popMatrix();
                        ++position;
                    }
                }
            }

            if (ex) {
                int x = config.locX + offsetX + position * 20 + w;
                int y = config.locY + offsetY;
                skin.bindTexture(EnumSkinPart.RIGHTPOTIONID);
                drawRect(x, y, w, h);
            }
        }
    }

    public static void drawTargetedMobPreview(EntityLivingBase el, int locX, int locY) {
        IndicatorsConfig config = IndicatorsConfig.mainInstance();
        Class<? extends EntityLivingBase> cls = el.getClass();
        EntityConfigurationEntry entry = Tools.getInstance().getEntityMap().get(cls);
        if (entry == null) {
            Configuration cfg = EntityConfigurationEntry.getEntityConfiguration();
            entry = EntityConfigurationEntry.generateDefaultConfiguration(cfg, cls);
            entry.save();
            Tools.getInstance().getEntityMap().put(cls, entry);
        }

        GlStateManager.pushMatrix();

        GlStateManager.translate((float) (locX + 25) + entry.offsetX, (float) (locY + 52) + entry.offsetY, 1.0F);

        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float ex = (3.0F - el.getEyeHeight()) * entry.sizeScaling;
        float finalScale = entry.scale + entry.scale * ex;
        if (el.isChild()) {
            finalScale = (entry.scale + entry.scale * ex) * entry.babyScale;
        }

        GlStateManager.scale(finalScale * 0.85F, finalScale * 0.85F, 0.1F);
        int hurt = el.hurtTime;
        if (config.lockPosition) {
            float ex1 = el.prevRenderYawOffset;
            el.hurtTime = 0;
            el.prevRenderYawOffset = el.renderYawOffset - 360.0F;
            GlStateManager.rotate(el.renderYawOffset - 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.pushMatrix();

            try {
                renderEntity(el);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            GlStateManager.popMatrix();
            el.prevRenderYawOffset = ex1;
        } else {
            el.hurtTime = 0;
            GlStateManager.rotate(180.0F - Minecraft.getMinecraft().player.rotationYaw, 0.0F, -1.0F, 0.0F);
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.pushMatrix();

            try {
                renderEntity(el);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            GlStateManager.popMatrix();
        }
        el.hurtTime = hurt;

        GlStateManager.popMatrix();
    }

    public static void renderEntity(EntityLivingBase el) {
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();

        try {
            float r1 = RenderLiving.NAME_TAG_RANGE;
            float r2 = RenderLiving.NAME_TAG_RANGE_SNEAK;
            RenderLiving.NAME_TAG_RANGE = 0.0F;
            RenderLiving.NAME_TAG_RANGE_SNEAK = 0.0F;
            Render<EntityLivingBase> render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(el);
            if (render != null) {
                render.doRender(el, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
            }

            RenderLiving.NAME_TAG_RANGE = r1;
            RenderLiving.NAME_TAG_RANGE_SNEAK = r2;
        } catch (Throwable t) {
            t.printStackTrace();
        }

        GlStateManager.disableBlend();
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.disableDepth();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
