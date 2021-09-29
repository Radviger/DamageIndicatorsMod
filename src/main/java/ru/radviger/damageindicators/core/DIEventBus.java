package ru.radviger.damageindicators.core;

import ru.radviger.damageindicators.textures.AbstractSkin;
import ru.radviger.damageindicators.textures.EnumSkinPart;
import ru.radviger.damageindicators.DamageIndicators;
import ru.radviger.damageindicators.client.ProxyClient;
import ru.radviger.damageindicators.configuration.IndicatorsConfig;
import ru.radviger.damageindicators.gui.DIGuiTools;
import ru.radviger.damageindicators.gui.RepositionGui;
import ru.radviger.damageindicators.rendering.ParticleText;
import ru.radviger.damageindicators.util.RaytraceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
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
import org.lwjgl.opengl.GL11;

import java.util.*;

public class DIEventBus {
    public static Map<Integer, Integer> healths = new HashMap<>();
    public static Map<Integer, List<PotionEffect>> potionEffects = new HashMap<>();
    public static List<Integer> enemies = new ArrayList<>();
    public static int playerDim = 0;
    public static String playerName = "";
    public static int LastTargeted = 0;
    public static boolean searched = false;
    public static double tick = 0.0D;
    public static int updateSkip = 4;
    static Entity last;
    double count = 5.0D;

    public static void updateMouseOversSkinned(float elapsedTime) {
        if (Minecraft.getMinecraft().player != null) {
            EntityLivingBase el = null;
            if (updateSkip-- <= 0) {
                updateSkip = 4;
                el = RaytraceUtil.getClosestLivingEntity(Minecraft.getMinecraft().player, IndicatorsConfig.mainInstance().mouseoverRange);
                if (el != null && el.getHealth() <= 0.0F) {
                    el = null;
                }
            }

            if (Minecraft.getMinecraft().player.getName().contains("rich1051414") && Minecraft.getMinecraft().player.isSneaking()) {
                Entity tmp = RaytraceUtil.getClosestEntity(Minecraft.getMinecraft().player, IndicatorsConfig.mainInstance().mouseoverRange);
                if (tmp != null && tmp != last) {
                    last = tmp;
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString(tmp.getClass().getName()));
                    TextTransfer textTransfer = new TextTransfer();
                    textTransfer.setClipboardContents(tmp.getClass().getName());
                }
            }

            if (el != null) {
                Class<? extends Entity> entityclass = el.getClass();
                EntityConfigurationEntry configentry = Tools.getInstance().getEntityMap().get(entityclass);
                if (configentry == null) {
                    Configuration configfile = EntityConfigurationEntry.getEntityConfiguration();
                    configentry = EntityConfigurationEntry.generateDefaultConfiguration(configfile, entityclass);
                    configentry.save();
                    Tools.getInstance().getEntityMap().put(entityclass, configentry);
                }

                if (configentry.IgnoreThisMob) {
                    el = null;
                } else {
                    LastTargeted = el.getEntityId();
                }
            }

            if (el != null || LastTargeted != 0 && (IndicatorsConfig.mainInstance().portraitLifetime == -1 || tick > 0.0D)) {
                ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
                if (IndicatorsConfig.mainInstance().locX > scaledresolution.getScaledWidth() - 135) {
                    IndicatorsConfig.mainInstance().locX = scaledresolution.getScaledWidth() - 135;
                }

                if (IndicatorsConfig.mainInstance().locY > scaledresolution.getScaledHeight() - 50) {
                    IndicatorsConfig.mainInstance().locY = scaledresolution.getScaledHeight() - 50;
                }

                if (IndicatorsConfig.mainInstance().locX < 0) {
                    IndicatorsConfig.mainInstance().locX = 0;
                }

                if (IndicatorsConfig.mainInstance().locY < 0) {
                    IndicatorsConfig.mainInstance().locY = 0;
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                if (el == null) {
                    tick -= elapsedTime;

                    try {
                        el = (EntityLivingBase) Minecraft.getMinecraft().world.getEntityByID(LastTargeted);
                    } catch (Throwable ignored) {
                    }

                    if (el == null) {
                        LastTargeted = 0;
                    }
                } else {
                    tick = IndicatorsConfig.mainInstance().portraitLifetime;
                }

                if (el == null) {
                    return;
                }

                LastTargeted = el.getEntityId();
                Class<? extends Entity> entityclass = el.getClass();
                EntityConfigurationEntry configentry = Tools.getInstance().getEntityMap().get(entityclass);
                if (configentry.maxHP == -1 || configentry.eyeHeight == -1.0F) {
                    configentry.eyeHeight = el.getEyeHeight();
                    configentry.maxHP = MathHelper.floor(Math.ceil(el.getMaxHealth()));
                }

                if (configentry.maxHP != MathHelper.floor(Math.ceil(el.getMaxHealth()))) {
                    configentry.maxHP = MathHelper.floor(Math.ceil(el.getMaxHealth()));
                }

                String Name = configentry.NameOverride;
                if (el instanceof EntityPlayer) {
                    Name = el.getName();
                }

                if (Name != null && !"".equals(Name)) {
                    if (el.isChild() && configentry.AppendBaby) {
                        Name = "\u00a7oBaby " + Name;
                    } else {
                        Name = "\u00a7o" + Name;
                    }
                } else {
                    Name = el.getName();
                    if (Name.endsWith(".name")) {
                        Name = Name.replace(".name", "");
                        Name = Name.substring(Name.lastIndexOf(".") + 1, Name.length());
                        Name = Name.substring(0, 1).toUpperCase() + Name.substring(1, Name.length());
                    }

                    if (el.isChild() && configentry.AppendBaby) {
                        Name = "Baby " + Name;
                    }
                }

                GL11.glPushMatrix();
                GL11.glTranslatef((1.0F - IndicatorsConfig.mainInstance().guiScale) * (float) IndicatorsConfig.mainInstance().locX, (1.0F - IndicatorsConfig.mainInstance().guiScale) * (float) IndicatorsConfig.mainInstance().locY, 0.0F);
                GL11.glScalef(IndicatorsConfig.mainInstance().guiScale, IndicatorsConfig.mainInstance().guiScale, IndicatorsConfig.mainInstance().guiScale);

                try {
                    DIGuiTools.DrawPortraitSkinned(IndicatorsConfig.mainInstance().locX, IndicatorsConfig.mainInstance().locY, Name, MathHelper.ceil(el.getHealth()), MathHelper.ceil(el.getMaxHealth()), el);
                    if (Calendar.getInstance().getWeekYear() + 3 > Calendar.getInstance().getWeeksInWeekYear()) {
                        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                        int Y = (Integer) AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGFRAMEY) + 75;
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    }
                } catch (Throwable ignored) {
                }

                GL11.glPopMatrix();
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                GL11.glDisableClientState(32888);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
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
    public void changeDimension(EntityJoinWorldEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            try {
                if (evt.getEntity() == Minecraft.getMinecraft().player) {
                    potionEffects.clear();
                    healths.clear();
                    enemies.clear();
                    playerDim = Minecraft.getMinecraft().player.dimension;
                    playerName = Minecraft.getMinecraft().player.getName();
                }
            } catch (Throwable ignored) {
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

        if (ProxyClient.kb.isPressed()) {
            RepositionGui gui = new RepositionGui();
            Minecraft.getMinecraft().displayGuiScreen(gui);
        }

        boolean flag = IndicatorsConfig.mainInstance().alternateRenderingMethod && event.getType() == ElementType.CHAT;
        if (!flag) {
            flag = event.getType() == ElementType.PORTAL && !IndicatorsConfig.mainInstance().alternateRenderingMethod;
        }

        if (event.getType() == ElementType.BOSSHEALTH && IndicatorsConfig.mainInstance().supressBossUI) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
        } else if (flag && Minecraft.getMinecraft().player != null) {
            if (Minecraft.getMinecraft().gameSettings.hideGUI) {
                LastTargeted = 0;
                return;
            }

            if (Minecraft.getMinecraft().gameSettings.showDebugInfo && IndicatorsConfig.mainInstance().DebugHidesWindow) {
                LastTargeted = 0;
                return;
            }

            if (Minecraft.getMinecraft().currentScreen != null && !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
                LastTargeted = 0;
                return;
            }

            if (!searched) {
                Tools.getInstance().scanforEntities();
                searched = true;
            }

            try {
                if (IndicatorsConfig.mainInstance().portraitEnabled && !DIPermissions.Handler.allDisabled && !DIPermissions.Handler.mouseOversDisabled) {
                    if (IndicatorsConfig.mainInstance().highCompatibilityMod) {
                        GL11.glPushAttrib(1048575);
                        GL11.glPushClientAttrib(-1);
                    }

                    try {
                        updateMouseOversSkinned(event.getPartialTicks());
                    } catch (Throwable var9) {
                        var9.printStackTrace();
                    }

                    if (IndicatorsConfig.mainInstance().highCompatibilityMod) {
                        GL11.glPopClientAttrib();
                        GL11.glPopAttrib();
                    }
                }
            } catch (Throwable var10) {
                var10.printStackTrace();
            }
        }

    }
}
