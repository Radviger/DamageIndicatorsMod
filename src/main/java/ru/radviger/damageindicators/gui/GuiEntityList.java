package ru.radviger.damageindicators.gui;

import ru.radviger.damageindicators.core.EntityConfigurationEntry;
import ru.radviger.damageindicators.core.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiEntityList extends GuiScrollingList {
    public static List<EntityConfigurationEntry> entities;
    public List<EntityConfigurationEntry> visibleEntities;
    public int selectedEntry = 0;
    private AdvancedGui parent;

    public GuiEntityList(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight, AdvancedGui parent) {
        super(client, width, height, top, bottom, left, entryHeight);
        this.parent = parent;
        this.visibleEntities = new ArrayList<>(entities);
    }

    @Override
    protected int getSize() {
        return this.visibleEntities.size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        this.selectedEntry = index;
        this.parent.listClickedCallback(index);
    }

    @Override
    protected boolean isSelected(int index) {
        return this.selectedEntry == index;
    }

    @Override
    protected void drawBackground() {
        this.parent.drawBackground(2);
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5) {
        try {
            Map<Class<? extends Entity>, String> classToStringMapping = Tools.getEntityList();
            String entryName = classToStringMapping.get(this.visibleEntities.get(listIndex).Clazz);
            if (this.visibleEntities.get(listIndex).NameOverride != null && !"".equals(this.visibleEntities.get(listIndex).NameOverride)) {
                entryName = this.visibleEntities.get(listIndex).NameOverride;
            } else if (entryName == null || "".equals(entryName)) {
                String[] ModName = this.visibleEntities.get(listIndex).Clazz.getName().split("\\.");
                if (ModName.length > 0) {
                    entryName = ModName[ModName.length - 1];
                } else {
                    entryName = this.visibleEntities.get(listIndex).Clazz.getName();
                }
            }

            if (this.visibleEntities.get(listIndex).Clazz == EntityOtherPlayerMP.class) {
                entryName = "Other Player";
            } else if (this.visibleEntities.get(listIndex).Clazz == EntityMob.class) {
                this.visibleEntities.remove(listIndex);
            } else if (this.visibleEntities.get(listIndex).Clazz == EntityLivingBase.class) {
                this.visibleEntities.remove(listIndex);
            } else if (this.visibleEntities.get(listIndex).Clazz == EntityLiving.class) {
                this.visibleEntities.remove(listIndex);
            }

            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(entryName, this.listWidth - 10), this.left + 3, var3 + 2, 16777215);
            String ModName1 = "Vanilla/Unknown Mod";
            EntityRegistration er = EntityRegistry.instance().lookupModSpawn(this.visibleEntities.get(listIndex).Clazz, true);
            if (er != null) {
                ModName1 = er.getContainer().getName();
            }

            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(ModName1, this.listWidth - 10), this.left + 3, var3 + 12, 10066431);
        } catch (Throwable ignored) {
        }

    }
}
