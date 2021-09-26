package ru.radviger.damageindicators.core;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DITicker {
    public static DITicker instance = new DITicker();
}
