package DamageIndicatorsMod.gui;

import DITextures.AbstractSkin;
import DITextures.EnumSkinPart;
import DITextures.JarSkinRegistration;
import DamageIndicatorsMod.configuration.DIConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class RepositionGui extends GuiScreen {
    private final BufferedImage colorBar = new BufferedImage(8, 1536, 1);
    private final BufferedImage Gradient = new BufferedImage(256, 256, 1);
    public DIConfig diConfig;
    public boolean mouseDown = false;
    private float animationTick = 0.0F;
    private DynamicTexture colorBarTex;
    private DynamicTexture gradientTex;
    private GuiTextField gtf;
    private boolean setDamageColor = false;
    private boolean setHealColor = false;
    private int textWidth;

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) throws IOException {
        if (par1GuiButton instanceof GuiCheckBox) {
            switch (par1GuiButton.id) {
                case 0:
                    ((GuiCheckBox) par1GuiButton).setChecked(!((GuiCheckBox) par1GuiButton).isChecked());
                    this.diConfig.portraitEnabled = ((GuiCheckBox) par1GuiButton).checked;
                    break;
                case 1:
                    ((GuiCheckBox) par1GuiButton).setChecked(!((GuiCheckBox) par1GuiButton).isChecked());
                    this.diConfig.enablePotionEffects = ((GuiCheckBox) par1GuiButton).checked;
                    break;
                case 2:
                    ((GuiCheckBox) par1GuiButton).setChecked(!((GuiCheckBox) par1GuiButton).isChecked());
                    this.diConfig.popOffsEnabled = ((GuiCheckBox) par1GuiButton).checked;
                case 3:
                case 4:
                case 5:
                default:
                    break;
                case 6:
                    ((GuiCheckBox) par1GuiButton).setChecked(!((GuiCheckBox) par1GuiButton).isChecked());
                    this.diConfig.alternateRenderingMethod = ((GuiCheckBox) par1GuiButton).checked;
                    break;
                case 7:
                    ((GuiCheckBox) par1GuiButton).setChecked(!((GuiCheckBox) par1GuiButton).isChecked());
                    this.diConfig.highCompatibilityMod = ((GuiCheckBox) par1GuiButton).checked;
            }
        } else {
            switch (par1GuiButton.id) {
                case 3:
                    this.mc.displayGuiScreen(new SkinGui((GuiScreen) null, this.mc.gameSettings));
                    break;
                case 4:
                    this.mc.displayGuiScreen(new AdvancedGui());
                    break;
                case 5:
                    this.mc.player.closeScreen();
            }
        }

        super.actionPerformed(par1GuiButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    private void drawColorbar() {
        if (this.colorBarTex == null) {
            int locx = 0;

            for (int color = 0; color < 6; ++color) {
                for (int saturation = 0; saturation < 256; ++saturation) {
                    int finalColor;
                    switch (color) {
                        case 0:
                            finalColor = 16711680 | saturation;
                            break;
                        case 1:
                            finalColor = (255 - saturation) * 65536 | 0 | 255;
                            break;
                        case 2:
                            finalColor = 0 | saturation * 256 | 255;
                            break;
                        case 3:
                            finalColor = '\uff00' | 255 - saturation;
                            break;
                        case 4:
                            finalColor = saturation * 65536 | '\uff00' | 0;
                            break;
                        default:
                            finalColor = 16711680 | (255 - saturation) * 256 | 0;
                    }

                    int pos = locx++;

                    for (int i = 0; i < 8; ++i) {
                        this.colorBar.setRGB(i, pos, finalColor);
                    }
                }
            }

            this.colorBarTex = new DynamicTexture(this.colorBar);
        }

    }

    private void drawColorSelector() {
        drawRect(-2, -2, 72, 66, -2236963);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.gradientTex.updateDynamicTexture();
        GL11.glBegin(7);
        GL11.glTexCoord2d(0.0D, 0.0D);
        GL11.glVertex3d(0.0D, 0.0D, (double) this.zLevel);
        GL11.glTexCoord2d(0.0D, 1.0D);
        GL11.glVertex3d(0.0D, 64.0D, (double) this.zLevel);
        GL11.glTexCoord2d(1.0D, 1.0D);
        GL11.glVertex3d(64.0D, 64.0D, (double) this.zLevel);
        GL11.glTexCoord2d(1.0D, 0.0D);
        GL11.glVertex3d(64.0D, 0.0D, (double) this.zLevel);
        GL11.glEnd();
        this.colorBarTex.updateDynamicTexture();
        GL11.glBegin(7);
        GL11.glTexCoord2d(0.0D, 0.0D);
        GL11.glVertex3d(66.0D, 0.0D, (double) this.zLevel);
        GL11.glTexCoord2d(0.0D, 1.0D);
        GL11.glVertex3d(66.0D, 64.0D, (double) this.zLevel);
        GL11.glTexCoord2d(1.0D, 1.0D);
        GL11.glVertex3d(70.0D, 64.0D, (double) this.zLevel);
        GL11.glTexCoord2d(1.0D, 0.0D);
        GL11.glVertex3d(70.0D, 0.0D, (double) this.zLevel);
        GL11.glEnd();
    }

    private void drawGradient(int startRed, int startGreen, int startBlue) {
        if (startRed >= startBlue && startRed >= startGreen) {
            startRed = 255;
        } else if (startGreen >= startBlue && startGreen >= startRed) {
            startGreen = 255;
        } else {
            startBlue = 255;
        }

        if (startRed <= startBlue && startRed <= startGreen) {
            startRed = 0;
        } else if (startGreen <= startBlue && startGreen <= startRed) {
            startGreen = 0;
        } else {
            startBlue = 0;
        }

        for (int y = 0; y < 256; ++y) {
            for (int x = 0; x < 256; ++x) {
                this.Gradient.setRGB(x, y, -16777216 | (startRed + (255 - startRed) * y / 255) * x / 255 * 65536 | (startGreen + (255 - startGreen) * y / 255) * x / 255 * 256 | (startBlue + (255 - startBlue) * y / 255) * x / 255);
            }
        }

        this.gradientTex = new DynamicTexture(this.Gradient);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        GL11.glPushMatrix();
        ((GuiCheckBox) this.buttonList.get(6)).checked = this.diConfig.alternateRenderingMethod;
        ((GuiCheckBox) this.buttonList.get(7)).checked = this.diConfig.highCompatibilityMod;
        if (!this.diConfig.portraitEnabled) {
            ((GuiCheckBox) this.buttonList.get(1)).enabled = false;
        } else {
            ((GuiCheckBox) this.buttonList.get(1)).enabled = true;
            GL11.glPushMatrix();
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            GL11.glTranslatef((1.0F - DIConfig.mainInstance().guiScale) * (float) DIConfig.mainInstance().locX, (1.0F - DIConfig.mainInstance().guiScale) * (float) DIConfig.mainInstance().locY, 0.0F);
            GL11.glScalef(DIConfig.mainInstance().guiScale, DIConfig.mainInstance().guiScale, 1.0F);
            GL11.glPushAttrib(8192);
            float headPosX = (float) DIConfig.mainInstance().locX;
            headPosX += ((float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWX) + (float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH) / 2.0F) * DIConfig.mainInstance().guiScale;
            float headPosY = (float) DIConfig.mainInstance().locY;
            headPosY += ((float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWY) + (float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT) / 2.0F) * DIConfig.mainInstance().guiScale;
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
            DIGuiTools.DrawPortraitSkinned(this.diConfig.locX, this.diConfig.locY, this.mc.player.getName(), (int) Math.ceil((double) this.mc.player.getMaxHealth()), (int) Math.ceil((double) this.mc.player.getHealth()), this.mc.player);
            this.mc.player.renderYawOffset = f2;
            this.mc.player.rotationYaw = f3;
            this.mc.player.rotationPitch = f4;
            this.mc.player.prevRotationYawHead = f5;
            this.mc.player.rotationYawHead = f6;
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

        if (this.animationTick >= 1.0F) {
            this.animationTick = -5.0F;
        }

        this.animationTick += 0.01F;
        GL11.glTranslatef((float) (this.width / 2 + 30 - this.textWidth / 2), (float) (this.height / 2 - 30), 0.0F);
        drawRect(0, 0, 30, 20, 1996488704);
        drawRect(0, 2, 30, 0, -1441726384);
        drawRect(0, 22, 30, 20, -1441726384);
        drawRect(0, 0, 2, 22, -1441726384);
        drawRect(28, 0, 30, 22, -1441726384);
        GL11.glTranslatef(32.0F, 25.0F, 0.0F);
        drawRect(0, 0, 15, 13, -16777216 | this.diConfig.DIColor);
        if (this.setDamageColor) {
            drawRect(0, 2, 15, 0, -2236963);
            drawRect(0, 15, 15, 13, -2236963);
            drawRect(0, 0, 2, 15, -2236963);
            drawRect(13, 0, 15, 15, -2236963);
            GL11.glTranslatef(17.0F, -31.0F, 0.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawColorSelector();
            GL11.glTranslatef(-17.0F, 31.0F, 0.0F);
        }

        GL11.glTranslatef(0.0F, 20.0F, 0.0F);
        drawRect(0, 0, 15, 15, -16777216 | this.diConfig.healColor);
        if (this.setHealColor) {
            drawRect(0, 2, 15, 0, -2236963);
            drawRect(0, 15, 15, 13, -2236963);
            drawRect(0, 0, 2, 15, -2236963);
            drawRect(13, 0, 15, 15, -2236963);
            GL11.glTranslatef(17.0F, -51.0F, 0.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawColorSelector();
            GL11.glTranslatef(-17.0F, 51.0F, 0.0F);
        }

        GL11.glPopMatrix();
        boolean mouseOver = false;
        boolean mouseOver2 = false;
        if (par1 > this.diConfig.locX && par1 < this.diConfig.locX + (Integer) JarSkinRegistration.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGFRAMEWIDTH) && par2 > this.diConfig.locY && par2 < this.diConfig.locY + (Integer) JarSkinRegistration.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGFRAMEHEIGHT)) {
            mouseOver = true;
        }

        if (par1 > ((GuiCheckBox) this.buttonList.get(7)).x - 20 && par2 > ((GuiCheckBox) this.buttonList.get(7)).y - 20 && par1 <= ((GuiCheckBox) this.buttonList.get(7)).getWidth()) {
            mouseOver2 = true;
        }

        if (this.mouseDown) {
            mouseOver = false;
            this.diConfig.locX = par1;
            this.diConfig.locY = par2;
        }

        this.fontRenderer.drawStringWithShadow("Gui Scale:", (float) (this.width / 2 - (this.textWidth + this.gtf.getWidth() + 8) / 2), (float) (this.height / 2 - 24), 16777215);
        this.fontRenderer.drawStringWithShadow("Damage Color:", (float) (this.width / 2 - this.fontRenderer.getStringWidth("Damage Color:") / 2), (float) (this.height / 2 - 2), 16777215);
        this.fontRenderer.drawStringWithShadow("Heal Color:", (float) (this.width / 2 - this.fontRenderer.getStringWidth("Heal Color:") / 2), (float) (this.height / 2 + 18), 16777215);
        this.gtf.drawTextBox();
        this.fontRenderer.drawStringWithShadow("%", (float) (this.width / 2 + 42 - this.textWidth / 2 - 6 + this.gtf.getWidth()), (float) (this.height / 2 - 24), 16777215);
        ((GuiCheckBox) this.buttonList.get(0)).setChecked(this.diConfig.portraitEnabled);
        ((GuiCheckBox) this.buttonList.get(1)).setChecked(this.diConfig.enablePotionEffects);
        ((GuiCheckBox) this.buttonList.get(2)).setChecked(this.diConfig.popOffsEnabled);
        super.drawScreen(par1, par2, par3);
        GL11.glDepthFunc(519);
        if (mouseOver) {
            GL11.glPushMatrix();
            this.fontRenderer.getStringWidth("<Drag Me>");
            GL11.glTranslatef((float) par1, (float) par2, 0.0F);
            drawRect(0, 0, 60, 20, 1996488704);
            drawRect(0, 2, 60, 0, -1441726384);
            drawRect(0, 22, 60, 20, -1441726384);
            drawRect(0, 0, 2, 22, -1441726384);
            drawRect(58, 0, 60, 22, -1441726384);
            this.fontRenderer.drawString("<Drag Me>", 7, 7, -1429418804);
            GL11.glPopMatrix();
        }

        if (mouseOver2) {
            GL11.glPushMatrix();
            this.fontRenderer.getStringWidth("This option may decrease performance.");
            GL11.glTranslatef((float) par1, (float) (par2 - 22), 0.0F);
            drawRect(0, 0, 210, 20, 1996488704);
            drawRect(0, 2, 210, 0, -1441726384);
            drawRect(0, 22, 210, 20, -1441726384);
            drawRect(0, 0, 2, 22, -1441726384);
            drawRect(208, 0, 210, 22, -1441726384);
            this.fontRenderer.drawString("This option may decrease performance.", 7, 7, -1429418804);
            GL11.glPopMatrix();
        }

        GL11.glDepthFunc(515);
        GL11.glEnable(2929);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.diConfig = DIConfig.mainInstance();
        int enablePortrait = this.fontRenderer.getStringWidth("Enable Portrait") + 12;
        this.buttonList.add(0, new GuiCheckBox(0, this.width / 2 - enablePortrait / 2, this.height / 2 - 66, enablePortrait, 16, "Enable Portrait"));
        ((GuiCheckBox) this.buttonList.get(0)).setChecked(this.diConfig.portraitEnabled);
        int enablePotionEffects = this.fontRenderer.getStringWidth("Enable PotionEffects") + 12;
        this.buttonList.add(1, new GuiCheckBox(1, this.width / 2 - enablePotionEffects / 2, this.height / 2 - 52, enablePotionEffects, 16, "Enable PotionEffects"));
        ((GuiCheckBox) this.buttonList.get(1)).setChecked(true);
        int enablePopOffsWidth = this.fontRenderer.getStringWidth("Enable PopOffs") + 12;
        this.buttonList.add(2, new GuiCheckBox(2, this.width / 2 - enablePopOffsWidth / 2, this.height / 2 - 38, enablePopOffsWidth, 16, "Enable PopOffs"));
        ((GuiCheckBox) this.buttonList.get(2)).setChecked(true);
        int enableSkinWidth = this.fontRenderer.getStringWidth("Select Skin...") + 8;
        this.buttonList.add(3, new GuiButton(3, this.width / 2 - enableSkinWidth / 2, this.height / 2 + 34, enableSkinWidth, 20, "Select Skin..."));
        ((GuiButton) this.buttonList.get(3)).enabled = true;
        int AdvancedWidth = this.fontRenderer.getStringWidth("Advanced") + 8;
        this.buttonList.add(4, new GuiButton(4, this.width - AdvancedWidth - 4, this.height - 24, AdvancedWidth, 20, "Advanced"));
        this.buttonList.add(5, new GuiButton(5, this.width - 24, 4, 20, 20, "X"));
        enablePortrait = this.fontRenderer.getStringWidth("Alternate Render Method") + 12;
        this.buttonList.add(6, new GuiCheckBox(6, this.width / 2 - enablePortrait / 2, this.height / 2 - 80, enablePortrait, 16, "Alternate Render Method"));
        this.buttonList.add(7, new GuiCheckBox(7, 5, this.height - 12, this.fontRenderer.getStringWidth("High Compatibility Rendering") + 12, 16, "High Compatibility Rendering"));
        this.textWidth = this.fontRenderer.getStringWidth("Gui Scale") + 8;
        this.gtf = new GuiTextField(8, this.fontRenderer, this.width / 2 + 40 - this.textWidth / 2, this.height / 2 - 24, 30, 20);
        this.gtf.setText(String.valueOf(MathHelper.floor(this.diConfig.guiScale * 100.0F)));
        this.gtf.setMaxStringLength(3);
        this.gtf.setEnableBackgroundDrawing(false);
        this.gtf.setVisible(true);
        this.drawColorbar();
        this.drawGradient(255, 0, 255);
        GL11.glClear(256);
    }

    @Override
    protected void keyTyped(char par1, int par2) throws IOException {
        if (par2 != 14 && par2 != 211) {
            if (Character.isDigit(par1)) {
                this.gtf.textboxKeyTyped(par1, par2);
                int setVal = Integer.valueOf(this.gtf.getText());
                if (setVal > 200) {
                    int p = this.gtf.getCursorPosition();
                    this.gtf.setText("200");
                    this.gtf.setCursorPosition(p);
                }
            }
        } else {
            this.gtf.textboxKeyTyped(par1, par2);
            super.keyTyped(par1, par2);
            if (this.gtf.getText().length() == 0) {
                this.gtf.setText("0");
                this.gtf.setCursorPositionZero();
                this.gtf.setSelectionPos(1);
            }
        }

        this.diConfig.guiScale = Float.valueOf(this.gtf.getText()) / 100.0F;
        super.keyTyped(par1, par2);
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) throws IOException {
        if (par3 == 0) {
            try {
                if (par2 >= this.height / 2 - 36 && par2 <= this.height / 2 + 28) {
                    byte ex;
                    int y;
                    int pixelcolor;
                    int ex1;
                    if (this.setDamageColor) {
                        if (par1 >= this.width / 2 + 53 && par1 <= this.width / 2 + 116) {
                            ex1 = par1 - (this.width / 2 + 53);
                            y = par2 - (this.height / 2 - 36);
                            pixelcolor = this.Gradient.getRGB(ex1 * 4, y * 4);
                            this.diConfig.DIColor = pixelcolor;
                            this.setDamageColor = false;
                            return;
                        }

                        if (par1 >= this.width / 2 + 119 && par1 <= this.width / 2 + 123) {
                            ex = 1;
                            y = par2 - (this.height / 2 - 36);
                            pixelcolor = this.colorBar.getRGB(ex, y * (this.colorBar.getHeight() / 64));
                            this.drawGradient(pixelcolor >> 16 & 255, pixelcolor >> 8 & 255, pixelcolor & 255);
                        }
                    } else if (this.setHealColor) {
                        if (par1 >= this.width / 2 + 53 && par1 <= this.width / 2 + 116) {
                            ex1 = par1 - (this.width / 2 + 53);
                            y = par2 - (this.height / 2 - 36);
                            pixelcolor = this.Gradient.getRGB(ex1 * 4, y * 4);
                            this.diConfig.healColor = pixelcolor;
                            this.setHealColor = false;
                            return;
                        }

                        if (par1 >= this.width / 2 + 119 && par1 <= this.width / 2 + 123) {
                            ex = 1;
                            y = par2 - (this.height / 2 - 36);
                            pixelcolor = this.colorBar.getRGB(ex, y * (this.colorBar.getHeight() / 64));
                            this.drawGradient(pixelcolor >> 16 & 255, pixelcolor >> 8 & 255, pixelcolor & 255);
                            return;
                        }
                    }
                }

                if (par1 >= this.width / 2 + 30 - this.textWidth / 2 + 30 && par1 <= this.width / 2 + 30 - this.textWidth / 2 + 30 + 15) {
                    if (par2 >= this.height / 2 - 5 && par2 <= this.height / 2 + 10) {
                        this.setDamageColor = true;
                        this.setHealColor = false;
                        this.drawGradient(this.diConfig.DIColor >> 16 & 255, this.diConfig.DIColor >> 8 & 255, this.diConfig.DIColor & 255);
                    } else if (par2 >= this.height / 2 - 25 && par2 <= this.height / 2 + 30) {
                        this.setHealColor = true;
                        this.setDamageColor = false;
                        this.drawGradient(this.diConfig.healColor >> 16 & 255, this.diConfig.healColor >> 8 & 255, this.diConfig.healColor & 255);
                    }
                }
            } catch (Throwable var8) {
            }

            if (par1 >= this.diConfig.locX - 1 && par1 <= this.diConfig.locX + 137 && par2 >= this.diConfig.locY - 1 && par2 <= this.diConfig.locY + 52) {
                this.mouseDown = true;
            }
        }

        this.gtf.mouseClicked(par1, par2, par3);
        super.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.mouseDown = false;
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void onGuiClosed() {
        DIConfig.overrideConfigAndSave(this.diConfig);
        super.onGuiClosed();
    }

    @Override
    public void updateScreen() {
        this.gtf.updateCursorCounter();
        super.updateScreen();
    }
}
