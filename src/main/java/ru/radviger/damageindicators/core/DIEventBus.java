package ru.radviger.damageindicators.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import ru.radviger.damageindicators.DamageIndicators;
import ru.radviger.damageindicators.client.ProxyClient;
import ru.radviger.damageindicators.configuration.IndicatorsConfig;
import ru.radviger.damageindicators.gui.DIGuiTools;
import ru.radviger.damageindicators.gui.RepositionGui;
import ru.radviger.damageindicators.rendering.ParticleText;
import ru.radviger.damageindicators.util.RaytraceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DIEventBus {
    public static Map<Integer, Integer> healths = new HashMap<>();
    public static Map<Integer, List<PotionEffect>> potionEffects = new HashMap<>();
    public static List<Integer> enemies = new ArrayList<>();
    public static int playerDim = 0;
    public static String playerName = "";
    public static int lastTargeted = -1;
    public static boolean searched = false;
    public static double tick = 0.0D;
    public static int updateSkip = 4;
    double count = 5.0D;

    public static void updateMouseOversSkinned(float elapsedTime) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;

        if (player != null) {
            IndicatorsConfig config = IndicatorsConfig.mainInstance();
            EntityLivingBase el = null;
            if (updateSkip-- <= 0) {
                updateSkip = 4;
                el = RaytraceUtil.getClosestLivingEntity(player, config.mouseoverRange);
                if (el != null && el.getHealth() <= 0.0F) {
                    System.err.println("Skipping dead entity " + el + ": " + el.getHealth());
                    el = null;
                }
            }

            Map<Class<? extends Entity>, EntityConfigurationEntry> entityMap = Tools.getInstance().getEntityMap();
            if (el != null) {
                Class<? extends EntityLivingBase> clazz = el.getClass();
                EntityConfigurationEntry entry = entityMap.get(clazz);
                if (entry == null) {
                    Configuration cfg = EntityConfigurationEntry.getEntityConfiguration();
                    entry = EntityConfigurationEntry.generateDefaultConfiguration(cfg, clazz);
                    entry.save();
                    entityMap.put(clazz, entry);
                }

                if (entry.ignore) {
                    System.err.println("Skipping ignored entity " + el);
                    el = null;
                } else {
                    lastTargeted = el.getEntityId();
                }
            }

            if (el != null || lastTargeted != -1 && (config.portraitLifetime == -1 || tick > 0.0D)) {
                ScaledResolution resolution = new ScaledResolution(mc);
                if (config.locX > resolution.getScaledWidth() - 135) {
                    config.locX = resolution.getScaledWidth() - 135;
                }

                if (config.locY > resolution.getScaledHeight() - 50) {
                    config.locY = resolution.getScaledHeight() - 50;
                }

                if (config.locX < 0) {
                    config.locX = 0;
                }

                if (config.locY < 0) {
                    config.locY = 0;
                }

                GlStateManager.color(1F, 1F, 1F, 1F);
                if (el == null) {
                    tick -= elapsedTime;
                    el = (EntityLivingBase) mc.world.getEntityByID(lastTargeted);

                    if (el == null) {
                        lastTargeted = -1;
                    }
                } else {
                    tick = config.portraitLifetime;
                }

                if (el == null) {
                    return;
                }

                lastTargeted = el.getEntityId();
                Class<? extends Entity> clazz = el.getClass();
                EntityConfigurationEntry entry = entityMap.get(clazz);
                if (entry.maxHP == -1 || entry.eyeHeight == -1.0F) {
                    entry.eyeHeight = el.getEyeHeight();
                    entry.maxHP = MathHelper.floor(Math.ceil(el.getMaxHealth()));
                }

                if (entry.maxHP != MathHelper.floor(Math.ceil(el.getMaxHealth()))) {
                    entry.maxHP = MathHelper.floor(Math.ceil(el.getMaxHealth()));
                }

                String name = el.getName();

                if (!(el instanceof EntityPlayer) && entry.nameOverride != null && !entry.nameOverride.isEmpty()) {
                    name = entry.nameOverride;
                }

                GlStateManager.pushMatrix();
                GlStateManager.translate((1.0F - config.guiScale) * (float) config.locX, (1.0F - config.guiScale) * (float) config.locY, 0.0F);
                GlStateManager.scale(config.guiScale, config.guiScale, config.guiScale);

                DIGuiTools.drawPortraitSkinned(config.locX, config.locY, name, MathHelper.ceil(el.getHealth()), MathHelper.ceil(el.getMaxHealth()), el);

                GlStateManager.popMatrix();

                OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                GlStateManager.color(1F, 1F, 1F, 1F);
            }
        }

    }

    @SubscribeEvent
    public void arrowNook(LivingHurtEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && evt.getEntityLiving() != null && evt.getSource() instanceof EntityDamageSourceIndirect && evt.getSource().getImmediateSource() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) evt.getSource().getImmediateSource();
            if (arrow != null && arrow.getIsCritical()) {
                DamageIndicators.proxy.doCritical(evt.getEntityLiving());
            }
        }

    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingDeathEvent evt) {
    }

    private void updateHealth(EntityLivingBase el, int currentHealth) {
        if (healths.containsKey(el.getEntityId())) {
            int lastHealth = healths.get(el.getEntityId());
            if (lastHealth != currentHealth) {
                int damage = lastHealth - currentHealth;
                double var10004 = el.posY + (double) el.height;
                ParticleText customParticle = new ParticleText(Minecraft.getMinecraft().world, el.posX, var10004, el.posZ, 0.001D, 0.05F * IndicatorsConfig.mainInstance().BounceStrength, 0.001D, damage);
                if (Minecraft.getMinecraft().player.canEntityBeSeen(el)) {
                    customParticle.shouldOnTop = true;
                } else if (Minecraft.getMinecraft().isSingleplayer()) {
                    customParticle.shouldOnTop = IndicatorsConfig.mainInstance().alwaysRender;
                }

                if (el != Minecraft.getMinecraft().player || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
                    Minecraft.getMinecraft().effectRenderer.addEffect(customParticle);
                }
            }
        }

        healths.put(el.getEntityId(), currentHealth);
    }

    @SubscribeEvent
    public void entityDeath(LivingDeathEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && IndicatorsConfig.mainInstance().popOffsEnabled) {
            this.updateHealth(evt.getEntityLiving(), 0);
        }

        Integer entityID = evt.getEntity().getEntityId();
        potionEffects.remove(entityID);
        healths.remove(entityID);
        enemies.remove((Object)entityID);
    }

    @SubscribeEvent
    public void livingUpdate(Pre evt) {
        this.count -= evt.getPartialRenderTick();
        Entity entity = evt.getEntity();
        int entityId = entity.getEntityId();

        if (entity.isDead) {
            potionEffects.remove(entityId);
            healths.remove(entityId);
            enemies.remove((Object)entityId);
        }
    }

    @SubscribeEvent
    public void attackEntity(AttackEntityEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && evt.getEntityLiving() != null && evt.getEntityPlayer() != null) {
            boolean flag = evt.getEntityPlayer().fallDistance > 0.0F && !evt.getEntityPlayer().onGround && !evt.getEntityPlayer().isOnLadder() && !evt.getEntityPlayer().isInWater() && evt.getEntityPlayer().getRidingEntity() == null;
            if (flag) {
                DamageIndicators.proxy.doCritical(evt.getTarget());
            }
        }

    }

    @SubscribeEvent
    public void livingEvent(LivingUpdateEvent evt) {
        EntityLivingBase el = evt.getEntityLiving();
        int entityId = el.getEntityId();

        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            if (IndicatorsConfig.mainInstance().popOffsEnabled) {
                this.updateHealth(el, MathHelper.ceil(el.getHealth()));
            }

            if (el.isDead) {
                potionEffects.remove(entityId);
                healths.remove(entityId);
                enemies.remove((Object)entityId);
            }
        }

    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void changeDimension(EntityJoinWorldEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (evt.getEntity() == mc.player) {
                potionEffects.clear();
                healths.clear();
                enemies.clear();
                playerDim = mc.player.dimension;
                playerName = mc.player.getName();
            }
        }
    }

    @SubscribeEvent
    public void mobHurtUs(LivingHurtEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && evt.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) evt.getEntity();
            if (player.getName().equals(playerName) && evt.getSource() != null && evt.getSource().getTrueSource() != null) {
                enemies.add(evt.getSource().getTrueSource().getEntityId());
            }
        }

    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void rendergui(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre event) {
        if (Minecraft.isGuiEnabled() && ProxyClient.kb == null) {
            ProxyClient.kb = new KeyBinding("key.portaitreposition", 52, "key.categories.ui");
            ClientRegistry.registerKeyBinding(ProxyClient.kb);
            KeyBinding.resetKeyBindingArrayAndHash();
        }

        Minecraft mc = Minecraft.getMinecraft();

        if (ProxyClient.kb.isPressed()) {
            RepositionGui gui = new RepositionGui();
            mc.displayGuiScreen(gui);
        }

        IndicatorsConfig config = IndicatorsConfig.mainInstance();
        boolean visible = config.alternateRenderingMethod && event.getType() == ElementType.CHAT;
        if (!visible) {
            visible = event.getType() == ElementType.PORTAL && !config.alternateRenderingMethod;
        }

        if (event.getType() == ElementType.BOSSHEALTH && config.supressBossUI) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
        } else if (visible && mc.player != null) {
            if (mc.gameSettings.hideGUI) {
                lastTargeted = -1;
                return;
            }

            if (mc.gameSettings.showDebugInfo && config.DebugHidesWindow) {
                lastTargeted = -1;
                return;
            }

            if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
                lastTargeted = -1;
                return;
            }

            if (!searched) {
                Tools.getInstance().scanforEntities();
                searched = true;
            }

            if (config.portraitEnabled && !DIPermissions.Handler.allDisabled && !DIPermissions.Handler.mouseOversDisabled) {
                if (config.highCompatibilityMod) {
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    GL11.glPushClientAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);
                }

                updateMouseOversSkinned(event.getPartialTicks());

                if (config.highCompatibilityMod) {
                    GL11.glPopClientAttrib();
                    GL11.glPopAttrib();
                }
            }
        }

    }
}
