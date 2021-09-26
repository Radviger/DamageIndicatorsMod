package ru.radviger.damageindicators.server;

import ru.radviger.damageindicators.configuration.IndicatorsConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommandDI extends CommandBase {
    @Override
    public String getName() {
        return "direload";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.damageindicators.direload";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender.getName().equalsIgnoreCase("server")) {
            IndicatorsConfig.loadConfig(null);

            for (EntityPlayer player : server.getPlayerList().getPlayers()) {
                if (player != null) {
                    ServerEventHandler.sendServerSettings(player);
                }
            }
        } else if (FMLCommonHandler.instance().getSide().isClient()) {
            sender.sendMessage(new TextComponentString("Configuration Reloading"));
            IndicatorsConfig.loadConfig(null);
        }
    }
}
