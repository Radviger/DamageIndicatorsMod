package ru.radviger.damageindicators.gui;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import ru.radviger.damageindicators.textures.AbstractSkin;
import ru.radviger.damageindicators.textures.EnumSkinPart;
import ru.radviger.damageindicators.configuration.IndicatorsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.opengl.GL11;

public class SkinGui extends GuiScreen {
    IndicatorsConfig config;
    private SkinSlot SkinSlot;

    public SkinGui(GuiScreen par1, GameSettings par2) {
    }

    @Override
    public void initGui() {
        this.SkinSlot = new SkinSlot(this);
        this.buttonList.add(new GuiButton(1, this.width - 24, 4, 20, 20, "X"));
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        Minecraft.getMinecraft().displayGuiScreen(new RepositionGui());
    }

    @Override
    public void onGuiClosed() {
        RepositionGui rp = new RepositionGui();
        rp.config = IndicatorsConfig.mainInstance();
        rp.onGuiClosed();
    }

    @Override
    public void drawDefaultBackground() {
    }

    protected void drawBackground() {
    }

    @Override
    public void drawBackground(int par1) {
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        IndicatorsConfig config = IndicatorsConfig.mainInstance();
        EntityPlayerSP player = this.mc.player;
        this.config = config;
        this.SkinSlot.drawScreen(par1, par2, par3);
        super.drawScreen(par1, par2, par3);
        GL11.glPushAttrib(GL11.GL_TEXTURE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        GlStateManager.pushMatrix();
        GlStateManager.translate((1.0F - config.guiScale) * (float) config.locX, (1.0F - config.guiScale) * (float) config.locY, 0.0F);
        GlStateManager.scale(config.guiScale, config.guiScale, 1.0F);
        float headPosX = (float) config.locX;
        headPosX += ((float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWX) + (float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH) / 2.0F) * config.guiScale;
        float headPosY = (float) config.locY;
        headPosY += ((float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWY) + (float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT) / 2.0F) * config.guiScale;
        headPosX = (float) par1 - headPosX;
        headPosY = (float) par2 - headPosY;
        float f2 = player.renderYawOffset;
        float f3 = player.rotationYaw;
        float f4 = player.rotationPitch;
        float f5 = player.prevRotationYawHead;
        float f6 = player.rotationYawHead;
        player.renderYawOffset = (float) Math.atan(headPosX / 40.0F) * 20.0F + 35.0F;
        player.rotationYaw = (float) Math.atan(headPosX / 40.0F) * 40.0F;
        player.rotationPitch = (float) Math.atan(headPosY / 40.0F) * 20.0F;
        player.rotationYawHead = player.rotationYaw;
        player.prevRotationYawHead = player.rotationYaw;
        Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
        GL11.glPushClientAttrib(GL11.GL_CLIENT_PIXEL_STORE_BIT);
        DIGuiTools.drawPortraitSkinned(this.config.locX, this.config.locY, player.getName(), (int) Math.ceil(player.getMaxHealth()), (int) Math.ceil(player.getHealth()), player);
        GL11.glPopClientAttrib();
        player.renderYawOffset = f2;
        player.rotationYaw = f3;
        player.rotationPitch = f4;
        player.prevRotationYawHead = f5;
        player.rotationYawHead = f6;
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }
}
