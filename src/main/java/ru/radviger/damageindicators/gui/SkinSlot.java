package ru.radviger.damageindicators.gui;

import ru.radviger.damageindicators.textures.AbstractSkin;
import ru.radviger.damageindicators.configuration.IndicatorsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class SkinSlot {
    protected final int listWidth;
    protected final int listHeight;
    protected final int top;
    protected final int bottom;
    protected final int left;
    protected final int slotHeight;
    private final Minecraft client;
    private final int right;
    public int selectedEntry;
    protected int mouseX;
    protected int mouseY;
    int boxLocX;
    int boxWidth;
    int boxHeight;
    int boxLocY;
    private SkinGui parentTexturePackGui;
    private Minecraft mc;
    private int scrollUpActionId;
    private int scrollDownActionId;
    private float initialMouseClickY;
    private float scrollFactor;
    private float scrollDistance;
    private int selectedIndex;
    private long lastClickTime;
    private boolean showSelectionBox;
    private boolean field_77243_s;
    private int field_77242_t;

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
        this.client = client;
        this.listWidth = width;
        this.listHeight = height;
        this.top = top;
        this.bottom = bottom;
        this.slotHeight = entryHeight;
        this.left = left;
        this.right = width + this.left;
        this.mc = client;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        this.boxLocX = MathHelper.floor((float) (left * scaledresolution.getScaleFactor()));
        this.boxWidth = MathHelper.floor((float) (width * scaledresolution.getScaleFactor()));
        this.boxHeight = MathHelper.floor((float) (height * scaledresolution.getScaleFactor()));
        this.boxLocY = MathHelper.floor((float) (top * scaledresolution.getScaleFactor()));
        this.selectedEntry = AbstractSkin.AVAILABLESKINS.indexOf(IndicatorsConfig.mainInstance().selectedSkin);
    }

    public static void addVertexWithUV(double x, double y, double z, double u, double v) {
        GL11.glTexCoord2d(u, v);
        GL11.glVertex3d(x, y, z);
    }

    public static void addVertex(double x, double y, double z) {
        GL11.glVertex3d(x, y, z);
    }

    protected int getSize() {
        return AbstractSkin.AVAILABLESKINS.size();
    }

    protected void elementClicked(int par1, boolean par2) {
        IndicatorsConfig.mainInstance().selectedSkin = AbstractSkin.AVAILABLESKINS.get(par1);
        AbstractSkin.setSkin(IndicatorsConfig.mainInstance().selectedSkin);
        if (par2) {
            Minecraft.getMinecraft().displayGuiScreen(this.parentTexturePackGui);
        }

        this.selectedEntry = par1;
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

    public void setShowSelectionBox(boolean par1) {
        this.showSelectionBox = par1;
    }

    protected void func_77223_a(boolean par1, int par2) {
        this.field_77243_s = par1;
        this.field_77242_t = par2;
        if (!par1) {
            this.field_77242_t = 0;
        }

    }

    protected int getContentHeight() {
        return this.getSize() * this.slotHeight + this.field_77242_t;
    }

    protected void func_77222_a(int par1, int par2, Tessellator par3Tessellator) {
    }

    protected void func_77224_a(int par1, int par2) {
    }

    protected void func_77215_b(int par1, int par2) {
    }

    public int func_77210_c(int par1, int par2) {
        int var3 = this.left + 1;
        int var4 = this.left + this.listWidth - 7;
        int var5 = par2 - this.top - this.field_77242_t + (int) this.scrollDistance - 4;
        int var6 = var5 / this.slotHeight;
        return par1 >= var3 && par1 <= var4 && var6 >= 0 && var5 >= 0 && var6 < this.getSize() ? var6 : -1;
    }

    public void registerScrollButtons(List par1List, int par2, int par3) {
        this.scrollUpActionId = par2;
        this.scrollDownActionId = par3;
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
            GL11.glEnable(3089);
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
                        int var10 = mouseY - this.top - this.field_77242_t + (int) this.scrollDistance - 4;
                        int var11 = var10 / this.slotHeight;
                        if (mouseX >= boxLeft && mouseX <= boxRight && var11 >= 0 && var10 >= 0 && var11 < ex) {
                            boolean var17 = var11 == this.selectedIndex && System.currentTimeMillis() - this.lastClickTime < 250L;
                            this.elementClicked(var11, var17);
                            this.selectedIndex = var11;
                            this.lastClickTime = System.currentTimeMillis();
                        } else if (mouseX >= boxLeft && mouseX <= boxRight && var10 < 0) {
                            this.func_77224_a(mouseX - boxLeft, mouseY - this.top + (int) this.scrollDistance - 4);
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
            GL11.glDisable(2896);
            GL11.glDisable(2912);
            GL11.glDisable(3553);
            GL11.glEnable(3042);
            GL11.glEnable(3008);
            GL11.glBlendFunc(770, 771);
            GL11.glBegin(7);
            GL11.glColor4f(0.3F, 0.3F, 0.3F, 0.5F);
            addVertexWithUV(this.left, this.bottom, 0.0D, 0.0D, 1.0D);
            addVertexWithUV(this.right, this.bottom, 0.0D, 1.0D, 1.0D);
            addVertexWithUV(this.right, this.top, 0.0D, 1.0D, 0.0D);
            addVertexWithUV(this.left, this.top, 0.0D, 0.0D, 0.0D);
            GL11.glEnd();
            GL11.glEnable(3553);
            int var10 = this.top + 4 - (int) this.scrollDistance;
            if (this.field_77243_s) {
            }

            for (int var11 = 0; var11 < ex; ++var11) {
                int var19 = var10 + var11 * this.slotHeight + this.field_77242_t;
                int var13 = this.slotHeight - 4;
                if (var19 <= this.bottom && var19 + var13 >= this.top) {
                    if (this.showSelectionBox && this.isSelected(var11)) {
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(770, 771);
                        GL11.glDisable(3553);
                        GL11.glBegin(7);
                        float j = 128.0F;
                        float k = 128.0F;
                        j = 128.0F;
                        j = (float) ((double) j / 255.0D);
                        k = (float) ((double) k / 255.0D);
                        j = (float) ((double) j / 255.0D);
                        GL11.glColor3f(j, k, j);
                        addVertexWithUV(boxLeft, var19 + var13 + 2, 0.0D, 0.0D, 1.0D);
                        addVertexWithUV(boxRight, var19 + var13 + 2, 0.0D, 1.0D, 1.0D);
                        addVertexWithUV(boxRight, var19 - 2, 0.0D, 1.0D, 0.0D);
                        addVertexWithUV(boxLeft, var19 - 2, 0.0D, 0.0D, 0.0D);
                        GL11.glColor3f(0.0F, 0.0F, 0.0F);
                        addVertexWithUV(boxLeft + 1, var19 + var13 + 1, 0.0D, 0.0D, 1.0D);
                        addVertexWithUV(boxRight - 1, var19 + var13 + 1, 0.0D, 1.0D, 1.0D);
                        addVertexWithUV(boxRight - 1, var19 - 1, 0.0D, 1.0D, 0.0D);
                        addVertexWithUV(boxLeft + 1, var19 - 1, 0.0D, 0.0D, 0.0D);
                        GL11.glEnd();
                        GL11.glEnable(3553);
                    }

                    this.drawSlot(var11, boxRight, var19, this.getSize());
                }
            }

            GL11.glDisable(2929);
            byte var20 = 4;
            this.overlayBackground(0, this.top, 255, 255);
            this.overlayBackground(this.bottom, this.listHeight, 255, 255);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3008);
            GL11.glShadeModel(7425);
            GL11.glDisable(3553);
            GL11.glBegin(7);
            GL11.glColor3f(0.0F, 0.0F, 0.0F);
            addVertexWithUV(this.left, this.top + var20, 0.0D, 0.0D, 1.0D);
            addVertexWithUV(this.right, this.top + var20, 0.0D, 1.0D, 1.0D);
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            addVertexWithUV(this.right, this.top, 0.0D, 1.0D, 0.0D);
            addVertexWithUV(this.left, this.top, 0.0D, 0.0D, 0.0D);
            GL11.glEnd();
            GL11.glBegin(7);
            addVertexWithUV(this.left, this.bottom, 0.0D, 0.0D, 1.0D);
            addVertexWithUV(this.right, this.bottom, 0.0D, 1.0D, 1.0D);
            GL11.glColor3f(0.0F, 0.0F, 0.0F);
            addVertexWithUV(this.right, this.bottom - var20, 0.0D, 1.0D, 0.0D);
            addVertexWithUV(this.left, this.bottom - var20, 0.0D, 0.0D, 0.0D);
            GL11.glEnd();
            int var19 = this.getContentHeight() - (this.bottom - this.top - 4);
            if (var19 > 0) {
                int var13 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
                if (var13 < 32) {
                    var13 = 32;
                }

                if (var13 > this.bottom - this.top - 8) {
                    var13 = this.bottom - this.top - 8;
                }

                int var14 = (int) this.scrollDistance * (this.bottom - this.top - var13) / var19 + this.top;
                if (var14 < this.top) {
                    var14 = this.top;
                }

                GL11.glBegin(7);
                GL11.glColor3f(0.0F, 0.0F, 0.0F);
                addVertexWithUV(scrollBarXStart, this.bottom, 0.0D, 0.0D, 1.0D);
                addVertexWithUV(scrollBarXEnd, this.bottom, 0.0D, 1.0D, 1.0D);
                addVertexWithUV(scrollBarXEnd, this.top, 0.0D, 1.0D, 0.0D);
                addVertexWithUV(scrollBarXStart, this.top, 0.0D, 0.0D, 0.0D);
                GL11.glEnd();
                GL11.glBegin(7);
                GL11.glColor3f(0.5F, 0.5F, 0.5F);
                addVertexWithUV(scrollBarXStart, (double) (var14 + var13), 0.0D, 0.0D, 1.0D);
                addVertexWithUV(scrollBarXEnd, (double) (var14 + var13), 0.0D, 1.0D, 1.0D);
                addVertexWithUV(scrollBarXEnd, var14, 0.0D, 1.0D, 0.0D);
                addVertexWithUV(scrollBarXStart, var14, 0.0D, 0.0D, 0.0D);
                GL11.glEnd();
                GL11.glBegin(7);
                GL11.glColor3f(0.75F, 0.75F, 0.75F);
                addVertexWithUV(scrollBarXStart, (double) (var14 + var13 - 1), 0.0D, 0.0D, 1.0D);
                addVertexWithUV(scrollBarXEnd - 1, (double) (var14 + var13 - 1), 0.0D, 1.0D, 1.0D);
                addVertexWithUV(scrollBarXEnd - 1, var14, 0.0D, 1.0D, 0.0D);
                addVertexWithUV(scrollBarXStart, var14, 0.0D, 0.0D, 0.0D);
                GL11.glEnd();
            }

            this.func_77215_b(mouseX, mouseY);
            GL11.glEnable(3553);
            GL11.glShadeModel(7424);
            GL11.glEnable(3008);
            GL11.glDisable(3042);
            GL11.glDisable(3089);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void overlayBackground(int par1, int par2, int par3, int par4) {
    }
}
