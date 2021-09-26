package ru.radviger.damageindicators.client;

import ru.radviger.damageindicators.textures.JarSkinRegistration;
import ru.radviger.damageindicators.configuration.IndicatorsConfig;
import ru.radviger.damageindicators.core.DIEventBus;
import ru.radviger.damageindicators.core.Tools;
import ru.radviger.damageindicators.rendering.ParticleText;
import ru.radviger.damageindicators.server.ProxyCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ProxyClient extends ProxyCommon {
    public static KeyBinding kb;
    int wordParticle = 1051414;

    @Override
    public void register() {
        super.register();
        DIEventBus seh = new DIEventBus();
        MinecraftForge.EVENT_BUS.register(seh);
        Tools.getInstance().RegisterRenders();
        JarSkinRegistration.init();
        Minecraft.getMinecraft().effectRenderer.registerParticle(this.wordParticle, (particleID, world, x, y, z, velX, velY, velZ, args) -> {
            ParticleText customParticle = new ParticleText(world, x, y, z, velX, velY, velZ);
            if (args[0] == 1) {
                customParticle.shouldOnTop = true;
            }

            return customParticle;
        });
    }

    @Override
    public void doCritical(Entity target) {
        int particles = 0;
        if (Minecraft.getMinecraft().player.canEntityBeSeen(target)) {
            particles = 1;
        } else if (Minecraft.getMinecraft().isSingleplayer()) {
            particles = IndicatorsConfig.mainInstance().alwaysRender ? 1 : 0;
        }

        if (target != Minecraft.getMinecraft().player || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
            double y = target.posY + (double) target.height;
            Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(this.wordParticle, target.posX, y, target.posZ, 0.001D, (double) (0.05F * IndicatorsConfig.mainInstance().BounceStrength), 0.001D, new int[]{particles});
        }

    }

    @Override
    public EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().player;
    }
}
