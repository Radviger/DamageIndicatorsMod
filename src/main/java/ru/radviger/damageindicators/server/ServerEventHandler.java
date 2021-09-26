package ru.radviger.damageindicators.server;

import ru.radviger.damageindicators.DamageIndicators;
import ru.radviger.damageindicators.configuration.IndicatorsConfig;
import ru.radviger.damageindicators.core.DIPermissions;
import ru.radviger.damageindicators.core.DIPotionEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import java.util.*;

public class ServerEventHandler {
    public static Map<String, Map<UUID, Long>> potionTimers = new HashMap<>();

    public static void sendServerSettings(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            byte toggles = 0;
            IndicatorsConfig config = IndicatorsConfig.mainInstance();
            if (config.portraitEnabled) {
                toggles |= 2;
            }
            if (config.enablePotionEffects) {
                toggles |= 4;
            }
            if (config.popOffsEnabled) {
                toggles |= 8;
            }
            DamageIndicators.network.sendTo(new DIPermissions(toggles), (EntityPlayerMP) player);
        }

    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            sendServerSettings(event.player);
        }

    }

    @SubscribeEvent
    public void livingEvent(LivingUpdateEvent evt) {
        EntityLivingBase el = evt.getEntityLiving();
        if (IndicatorsConfig.mainInstance().enablePotionEffects && evt.getEntityLiving() != null) {
            Collection<PotionEffect> potionEffects = el.getActivePotionEffects();
            if (!potionEffects.isEmpty()) {
                int offset = MathHelper.floor((float) IndicatorsConfig.mainInstance().packetrange / 2.0F);
                AxisAlignedBB aabb = new AxisAlignedBB(el.posX - (double) offset, el.posY - (double) offset, el.posZ - (double) offset, el.posX + (double) offset, el.posY + (double) offset, el.posZ + (double) offset);
                List<EntityPlayer> players = el.world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
                if (!players.isEmpty()) {
                    Iterator<EntityPlayer> var7 = players.iterator();

                    while (true) {
                        EntityPlayer player;
                        Map<UUID, Long> potioneffectstimer;
                        do {
                            if (!var7.hasNext()) {
                                return;
                            }

                            player = var7.next();
                            potionTimers.computeIfAbsent(player.getName(), k -> new WeakHashMap<>());

                            potioneffectstimer = potionTimers.get(player.getName());
                        } while (potioneffectstimer.containsKey(el.getPersistentID()) && System.currentTimeMillis() - potioneffectstimer.get(el.getPersistentID()) <= 1000L);

                        if (player instanceof EntityPlayerMP) {
                            DamageIndicators.network.sendTo(new DIPotionEffects(el, this.getFormattedPotionEffects(el)), (EntityPlayerMP) player);
                            potioneffectstimer.put(el.getPersistentID(), System.currentTimeMillis());
                        }
                    }
                }
            }
        }

    }

    public List<PotionEffect> getFormattedPotionEffects(EntityLivingBase el) {
        return new ArrayList<>(el.getActivePotionEffects());
    }
}
