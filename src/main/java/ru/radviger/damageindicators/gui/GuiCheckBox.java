package ru.radviger.damageindicators.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        if (this.enabled) {
            mc.fontRenderer.drawStringWithShadow(this.displayString, (float) this.x, (float) this.y, Color.white.getRGB());
            GlStateManager.color(1F, 1F, 1F, 1F);
        } else {
            mc.fontRenderer.drawStringWithShadow(this.displayString, (float) this.x, (float) this.y, Color.GRAY.getRGB());
            GlStateManager.color(0.5F, 0.5F, 0.5F, 1F);
        }

        DIGuiTools.widgetsPNG.updateDynamicTexture();
        if (!this.checked) {
            this.drawTexturedModalRect128(this.x + offset, this.y, 240, 0, 8, 8);
        } else {
            this.drawTexturedModalRect128(this.x + offset, this.y, 232, 0, 8, 8);
        }

    }

    public void drawTexturedModalRect128(int x, int y, int textureX, int textureY, int w, int h) {
        float resolutionX = 1F / 128F;
        float resolutionY = 1F / 128F;

        Tessellator t = Tessellator.getInstance();
        BufferBuilder b = t.getBuffer();
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        b.pos(x + 0, y + h, zLevel).tex((float) (textureX + 0) * resolutionX, (float) (textureY + h) * resolutionY).endVertex();
        b.pos(x + w, y + h, zLevel).tex((float) (textureX + w) * resolutionX, (float) (textureY + h) * resolutionY).endVertex();
        b.pos(x + w, y + 0, zLevel).tex((float) (textureX + w) * resolutionX, (float) (textureY + 0) * resolutionY).endVertex();
        b.pos(x + 0, y + 0, zLevel).tex((float) (textureX + 0) * resolutionX, (float) (textureY + 0) * resolutionY).endVertex();
        t.draw();
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
