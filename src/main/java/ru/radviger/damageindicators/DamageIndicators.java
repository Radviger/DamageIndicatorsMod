package ru.radviger.damageindicators;

import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import ru.radviger.damageindicators.configuration.IndicatorsConfig;
import ru.radviger.damageindicators.core.DIPermissions;
import ru.radviger.damageindicators.core.DIPotionEffects;
import ru.radviger.damageindicators.server.CommandDI;
import ru.radviger.damageindicators.server.ProxyCommon;

@Mod(
    useMetadata = true,
    modid = "damageindicators",
    name = "Damage Indicators",
    acceptableRemoteVersions = "*",
    acceptedMinecraftVersions = "[1.12.2]"
)
public class DamageIndicators {
    public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("DI");
    public static Logger log;

    @SidedProxy(
        clientSide = "ru.radviger.damageindicators.client.ProxyClient",
        serverSide = "ru.radviger.damageindicators.server.ProxyCommon",
        modId = "damageindicators"
    )
    public static ProxyCommon proxy;
    int packetID = 0;
    CommandDI cdi = new CommandDI();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();

        try {
            IndicatorsConfig.loadConfig(event.getSuggestedConfigurationFile());
        } catch (Throwable t) {
            t.printStackTrace();
            if (!event.getSuggestedConfigurationFile().delete()) {
                event.getSuggestedConfigurationFile().deleteOnExit();
            }

            IndicatorsConfig.loadConfig(event.getSuggestedConfigurationFile());
        }

        network.registerMessage(DIPermissions.Handler.class, DIPermissions.class, this.packetID, Side.SERVER);
        network.registerMessage(DIPermissions.Handler.class, DIPermissions.class, this.packetID++, Side.CLIENT);
        network.registerMessage(DIPotionEffects.Handler.class, DIPotionEffects.class, this.packetID, Side.SERVER);
        network.registerMessage(DIPotionEffects.Handler.class, DIPotionEffects.class, this.packetID++, Side.CLIENT);
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
        ServerCommandManager scm = (ServerCommandManager) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        if (!scm.getCommands().containsKey(cdi.getName())) {
            scm.registerCommand(cdi);
        }
    }
}
