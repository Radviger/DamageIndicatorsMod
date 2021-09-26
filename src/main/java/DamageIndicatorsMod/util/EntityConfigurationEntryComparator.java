package DamageIndicatorsMod.util;

import DamageIndicatorsMod.core.EntityConfigurationEntry;
import DamageIndicatorsMod.core.Tools;
import net.minecraft.entity.Entity;

import java.util.Comparator;
import java.util.Map;

public class EntityConfigurationEntryComparator implements Comparator<EntityConfigurationEntry> {
    @Override
    public int compare(EntityConfigurationEntry o1, EntityConfigurationEntry o2) {
        Map<Class<? extends Entity>, String> mapping = Tools.getEntityList();
        String str1 = mapping.containsKey(o1.Clazz) ? mapping.get(o1.Clazz) : o1.Clazz.getName();
        String str2 = mapping.containsKey(o2.Clazz) ? mapping.get(o2.Clazz) : o2.Clazz.getName();
        return str1.compareTo(str2);
    }
}
