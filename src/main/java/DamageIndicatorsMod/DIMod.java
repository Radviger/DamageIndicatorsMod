package DamageIndicatorsMod;

import DamageIndicatorsMod.configuration.DIConfig;
import DamageIndicatorsMod.core.DIPermissions;
import DamageIndicatorsMod.core.DIPotionEffects;
import DamageIndicatorsMod.server.CommandDI;
import DamageIndicatorsMod.server.DIProxy;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(
    useMetadata = true,
    modid = "damageindicatorsmod",
    name = "Damage Indicators Mod",
    acceptableRemoteVersions = "*",
    acceptedMinecraftVersions = "[1.12.2]"
)
public class DIMod {
    public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("DIMod");
    public static Logger log;
    @Instance("damageindicatorsmod")
    public static DIMod instance;
    @SidedProxy(
        clientSide = "DamageIndicatorsMod.client.DIClientProxy",
        serverSide = "DamageIndicatorsMod.server.DIProxy",
        modId = "damageindicatorsmod"
    )
    public static DIProxy proxy;
    int packetID = 0;
    CommandDI cdi = new CommandDI();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();

        try {
            DIConfig.loadConfig(event.getSuggestedConfigurationFile());
        } catch (Throwable var4) {
            var4.printStackTrace();
            if (!event.getSuggestedConfigurationFile().delete()) {
                event.getSuggestedConfigurationFile().deleteOnExit();
            }

            DIConfig.loadConfig(event.getSuggestedConfigurationFile());
        }

        try {
            network.registerMessage(DIPermissions.Handler.class, DIPermissions.class, this.packetID, Side.SERVER);
            network.registerMessage(DIPermissions.Handler.class, DIPermissions.class, this.packetID++, Side.CLIENT);
            network.registerMessage(DIPotionEffects.Handler.class, DIPotionEffects.class, this.packetID, Side.SERVER);
            network.registerMessage(DIPotionEffects.Handler.class, DIPotionEffects.class, this.packetID++, Side.CLIENT);
        } catch (Throwable var3) {
            var3.printStackTrace();
        }

    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.register();
    }

    @EventHandler
    public void load(FMLPostInitializationEvent event) {
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent evt) {
        try {
            ServerCommandManager scm = (ServerCommandManager) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
            if (!scm.getCommands().containsKey(this.cdi.getName())) {
                scm.registerCommand(this.cdi);
            }
        } catch (Throwable ignored) {}
    }
}
