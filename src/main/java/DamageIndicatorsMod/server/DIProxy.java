package DamageIndicatorsMod.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class DIProxy {
    public ModContainer dimod;

    public void register() {
        ServerEventHandler seh = new ServerEventHandler();
        MinecraftForge.EVENT_BUS.register(seh);
        if (this.dimod == null) {
            for (ModContainer modContainer : Loader.instance().getModList()) {
                this.dimod = modContainer;
                if (this.dimod != null && this.dimod.getName().equals("Damage Indicators")) {
                    break;
                }
            }
        }

    }

    public void doCritical(Entity target) {
    }

    public EntityPlayer getPlayer() {
        return null;
    }
}
