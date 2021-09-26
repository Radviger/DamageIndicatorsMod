package ru.radviger.damageindicators.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GuiCheckBox extends GuiButton {
    public boolean checked = false;

    public GuiCheckBox(int id, int x, int y, int w, int h, String Message) {
        super(id, x, y, w, h, Message);
        this.enabled = true;
        this.displayString = Message;
    }

    public static void addVertexWithUV(double x, double y, double z, double u, double v) {
        GL11.glTexCoord2d(u, v);
        GL11.glVertex3d(x, y, z);
    }

    public static void addVertex(double x, double y, double z) {
        GL11.glVertex3d(x, y, z);
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        int offset = mc.fontRenderer.getStringWidth(this.displayString) + 5;
        if (DIGuiTools.widgetsPNG == null) {
            try {
                BufferedImage ex = ImageIO.read(Minecraft.class.getResourceAsStream("/assets/minecraft/textures/gui/widgets.png"));
                DIGuiTools.widgetsPNG = new DynamicTexture(ex);
            } catch (Throwable var7) {
                var7.printStackTrace();
            }
        }

        if (this.enabled) {
            mc.fontRenderer.drawStringWithShadow(this.displayString, (float) this.x, (float) this.y, Color.white.getRGB());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            mc.fontRenderer.drawStringWithShadow(this.displayString, (float) this.x, (float) this.y, Color.GRAY.getRGB());
            GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);
        }

        DIGuiTools.widgetsPNG.updateDynamicTexture();
        if (!this.checked) {
            this.drawTexturedModalRect128(this.x + offset, this.y, 240, 0, 8, 8);
        } else {
            this.drawTexturedModalRect128(this.x + offset, this.y, 232, 0, 8, 8);
        }

    }

    public void drawTexturedModalRect128(int par1, int par2, int par3, int par4, int par5, int par6) {
        float var7 = 0.007813F;
        float var8 = 0.007813F;
        GL11.glBegin(7);
        addVertexWithUV((double) (par1 + 0), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + 0) * var7), (double) ((float) (par4 + par6) * var8));
        addVertexWithUV((double) (par1 + par5), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + par5) * var7), (double) ((float) (par4 + par6) * var8));
        addVertexWithUV((double) (par1 + par5), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + par5) * var7), (double) ((float) (par4 + 0) * var8));
        addVertexWithUV((double) (par1 + 0), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + 0) * var7), (double) ((float) (par4 + 0) * var8));
        GL11.glEnd();
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public void mouseReleased(int par1, int par2) {
        super.mouseReleased(par1, par2);
    }

    public boolean toggle() {
        this.checked = !this.checked;
        return this.checked;
    }
}
