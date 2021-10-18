package ru.radviger.damageindicators.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import ru.radviger.damageindicators.configuration.IndicatorsConfig;
import ru.radviger.damageindicators.textures.AbstractSkin;

public class SkinSlot {
    protected final int listWidth;
    protected final int listHeight;
    protected final int top;
    protected final int bottom;
    protected final int left;
    protected final int slotHeight;
    private final int right;
    public int selectedEntry;
    protected int mouseX;
    protected int mouseY;
    int boxLocX;
    int boxWidth;
    int boxHeight;
    int boxLocY;
    private SkinGui parentTexturePackGui;
    private int scrollUpActionId;
    private int scrollDownActionId;
    private float initialMouseClickY;
    private float scrollFactor;
    private float scrollDistance;
    private int selectedIndex;
    private long lastClickTime;
    private final boolean showSelectionBox;

    public SkinSlot(SkinGui par1GuiTexturePacks) {
        this(Minecraft.getMinecraft(), par1GuiTexturePacks.width - 128, par1GuiTexturePacks.height - 128, 64, par1GuiTexturePacks.height - 64, 64, 32);
        this.parentTexturePackGui = par1GuiTexturePacks;
        this.selectedEntry = 0;
        if (IndicatorsConfig.mainInstance().portraitEnabled) {
            this.selectedEntry = AbstractSkin.AVAILABLESKINS.indexOf(IndicatorsConfig.mainInstance().selectedSkin);
        }

    }

    public SkinSlot(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight) {
        this.selectedEntry = 0;
        this.initialMouseClickY = -2.0F;
        this.selectedIndex = -1;
        this.lastClickTime = 0L;
        this.showSelectionBox = true;
        this.listWidth = width;
        this.listHeight = height;
        this.top = top;
        this.bottom = bottom;
        this.slotHeight = entryHeight;
        this.left = left;
        this.right = width + this.left;
        ScaledResolution scaledresolution = new ScaledResolution(client);
        this.boxLocX = MathHelper.floor((float) (left * scaledresolution.getScaleFactor()));
        this.boxWidth = MathHelper.floor((float) (width * scaledresolution.getScaleFactor()));
        this.boxHeight = MathHelper.floor((float) (height * scaledresolution.getScaleFactor()));
        this.boxLocY = MathHelper.floor((float) (top * scaledresolution.getScaleFactor()));
        this.selectedEntry = AbstractSkin.AVAILABLESKINS.indexOf(IndicatorsConfig.mainInstance().selectedSkin);
    }

    protected int getSize() {
        return AbstractSkin.AVAILABLESKINS.size();
    }

    protected void elementClicked(int id, boolean doubleClick) {
        IndicatorsConfig.mainInstance().selectedSkin = AbstractSkin.AVAILABLESKINS.get(id);
        AbstractSkin.setSkin(IndicatorsConfig.mainInstance().selectedSkin);
        if (doubleClick) {
            Minecraft.getMinecraft().displayGuiScreen(this.parentTexturePackGui);
        }

        this.selectedEntry = id;
    }

    protected boolean isSelected(int index) {
        return this.selectedEntry == index;
    }

    protected void drawSlot(int par1, int par2, int par3, int par4) {
        String text1 = AbstractSkin.getSkinName(AbstractSkin.AVAILABLESKINS.get(par1));
        String text2 = AbstractSkin.getAuthor(AbstractSkin.AVAILABLESKINS.get(par1));
        int color = 3398963;
        int var10003 = this.left + 4;
        this.parentTexturePackGui.drawString(Minecraft.getMinecraft().fontRenderer, text1, var10003, par3 + 3, color);
        var10003 = this.left + 4;
        this.parentTexturePackGui.drawString(Minecraft.getMinecraft().fontRenderer, text2, var10003, par3 + 15, color);
    }

    protected int getContentHeight() {
        return this.getSize() * this.slotHeight;
    }

    private void applyScrollLimits() {
        int var1 = this.getContentHeight() - (this.bottom - this.top - 4);
        if (var1 < 0) {
            var1 /= 2;
        }

        if (this.scrollDistance < 0.0F) {
            this.scrollDistance = 0.0F;
        }

        if (this.scrollDistance > (float) var1) {
            this.scrollDistance = (float) var1;
        }

    }

    public void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == this.scrollUpActionId) {
                this.scrollDistance -= (float) (this.slotHeight * 2 / 3);
                this.initialMouseClickY = -2.0F;
                this.applyScrollLimits();
            } else if (button.id == this.scrollDownActionId) {
                this.scrollDistance += (float) (this.slotHeight * 2 / 3);
                this.initialMouseClickY = -2.0F;
                this.applyScrollLimits();
            }
        }

    }

    public void drawScreen(int mouseX, int mouseY, float par3) {
        try {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(this.boxLocX, this.boxLocY, this.boxWidth, this.boxHeight);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            int ex = this.getSize();
            int scrollBarXStart = this.left + this.listWidth - 6;
            int scrollBarXEnd = scrollBarXStart + 6;
            int boxLeft = this.left;
            int boxRight = scrollBarXStart - 1;
            if (Mouse.isButtonDown(0)) {
                if (this.initialMouseClickY != -1.0F) {
                    if (this.initialMouseClickY >= 0.0F) {
                        this.scrollDistance -= ((float) mouseY - this.initialMouseClickY) * this.scrollFactor;
                        this.initialMouseClickY = (float) mouseY;
                    }
                } else {
                    boolean var18 = true;
                    if (mouseY >= this.top && mouseY <= this.bottom) {
                        int var10 = mouseY - this.top + (int) this.scrollDistance - 4;
                        int id = var10 / this.slotHeight;
                        if (mouseX >= boxLeft && mouseX <= boxRight && id >= 0 && var10 >= 0 && id < ex) {
                            boolean doubleClick = id == this.selectedIndex && System.currentTimeMillis() - this.lastClickTime < 250L;
                            this.elementClicked(id, doubleClick);
                            this.selectedIndex = id;
                            this.lastClickTime = System.currentTimeMillis();
                        } else if (mouseX >= boxLeft && mouseX <= boxRight && var10 < 0) {
                            var18 = false;
                        }

                        if (mouseX >= scrollBarXStart && mouseX <= scrollBarXEnd) {
                            this.scrollFactor = -1.0F;
                            int var19 = this.getContentHeight() - (this.bottom - this.top - 4);
                            if (var19 < 1) {
                                var19 = 1;
                            }

                            int var13 = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getContentHeight());
                            if (var13 < 32) {
                                var13 = 32;
                            }

                            if (var13 > this.bottom - this.top - 8) {
                                var13 = this.bottom - this.top - 8;
                            }

                            this.scrollFactor /= (float) (this.bottom - this.top - var13) / (float) var19;
                        } else {
                            this.scrollFactor = 1.0F;
                        }

                        if (var18) {
                            this.initialMouseClickY = (float) mouseY;
                        } else {
                            this.initialMouseClickY = -2.0F;
                        }
                    } else {
                        this.initialMouseClickY = -2.0F;
                    }
                }
            } else {
                while (Mouse.next()) {
                    int var181 = Mouse.getEventDWheel();
                    if (var181 != 0) {
                        if (var181 > 0) {
                            var181 = -1;
                        } else if (var181 < 0) {
                            var181 = 1;
                        }

                        this.scrollDistance += (float) (var181 * this.slotHeight / 2);
                    }
                }

                this.initialMouseClickY = -1.0F;
            }

            this.applyScrollLimits();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            Tessellator t = Tessellator.getInstance();
            BufferBuilder b = t.getBuffer();

            b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

            b.pos(left, bottom, 0).tex(0, 1).color(0.3F, 0.3F, 0.3F, 0.5F).endVertex();
            b.pos(right, bottom, 0).tex(1, 1).color(0.3F, 0.3F, 0.3F, 0.5F).endVertex();;
            b.pos(right, top, 0).tex(1, 0).color(0.3F, 0.3F, 0.3F, 0.5F).endVertex();;
            b.pos(left, top, 0).tex(0, 0).color(0.3F, 0.3F, 0.3F, 0.5F).endVertex();;

            t.draw();

            GlStateManager.enableTexture2D();
            int d = top + 4 - (int) scrollDistance;

            for (int i = 0; i < ex; ++i) {
                int boxTop = d + i * slotHeight;
                int boxHeight = slotHeight - 4;
                if (boxTop <= bottom && boxTop + boxHeight >= top) {
                    if (showSelectionBox && isSelected(i)) {
                        GlStateManager.color(1F, 1F, 1F, 1F);
                        GlStateManager.enableBlend();
                        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                        GlStateManager.disableTexture2D();

                        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

                        b.pos(boxLeft, boxTop + boxHeight + 2, 0).tex(0, 1).color(0.5F, 0.5F, 0.5F, 1F).endVertex();
                        b.pos(boxRight, boxTop + boxHeight + 2, 0).tex(1, 1).color(0.5F, 0.5F, 0.5F, 1F).endVertex();;
                        b.pos(boxRight, boxTop - 2, 0).tex(1, 0).color(0.5F, 0.5F, 0.5F, 1F).endVertex();;
                        b.pos(boxLeft, boxTop - 2, 0).tex(0, 0).color(0.5F, 0.5F, 0.5F, 1F).endVertex();;

                        b.pos(boxLeft + 1, boxTop + boxHeight + 1, 0).tex(0, 1).color(0F, 0F, 0F, 1F).endVertex();
                        b.pos(boxRight - 1, boxTop + boxHeight + 1, 0).tex(1, 1).color(0F, 0F, 0F, 1F).endVertex();;
                        b.pos(boxRight - 1, boxTop - 1, 0).tex(1, 0).color(0F, 0F, 0F, 1F).endVertex();;
                        b.pos(boxLeft + 1, boxTop - 1, 0).tex(0, 0).color(0F, 0F, 0F, 1F).endVertex();;

                        t.draw();

                        GlStateManager.enableTexture2D();
                    }

                    this.drawSlot(i, boxRight, boxTop, this.getSize());
                }
            }

            GlStateManager.disableDepth();

            overlayBackground(0, top, 255, 255);
            overlayBackground(bottom, listHeight, 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GlStateManager.disableTexture2D();

            GlStateManager.color(0.0F, 0.0F, 0.0F);
            b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

            b.pos(left, top + 4, 0).tex(0, 1).color(0F, 0F, 0F, 1F).endVertex();
            b.pos(right, top + 4, 0).tex(1, 1).color(0F, 0F, 0F, 1F).endVertex();
            b.pos(right, top, 0).tex(1, 0).color(1F, 1F, 1F, 1F).endVertex();
            b.pos(left, top, 0).tex(0, 0).color(1F, 1F, 1F, 1F).endVertex();

            b.pos(left, bottom, 0).tex(0, 1).color(1F, 1F, 1F, 1F).endVertex();
            b.pos(right, bottom, 0).tex(1, 1).color(1F, 1F, 1F, 1F).endVertex();
            b.pos(right, bottom - 4, 0).tex(1, 0).color(0F, 0F, 0F, 1F).endVertex();
            b.pos(left, bottom - 4, 0).tex(0, 0).color(0F, 0F, 0F, 1F).endVertex();

            int dh = getContentHeight() - (bottom - top - 4);
            if (dh > 0) {
                int sdh = (bottom - top) * (bottom - top) / getContentHeight();
                if (sdh < 32) {
                    sdh = 32;
                }

                if (sdh > bottom - top - 8) {
                    sdh = bottom - top - 8;
                }

                int th = (int) scrollDistance * (bottom - top - sdh) / dh + this.top;
                if (th < top) {
                    th = top;
                }

                b.pos(scrollBarXStart, bottom, 0).tex(0, 1).color(0F, 0F, 0F, 1F).endVertex();
                b.pos(scrollBarXEnd, bottom, 0).tex(1, 1).color(0F, 0F, 0F, 1F).endVertex();
                b.pos(scrollBarXEnd, top, 0).tex(1, 0).color(0F, 0F, 0F, 1F).endVertex();
                b.pos(scrollBarXStart, top, 0).tex(0, 0).color(0F, 0F, 0F, 1F).endVertex();

                b.pos(scrollBarXStart, th + sdh, 0).tex(0, 1).color(0.5F, 0.5F, 0.5F, 1F).endVertex();
                b.pos(scrollBarXEnd, th + sdh, 0).tex(1, 1).color(0.5F, 0.5F, 0.5F, 1F).endVertex();
                b.pos(scrollBarXEnd, th, 0).tex(1, 0).color(0.5F, 0.5F, 0.5F, 1F).endVertex();
                b.pos(scrollBarXStart, th, 0).tex(0, 0).color(0.5F, 0.5F, 0.5F, 1F).endVertex();

                b.pos(scrollBarXStart, th + sdh - 1, 0).tex(0, 1).color(0.75F, 0.75F, 0.75F, 1F).endVertex();
                b.pos(scrollBarXEnd - 1, th + sdh - 1, 0).tex(1, 1).color(0.75F, 0.75F, 0.75F, 1F).endVertex();
                b.pos(scrollBarXEnd - 1, th, 0).tex(1, 0).color(0.75F, 0.75F, 0.75F, 1F).endVertex();
                b.pos(scrollBarXStart, th, 0).tex(0, 0).color(0.75F, 0.75F, 0.75F, 1F).endVertex();

            }

            t.draw();

            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GlStateManager.color(1F, 1F, 1F, 1F);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void overlayBackground(int par1, int par2, int par3, int par4) {
    }
}
