package ru.radviger.damageindicators.util;

import ru.radviger.damageindicators.core.EntityConfigurationEntry;
import ru.radviger.damageindicators.core.Tools;
import net.minecraft.entity.Entity;

import java.util.Comparator;
import java.util.Map;

public class EntityConfigurationEntryComparator implements Comparator<EntityConfigurationEntry> {
    @Override
    public int compare(EntityConfigurationEntry o1, EntityConfigurationEntry o2) {
        Map<Class<? extends Entity>, String> mapping = Tools.getEntityList();
        String str1 = mapping.containsKey(o1.clazz) ? mapping.get(o1.clazz) : o1.clazz.getName();
        String str2 = mapping.containsKey(o2.clazz) ? mapping.get(o2.clazz) : o2.clazz.getName();
        return str1.compareTo(str2);
    }
}
