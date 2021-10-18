package ru.radviger.damageindicators.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiToolTip extends Gui {
    private final AdvancedGui PARENT;
    public float alpha = 1.0F;
    public int borderColor = -12320649;
    public int borderWidth = 1;
    public boolean Centered = true;
    public boolean centerVertically = true;
    public int fontColor = -1;
    public int gradientEnd = -16777216;
    public int gradientStart = -16777216;
    public int HEIGHT = 128;
    public int iconIndex = 0;
    public int lineSpacing = 11;
    public String[] stringLines;
    public String TextureFile = null;
    public boolean useTexture = false;
    public int WIDTH = 48;
    public int xPos = 0;
    public int yPos = 0;
    FontRenderer cfr;

    public GuiToolTip(AdvancedGui parentGui, int width, int height) {
        this.PARENT = parentGui;
        this.WIDTH = width;
        this.HEIGHT = height;
        this.cfr = Minecraft.getMinecraft().fontRenderer;
    }

    public void drawCenteredStringNoShadow(FontRenderer par1FontRenderer, String par2Str, int par3, int par4, int par5) {
        this.cfr.setUnicodeFlag(true);
        if ((par5 >> 24 & 255) > 16) {
            this.cfr.drawString(par2Str, (float) (MathHelper.floor((float) par3 - (float) par1FontRenderer.getStringWidth(par2Str) / 2.0F * 0.75F) - 8), (float) par4, par5, false);
        }

        this.cfr.setUnicodeFlag(false);
    }

    public void drawStrings(FontRenderer par1FontRenderer) {
        this.drawStrings(par1FontRenderer, this.xPos, this.yPos);
    }

    public void drawStrings(FontRenderer par1FontRenderer, int x, int y) {
        this.drawStrings(par1FontRenderer, x, y, this.borderColor, this.gradientStart, this.gradientEnd, this.fontColor);
    }

    public void drawStrings(FontRenderer par1FontRenderer, int x, int y, int border, int gradStart, int gradEnd, int fontcolor) {
        this.drawStrings(par1FontRenderer, x, y, border, gradStart, gradEnd, fontcolor, this.stringLines);
    }

    public void drawStrings(FontRenderer par1FontRenderer, int x, int y, int border, int gradStart, int gradEnd, int font, boolean centered, String[] lines) {
        GlStateManager.depthFunc(GL11.GL_ALWAYS);
        float[] components = (new Color(this.gradientStart)).getComponents(new float[4]);
        GlStateManager.color(components[0], components[1], components[2], components[3]);
        int lineNumber;
        if (this.useTexture) {
            lineNumber = 0 + this.iconIndex % 8 * 18;
            int arr$ = 198 + this.iconIndex / 8 * 18;
            this.PARENT.drawTexturedModalRect(x, y, lineNumber, arr$, this.WIDTH, this.HEIGHT);
        } else {
            this.PARENT.drawGradientRect(x, y, x + this.WIDTH, y + this.HEIGHT, gradStart, gradEnd);
            drawRect(x, y, x + this.WIDTH, y + this.borderWidth, border);
            drawRect(x, y + this.HEIGHT - this.borderWidth, x + this.WIDTH, y + this.HEIGHT, border);
            drawRect(x, y, x + this.borderWidth, y + this.HEIGHT, border);
            drawRect(x + this.WIDTH - this.borderWidth, y, x + this.WIDTH, y + this.HEIGHT, border);
        }

        lineNumber = 0;
        String[] var19 = lines;
        int len$ = lines.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            String string = var19[i$];
            int linecount = lines.length;
            int verticalOffset = MathHelper.floor((float) this.HEIGHT / 2.0F - (float) linecount * ((float) par1FontRenderer.FONT_HEIGHT + 2.0F) * 1.0F / 2.0F);
            if (centered) {
                if (this.centerVertically) {
                    this.drawCenteredStringNoShadow(par1FontRenderer, string, x + this.WIDTH / 2, y + verticalOffset + lineNumber * (par1FontRenderer.FONT_HEIGHT + 2), font);
                } else {
                    this.drawCenteredStringNoShadow(par1FontRenderer, string, x + this.WIDTH / 2, y + 3 + lineNumber * this.lineSpacing, font);
                }
            } else {
                par1FontRenderer.drawString(string, x + 3, y + 3 + lineNumber * this.lineSpacing, font);
            }

            ++lineNumber;
        }

        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void drawStrings(FontRenderer par1FontRenderer, int x, int y, int border, int gradStart, int gradEnd, int font, String[] lines) {
        this.drawStrings(par1FontRenderer, x, y, border, gradStart, gradEnd, font, this.Centered, lines);
    }

    public void drawStrings(FontRenderer par1FontRenderer, int x, int y, String[] lines) {
        this.drawStrings(par1FontRenderer, x, y, this.borderColor, this.gradientStart, this.gradientEnd, this.fontColor, lines);
    }

    public void drawStrings(FontRenderer par1FontRenderer, String[] lines) {
        this.drawStrings(par1FontRenderer, this.xPos, this.yPos, lines);
    }

    public void drawStringsWithDifferentColors(FontRenderer par1FontRenderer, int x, int y, int border, int gradStart, int gradEnd, boolean centered, String colonDelimetedString, int[] colors) {
        this.drawStringsWithDifferentColors(par1FontRenderer, x, y, border, gradStart, gradEnd, centered, colonDelimetedString.split(":"), colors);
    }

    public void drawStringsWithDifferentColors(FontRenderer par1FontRenderer, int x, int y, int border, int gradStart, int gradEnd, boolean centered, String[] lines, int[] colors) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 1800.0F);
        if (lines.length != colors.length) {
            throw new IllegalArgumentException("The number of string lines must be equal to the number of colors passed in");
        } else {
            if (this.useTexture) {
                float[] color = (new Color(this.gradientStart)).getComponents(new float[4]);
                GlStateManager.color(color[0], color[1], color[2], color[3]);
                int u = 0 + this.iconIndex % 8 * 18;
                int v = 198 + this.iconIndex / 8 * 18;
                this.PARENT.drawTexturedModalRect(x, y, u, v, this.WIDTH, this.HEIGHT);
                GlStateManager.color(1F, 1F, 1F, 1F);
            } else {
                this.PARENT.drawGradientRect(x, y, this.WIDTH, this.HEIGHT, gradStart, gradEnd);
                drawRect(x, y, x + this.WIDTH, y + this.borderWidth, border);
                drawRect(x, y + this.HEIGHT - this.borderWidth, x + this.WIDTH, y + this.HEIGHT, border);
                drawRect(x, y, x + this.borderWidth, y + this.HEIGHT, border);
                drawRect(x + this.WIDTH - this.borderWidth, y, x + this.WIDTH, y + this.HEIGHT, border);
            }

            int i = 0;

            for (String string : lines) {
                int linecount = lines.length;
                int verticalSpacing = MathHelper.floor((float) (this.HEIGHT / (linecount + 1)));
                if (centered) {
                    if (this.centerVertically) {
                        this.drawCenteredString(par1FontRenderer, string, x + this.WIDTH / 2, y + verticalSpacing * i - par1FontRenderer.FONT_HEIGHT / 2, colors[i]);
                    } else {
                        this.drawCenteredString(par1FontRenderer, string, x + this.WIDTH / 2, y + 3 + i * this.lineSpacing, colors[i]);
                    }
                } else {
                    this.drawString(par1FontRenderer, string, x + 3, y + 3 + i * this.lineSpacing, colors[i]);
                }

                ++i;
            }

            GlStateManager.popMatrix();
        }
    }

    public void drawStringsWithDifferentColors(FontRenderer par1FontRenderer, int[] colors) {
        this.drawStringsWithDifferentColors(par1FontRenderer, this.xPos, this.yPos, this.borderColor, this.gradientStart, this.gradientEnd, this.Centered, this.stringLines, colors);
    }

    public boolean isCentered() {
        return this.Centered;
    }

    public void setCentered(boolean Centered) {
        this.Centered = Centered;
    }

    public boolean isCenterVertically() {
        return this.centerVertically;
    }

    public void setCenterVertically(boolean centerVertically) {
        this.centerVertically = centerVertically;
    }

    public boolean isUsingTexture() {
        return this.useTexture;
    }

    public void setBasicColors(int border, int gradStart, int gradEnd, int font) {
        this.borderColor = border;
        this.gradientStart = gradStart;
        this.gradientEnd = gradEnd;
        this.fontColor = font;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setDontUseTexture() {
        this.useTexture = false;
    }

    public void setGlobalAlpha(float trans) {
        this.alpha = trans > 0.0F ? (trans > 1.0F ? 1.0F : trans) : 0.0F;
        Color color = new Color(this.borderColor);
        float[] temp = color.getColorComponents(new float[3]);
        color = new Color(temp[0], temp[1], temp[2], this.alpha);
        this.borderColor = color.getRGB();
        color = new Color(this.gradientStart);
        temp = color.getColorComponents(new float[3]);
        color = new Color(temp[0], temp[1], temp[2], this.alpha);
        this.gradientStart = color.getRGB();
        color = new Color(this.gradientEnd);
        temp = color.getColorComponents(new float[3]);
        color = new Color(temp[0], temp[1], temp[2], this.alpha);
        this.gradientEnd = color.getRGB();
        color = new Color(this.fontColor);
        temp = color.getColorComponents(new float[3]);
        color = new Color(temp[0], temp[1], temp[2], this.alpha);
        this.fontColor = color.getRGB();
    }

    public void setGlobalAlpha(int trans) {
        trans = trans > 0 ? (trans > 255 ? 255 : trans) : 0;
        this.setGlobalAlpha((float) trans / 255.0F * 1.0F);
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public void setPos(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void setStringLines(String[] stringLines) {
        this.stringLines = stringLines;
    }

    public void setTextLines(String[] lines) {
        this.stringLines = lines;
    }

    public void setTextureFile(String TextureFile, int iconIndex) {
        this.TextureFile = TextureFile;
        this.iconIndex = iconIndex;
        this.useTexture = true;
    }

    public void setUpForDraw(int x, int y) {
        this.setUpForDraw(x, y, this.borderColor, this.gradientStart, this.gradientEnd, this.fontColor, this.Centered, this.centerVertically);
    }

    public void setUpForDraw(int x, int y, int borderColor, int baseColor, int gradEndColor, int defaultFontColor, boolean centeredHorizontally, boolean centeredVerTically) {
        this.setUpForDraw(x, y, borderColor, baseColor, gradEndColor, defaultFontColor, centeredHorizontally, centeredVerTically, this.WIDTH, this.HEIGHT);
    }

    public void setUpForDraw(int x, int y, int borderColor, int baseColor, int gradEndColor, int defaultFontColor, boolean centeredHorizontally, boolean centeredVerTically, int newWidth, int newHeight) {
        this.setUpForDraw(x, y, borderColor, baseColor, gradEndColor, defaultFontColor, centeredHorizontally, centeredVerTically, newWidth, newHeight, this.TextureFile, this.iconIndex);
    }

    public void setUpForDraw(int x, int y, int borderColor, int baseColor, int gradEndColor, int defaultFontColor, boolean centeredHorizontally, boolean centeredVertically, int newWidth, int newHeight, String texture, int iconIndex) {
        this.setUpForDraw(x, y, borderColor, baseColor, gradEndColor, defaultFontColor, centeredHorizontally, centeredVertically, newWidth, newHeight, texture, iconIndex, this.stringLines);
    }

    public void setUpForDraw(int x, int y, int borderColor, int baseColor, int gradEndColor, int defaultFontColor, boolean centeredHorizontally, boolean centeredVertically, int newWidth, int newHeight, String texture, int iconIndex, String[] lines) {
        this.xPos = x;
        this.yPos = y;
        this.borderColor = borderColor;
        this.gradientStart = baseColor;
        this.gradientEnd = gradEndColor;
        this.fontColor = defaultFontColor;
        this.Centered = centeredHorizontally;
        this.centerVertically = centeredVertically;
        this.WIDTH = newWidth;
        this.HEIGHT = newHeight;
        this.stringLines = lines;
        if (texture != null && !"".equals(texture)) {
            this.setTextureFile(texture, iconIndex);
        } else {
            this.setDontUseTexture();
        }

    }

    public void setUpForDraw(int x, int y, String[] lines) {
        this.stringLines = lines;
        this.setUpForDraw(x, y, this.borderColor, this.gradientStart, this.gradientEnd, this.fontColor, this.Centered, this.centerVertically);
    }
}
