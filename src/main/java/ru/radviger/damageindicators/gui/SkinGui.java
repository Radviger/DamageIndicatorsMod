package ru.radviger.damageindicators.gui;

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
        this.config = IndicatorsConfig.mainInstance();
        this.SkinSlot.drawScreen(par1, par2, par3);
        super.drawScreen(par1, par2, par3);
        GL11.glPushAttrib(278529);
        GL11.glPushMatrix();
        GL11.glTranslatef((1.0F - IndicatorsConfig.mainInstance().guiScale) * (float) IndicatorsConfig.mainInstance().locX, (1.0F - IndicatorsConfig.mainInstance().guiScale) * (float) IndicatorsConfig.mainInstance().locY, 0.0F);
        GL11.glScalef(IndicatorsConfig.mainInstance().guiScale, IndicatorsConfig.mainInstance().guiScale, 1.0F);
        float headPosX = (float) IndicatorsConfig.mainInstance().locX;
        headPosX += ((float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWX) + (float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH) / 2.0F) * IndicatorsConfig.mainInstance().guiScale;
        float headPosY = (float) IndicatorsConfig.mainInstance().locY;
        headPosY += ((float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWY) + (float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT) / 2.0F) * IndicatorsConfig.mainInstance().guiScale;
        headPosX = (float) par1 - headPosX;
        headPosY = (float) par2 - headPosY;
        float f2 = this.mc.player.renderYawOffset;
        float f3 = this.mc.player.rotationYaw;
        float f4 = this.mc.player.rotationPitch;
        float f5 = this.mc.player.prevRotationYawHead;
        float f6 = this.mc.player.rotationYawHead;
        this.mc.player.renderYawOffset = (float) Math.atan((double) (headPosX / 40.0F)) * 20.0F + 35.0F;
        this.mc.player.rotationYaw = (float) Math.atan((double) (headPosX / 40.0F)) * 40.0F;
        this.mc.player.rotationPitch = (float) Math.atan((double) (headPosY / 40.0F)) * 20.0F;
        this.mc.player.rotationYawHead = this.mc.player.rotationYaw;
        this.mc.player.prevRotationYawHead = this.mc.player.rotationYaw;
        Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
        GL11.glPushClientAttrib(1);
        DIGuiTools.DrawPortraitSkinned(this.config.locX, this.config.locY, this.mc.player.getName(), (int) Math.ceil((double) this.mc.player.getMaxHealth()), (int) Math.ceil((double) this.mc.player.getHealth()), this.mc.player);
        GL11.glPopClientAttrib();
        this.mc.player.renderYawOffset = f2;
        this.mc.player.rotationYaw = f3;
        this.mc.player.rotationPitch = f4;
        this.mc.player.prevRotationYawHead = f5;
        this.mc.player.rotationYawHead = f6;
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
