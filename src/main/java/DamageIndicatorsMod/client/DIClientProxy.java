package DamageIndicatorsMod.client;

import DITextures.JarSkinRegistration;
import DamageIndicatorsMod.DIMod;
import DamageIndicatorsMod.configuration.DIConfig;
import DamageIndicatorsMod.core.DIEventBus;
import DamageIndicatorsMod.core.Tools;
import DamageIndicatorsMod.rendering.DIWordParticles;
import DamageIndicatorsMod.server.DIProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

public class DIClientProxy extends DIProxy {
    public static KeyBinding kb;
    int wordParticle = 1051414;

    @Override
    public void register() {
        super.register();
        DIEventBus seh = new DIEventBus();
        MinecraftForge.EVENT_BUS.register(seh);
        Tools.getInstance().RegisterRenders();
        JarSkinRegistration.init();
        Minecraft.getMinecraft().effectRenderer.registerParticle(this.wordParticle, (particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, p_178902_15_) -> {
            DIWordParticles customParticle = new DIWordParticles(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            if (p_178902_15_[0] == 1) {
                customParticle.shouldOnTop = true;
            }

            return customParticle;
        });
    }

    @Override
    public void doCritical(Entity target) {
        int shouldbeseen = 0;
        if (Minecraft.getMinecraft().player.canEntityBeSeen(target)) {
            shouldbeseen = 1;
        } else if (Minecraft.getMinecraft().isSingleplayer()) {
            shouldbeseen = DIConfig.mainInstance().alwaysRender ? 1 : 0;
        }

        if (target != Minecraft.getMinecraft().player || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
            double var10003 = target.posY + (double) target.height;
            Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(this.wordParticle, target.posX, var10003, target.posZ, 0.001D, (double) (0.05F * DIConfig.mainInstance().BounceStrength), 0.001D, new int[]{shouldbeseen});
        }

    }

    @Override
    public EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().player;
    }
}
