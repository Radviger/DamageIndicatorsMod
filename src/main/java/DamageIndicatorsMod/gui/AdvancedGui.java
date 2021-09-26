package DamageIndicatorsMod.gui;

import DITextures.AbstractSkin;
import DITextures.EnumSkinPart;
import DamageIndicatorsMod.configuration.DIConfig;
import DamageIndicatorsMod.core.EntityConfigurationEntry;
import DamageIndicatorsMod.core.Tools;
import DamageIndicatorsMod.util.EntityConfigurationEntryComparator;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdvancedGui extends GuiScreen {
    public boolean backwards = false;
    private GuiEntityList guiEntityList;
    private List<GuiTextField> textboxes = new ArrayList<>();
    private boolean entrySelected = false;
    private int selectedEntry = 0;
    private List<Rectangle> controlLocations = new ArrayList<>();
    private List<String[]> controlTooltipText = new ArrayList<>();
    private GuiToolTip tooltip;
    private int tooltipWidth = 96;
    private int tooltipHeight = 64;
    private Rectangle LastHovered = null;
    private int timeHovered = 0;
    private GuiTextField search;
    private boolean popoffsetting;
    private boolean portraitsetting;
    private final EntityConfigurationEntryComparator comparator = new EntityConfigurationEntryComparator();
    private EntityLivingBase tempMob;
    private EntityConfigurationEntry ece;
    private long lasttime = 0L;

    @Override
    protected void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6) {
        super.drawGradientRect(par1, par2, par3, par4, par5, par6);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        DIConfig.mainInstance().popOffsEnabled = this.popoffsetting;
        DIConfig.mainInstance().portraitEnabled = this.portraitsetting;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void initGui() {
        this.popoffsetting = DIConfig.mainInstance().popOffsEnabled;
        this.portraitsetting = DIConfig.mainInstance().portraitEnabled;
        DIConfig.mainInstance().popOffsEnabled = false;
        DIConfig.mainInstance().portraitEnabled = false;
        GuiEntityList.entities = new ArrayList(Tools.getInstance().getEntityMap().values());
        Iterator it = GuiEntityList.entities.iterator();

        while (it.hasNext()) {
            EntityConfigurationEntry ece = (EntityConfigurationEntry) it.next();
            if (EntityPlayer.class.isAssignableFrom(ece.Clazz)) {
                it.remove();
            }
        }

        GuiEntityList.entities.sort(this.comparator);
        this.guiEntityList = new GuiEntityList(this.mc, 120, this.height, 16, this.height - 16, 10, 25, this);
        this.fontRenderer.drawStringWithShadow(GuiEntityList.entities.get(this.selectedEntry).Clazz.getName(), 225.0F, 160.0F, 10066431);
        this.tooltip = new GuiToolTip(this, this.tooltipWidth, this.tooltipHeight);
        this.tooltip.setCenterVertically(true);
        this.tooltip.setCentered(true);
        this.search = new GuiTextField(5, this.fontRenderer, 11, 5, 115, 10);
        this.search.setText("Search...");
        this.createTooltips();
        super.initGui();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        for (GuiTextField gtf : this.textboxes) {
            gtf.mouseClicked(mouseX, mouseY, button);
        }

        this.search.mouseClicked(mouseX, mouseY, button);
        if (this.search.isFocused()) {
            if ("Search...".equals(this.search.getText())) {
                this.search.setText("");
            }
        } else if ("".equals(this.search.getText())) {
            this.search.setText("Search...");
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void keyTyped(char symbol, int key) throws IOException {
        for (GuiTextField ec : this.textboxes) {
            ec.textboxKeyTyped(symbol, key);
        }

        if (this.search.isFocused()) {
            this.guiEntityList.visibleEntities.clear();
            this.search.textboxKeyTyped(symbol, key);
            if (!"".equals(this.search.getText())) {
                this.entrySelected = false;
                this.guiEntityList.selectedEntry = 0;

                for (EntityConfigurationEntry ec1 : GuiEntityList.entities) {
                    Map<Class<? extends Entity>, String> classToStringMapping = Tools.getEntityList();
                    if (ec1.Clazz.getName().toLowerCase().contains(this.search.getText().toLowerCase())) {
                        this.guiEntityList.visibleEntities.add(ec1);
                    } else if (classToStringMapping.containsKey(ec1.Clazz)) {
                        String temp = classToStringMapping.get(ec1.Clazz).toLowerCase();
                        if (temp.contains(this.search.getText().toLowerCase())) {
                            this.guiEntityList.visibleEntities.add(ec1);
                        }
                    } else if (ec1.NameOverride.toLowerCase().contains(this.search.getText().toLowerCase())) {
                        this.guiEntityList.visibleEntities.add(ec1);
                    }
                }
            } else {
                this.guiEntityList.visibleEntities.addAll(GuiEntityList.entities);
            }
        }

        super.keyTyped(symbol, key);
    }

    public void createTooltips() {
        this.controlLocations.clear();
        this.controlTooltipText.clear();
        this.controlLocations.add(new Rectangle(220 - this.fontRenderer.getStringWidth("Scale Factor"), 32, this.fontRenderer.getStringWidth("Scale Factor"), this.fontRenderer.FONT_HEIGHT));
        this.controlTooltipText.add(new String[]{"How big mob looks in portrait"});
        this.controlLocations.add(new Rectangle(220 - this.fontRenderer.getStringWidth("X Offset"), 48, this.fontRenderer.getStringWidth("X Offset"), this.fontRenderer.FONT_HEIGHT));
        this.controlTooltipText.add(new String[]{"Nudge this many pixels right."});
        this.controlLocations.add(new Rectangle(220 - this.fontRenderer.getStringWidth("Y Offset"), 64, this.fontRenderer.getStringWidth("Y Offset"), this.fontRenderer.FONT_HEIGHT));
        this.controlTooltipText.add(new String[]{"Nudge this many pixels down."});
        this.controlLocations.add(new Rectangle(220 - this.fontRenderer.getStringWidth("Size Scaling"), 80, this.fontRenderer.getStringWidth("Size Scaling"), this.fontRenderer.FONT_HEIGHT));
        this.controlTooltipText.add(new String[]{"For Slime Type Mobs. How much to scale based on size."});
        this.controlLocations.add(new Rectangle(220 - this.fontRenderer.getStringWidth("Baby Scaling"), 112, this.fontRenderer.getStringWidth("Baby Scaling"), this.fontRenderer.FONT_HEIGHT));
        this.controlTooltipText.add(new String[]{"To make babies bigger in portrait."});
        this.controlLocations.add(new Rectangle(220 - this.fontRenderer.getStringWidth("Name Override"), 144, this.fontRenderer.getStringWidth("Name Override"), this.fontRenderer.FONT_HEIGHT));
        this.controlTooltipText.add(new String[]{"Use this name instead."});
        this.controlLocations.add(new Rectangle(225, 142, 120, 10));
        this.controlTooltipText.add(new String[]{"Replace name with this text."});
        this.controlLocations.add(new Rectangle(220 - this.fontRenderer.getStringWidth("Full Class Name"), 160, this.fontRenderer.getStringWidth("Full Class Name"), this.fontRenderer.FONT_HEIGHT));
        this.controlTooltipText.add(new String[]{"Full Class Path For Debugging."});
        this.controlLocations.add(new Rectangle(225, 160, this.fontRenderer.getStringWidth(this.guiEntityList.visibleEntities.get(this.selectedEntry).Clazz.getName()), this.fontRenderer.FONT_HEIGHT));
        this.controlTooltipText.add(new String[]{"Full Class Path For Debugging."});
        this.controlLocations.add(new Rectangle(220 - (this.fontRenderer.getStringWidth("Prefix Babies") + 12), 96, this.fontRenderer.getStringWidth("Prefix Babies") + 12, 12));
        this.controlTooltipText.add(new String[]{"Prefix names with baby if a baby."});
        this.controlLocations.add(new Rectangle(300, 186, 80, 20));
        this.controlTooltipText.add(new String[]{"Save all changes."});
        this.controlLocations.add(new Rectangle(225, 30, 120, 10));
        this.controlTooltipText.add(new String[]{"How big mob looks in portrait"});
        this.controlLocations.add(new Rectangle(225, 46, 120, 10));
        this.controlTooltipText.add(new String[]{"Nudge this many pixels right."});
        this.controlLocations.add(new Rectangle(225, 62, 120, 10));
        this.controlTooltipText.add(new String[]{"Nudge this many pixels down."});
        this.controlLocations.add(new Rectangle(225, 78, 120, 10));
        this.controlTooltipText.add(new String[]{"For Slime Type Mobs. How much to scale based on size."});
        this.controlLocations.add(new Rectangle(225, 110, 120, 10));
        this.controlTooltipText.add(new String[]{"To make babies bigger in portrait."});
        this.controlLocations.add(new Rectangle(32, 32, 120, this.height - 64));
        this.controlTooltipText.add(new String[]{"Detected Entities. Click to configure."});

        for (int i = 0; i < this.controlLocations.size(); ++i) {
            this.controlTooltipText.set(i, this.fontRenderer.listFormattedStringToWidth(this.controlTooltipText.get(i)[0], this.tooltipWidth - 2).toArray(new String[0]));
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float elapsedTime) {
        if (this.lasttime == 0L) {
            this.lasttime = System.nanoTime();
        }

        double diff = (double) (System.nanoTime() - this.lasttime) / 1000000.0D;
        this.lasttime = System.nanoTime();
        this.drawBackground(2);
        this.guiEntityList.drawScreen(mouseX, mouseY, elapsedTime);
        boolean found = false;
        this.search.drawTextBox();
        if (this.entrySelected) {
            if (this.ece == null || this.ece != this.guiEntityList.visibleEntities.get(this.selectedEntry)) {
                this.ece = this.guiEntityList.visibleEntities.get(this.selectedEntry);

                try {
                    if (this.tempMob != null) {
                        this.tempMob.setDead();
                    }

                    try {
                        this.tempMob = (EntityLivingBase) this.ece.Clazz.getConstructor(World.class).newInstance(this.mc.world);
                    } catch (InstantiationException var18) {
                        this.tempMob = null;
                    }
                } catch (Throwable var19) {
                    Logger.getLogger(AdvancedGui.class.getName()).log(Level.SEVERE, null, var19);
                }
            }

            this.fontRenderer.drawStringWithShadow("Scale Factor:", (float) (220 - this.fontRenderer.getStringWidth("Scale Factor")), 32.0F, 16777215);
            this.fontRenderer.drawStringWithShadow("X Offset:", (float) (220 - this.fontRenderer.getStringWidth("X Offset")), 48.0F, 16777215);
            this.fontRenderer.drawStringWithShadow("Y Offset:", (float) (220 - this.fontRenderer.getStringWidth("Y Offset")), 64.0F, 16777215);
            this.fontRenderer.drawStringWithShadow("Size Scaling:", (float) (220 - this.fontRenderer.getStringWidth("Size Scaling")), 80.0F, 16777215);
            this.fontRenderer.drawStringWithShadow("Baby Scaling:", (float) (220 - this.fontRenderer.getStringWidth("Baby Scaling")), 112.0F, 16777215);
            this.fontRenderer.drawStringWithShadow("Name Override:", (float) (220 - this.fontRenderer.getStringWidth("Name Override")), 144.0F, 16777215);
            this.fontRenderer.drawStringWithShadow("Full Class Name:", (float) (220 - this.fontRenderer.getStringWidth("Full Class Name")), 160.0F, 16777215);
            this.fontRenderer.drawStringWithShadow(this.guiEntityList.visibleEntities.get(this.selectedEntry).Clazz.getName(), 225.0F, 160.0F, 10066431);

            for (GuiTextField transparency : this.textboxes) {
                transparency.drawTextBox();
            }

            GL11.glPushMatrix();
            if (this.ece == null) {
                this.ece = this.guiEntityList.visibleEntities.get(this.selectedEntry);
            }

            String var14 = this.ece.NameOverride;
            if (this.tempMob != null) {
                if (var14 == null || "".equals(var14)) {
                    var14 = this.tempMob.getName();
                }
            } else if (var14 == null || "".equals(var14)) {
                Map<Class<? extends Entity>, String> classToStringMapping = Tools.getEntityList();
                if (classToStringMapping.containsKey(this.ece.Clazz)) {
                    var14 = classToStringMapping.get(this.ece.Clazz);
                } else {
                    var14 = this.ece.Clazz.getName().substring(this.ece.Clazz.getName().lastIndexOf(".") + 1);
                }
            }

            this.zLevel += 0.1F;
            GL11.glPushMatrix();
            float var16 = DIConfig.mainInstance().guiScale;
            GL11.glPushAttrib(8192);

            try {
                float headPosX = 150.0F;
                headPosX += (float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWX) + (float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH) / 2.0F;
                float headPosY = 175.0F;
                headPosY += (float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWY) + (float) (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT) / 2.0F;
                headPosX = (float) mouseX - headPosX;
                headPosY = (float) mouseY - headPosY;
                float f2 = this.tempMob.renderYawOffset;
                float f3 = this.tempMob.rotationYaw;
                float f4 = this.tempMob.rotationPitch;
                float f5 = this.tempMob.prevRotationYawHead;
                float f6 = this.tempMob.rotationYawHead;
                this.tempMob.renderYawOffset = (float) Math.atan(headPosX / 40.0F) * 20.0F + 35.0F;
                this.tempMob.rotationYaw = (float) Math.atan(headPosX / 40.0F) * 40.0F;
                this.tempMob.rotationPitch = (float) Math.atan(headPosY / 40.0F) * 20.0F;
                this.tempMob.rotationYawHead = this.tempMob.rotationYaw;
                this.tempMob.prevRotationYawHead = this.tempMob.rotationYaw;
                DIConfig.mainInstance().guiScale = 1.0F;
                DIGuiTools.DrawPortraitSkinned(150, 175, var14, (int) Math.ceil(this.tempMob == null ? 0.0D : (double) this.tempMob.getHealth()), (int) Math.ceil(this.tempMob == null ? 0.0D : (double) this.tempMob.getMaxHealth()), this.tempMob);
                this.tempMob.renderYawOffset = f2;
                this.tempMob.rotationYaw = f3;
                this.tempMob.rotationPitch = f4;
                this.tempMob.prevRotationYawHead = f5;
                this.tempMob.rotationYawHead = f6;
            } catch (Throwable ignored) {
            }

            GL11.glPopAttrib();
            this.zLevel += 0.1F;
            DIConfig.mainInstance().guiScale = var16;
            GL11.glPopMatrix();
            super.drawScreen(mouseX, mouseY, elapsedTime);
            GL11.glPopMatrix();

            try {
                if (this.controlLocations != null) {
                    for (int lines = 0; lines < this.controlLocations.size(); ++lines) {
                        try {
                            if (this.controlLocations.get(lines).contains(mouseX, mouseY)) {
                                found = true;
                                int ex = 0;
                                if (this.controlLocations.get(lines) != null && (this.LastHovered == null || this.LastHovered != this.controlLocations.get(lines))) {
                                    this.LastHovered = this.controlLocations.get(lines);
                                    this.timeHovered = 1;
                                }

                                if (this.timeHovered != 0) {
                                    this.timeHovered += MathHelper.floor(diff);
                                    if (this.timeHovered > 255) {
                                        this.timeHovered = -2000;
                                        this.backwards = true;
                                    } else if (this.backwards && this.timeHovered > 0) {
                                        this.backwards = false;
                                        this.timeHovered = 0;
                                    }

                                    ex = Math.min(255, Math.abs(this.timeHovered));
                                }

                                ex = MathHelper.floor((float) ex * 0.75F);
                                this.tooltip.setGlobalAlpha(ex);
                                this.tooltip.HEIGHT = this.controlTooltipText.get(lines).length * (this.fontRenderer.FONT_HEIGHT + 2) + 6;
                                this.tooltip.setUpForDraw(mouseX, mouseY, this.controlTooltipText.get(lines));
                                this.tooltip.setDontUseTexture();
                                this.tooltip.drawStrings(this.fontRenderer);
                                break;
                            }
                        } catch (Throwable ignored) {
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        if (!found) {
            this.LastHovered = null;
            this.timeHovered = 0;
        }

    }

    @Override
    public void updateScreen() {
        this.search.updateCursorCounter();

        for (GuiTextField gtf : this.textboxes) {
            gtf.updateCursorCounter();
        }

        super.updateScreen();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        EntityConfigurationEntry current = this.guiEntityList.visibleEntities.get(this.selectedEntry);
        if (button instanceof GuiCheckBox) {
            ((GuiCheckBox) button).toggle();
        } else {
            for (GuiTextField textbox : this.textboxes) {
                if (textbox != this.textboxes.get(5)) {
                    try {
                        textbox.setText("" + Float.valueOf(textbox.getText()));
                    } catch (Throwable var6) {
                        textbox.setText("0.0");
                    }
                }
            }
        }

        EntityConfigurationEntry newEce1 = new EntityConfigurationEntry(current.Clazz, Float.parseFloat(this.textboxes.get(0).getText()), Float.parseFloat(this.textboxes.get(1).getText()), Float.parseFloat(this.textboxes.get(2).getText()), Float.parseFloat(this.textboxes.get(3).getText()), Float.parseFloat(this.textboxes.get(4).getText()), ((GuiCheckBox) this.buttonList.get(1)).isChecked(), this.textboxes.get(5).getText(), ((GuiCheckBox) this.buttonList.get(0)).isChecked(), current.maxHP, current.eyeHeight);
        if (!current.equals(newEce1)) {
            Tools.getInstance().getEntityMap().put(newEce1.Clazz, newEce1);
            EntityConfigurationEntry.saveEntityConfig(newEce1);
            GuiEntityList.entities = new ArrayList<>(Tools.getInstance().getEntityMap().values());
            GuiEntityList.entities.sort(this.comparator);
            this.guiEntityList.visibleEntities.set(this.selectedEntry, newEce1);
        }

        super.actionPerformed(button);
    }

    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

    public void listClickedCallback(int index) {
        this.buttonList.clear();
        this.textboxes = new ArrayList<>();
        this.entrySelected = true;
        this.selectedEntry = index;
        this.buttonList.add(0, new GuiCheckBox(0, 220 - (this.fontRenderer.getStringWidth("Ignore Mob") + 12), 14, this.fontRenderer.getStringWidth("Ignore Mob") + 12, 12, "Ignore Mob"));
        ((GuiCheckBox) this.buttonList.get(0)).setChecked(this.guiEntityList.visibleEntities.get(index).IgnoreThisMob);
        this.buttonList.add(1, new GuiCheckBox(1, 220 - (this.fontRenderer.getStringWidth("Prefix Babies") + 12), 96, this.fontRenderer.getStringWidth("Prefix Babies") + 12, 12, "Prefix Babies"));
        ((GuiCheckBox) this.buttonList.get(1)).setChecked(this.guiEntityList.visibleEntities.get(index).AppendBaby);
        this.buttonList.add(2, new GuiButton(2, 315, 186, 80, 20, "Save"));
        this.addTextBoxes(index);
        String Name = this.guiEntityList.visibleEntities.get(index).NameOverride != null ? this.guiEntityList.visibleEntities.get(index).NameOverride : "";

        this.textboxes.get(5).setText(Name);
    }

    public void addTextBoxes(int listIndex) {
        this.textboxes.add(0, new GuiTextField(0, this.fontRenderer, 225, 30, 120, 10));
        this.textboxes.get(0).setText(String.valueOf(this.guiEntityList.visibleEntities.get(listIndex).ScaleFactor));
        this.textboxes.add(1, new GuiTextField(1, this.fontRenderer, 225, 46, 120, 10));
        this.textboxes.get(1).setText(String.valueOf(this.guiEntityList.visibleEntities.get(listIndex).XOffset));
        this.textboxes.add(2, new GuiTextField(2, this.fontRenderer, 225, 62, 120, 10));
        this.textboxes.get(2).setText(String.valueOf(this.guiEntityList.visibleEntities.get(listIndex).YOffset));
        this.textboxes.add(3, new GuiTextField(3, this.fontRenderer, 225, 78, 120, 10));
        this.textboxes.get(3).setText(String.valueOf(this.guiEntityList.visibleEntities.get(listIndex).EntitySizeScaling));
        this.textboxes.add(4, new GuiTextField(4, this.fontRenderer, 225, 110, 120, 10));
        this.textboxes.get(4).setText(String.valueOf(this.guiEntityList.visibleEntities.get(listIndex).BabyScaleFactor));
        this.textboxes.add(5, new GuiTextField(5, this.fontRenderer, 225, 142, 120, 10));
    }
}
