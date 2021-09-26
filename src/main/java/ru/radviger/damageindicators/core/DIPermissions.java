package ru.radviger.damageindicators.core;

import ru.radviger.damageindicators.DamageIndicators;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DIPermissions implements IMessage {
    byte message;

    public DIPermissions() {
    }

    public DIPermissions(byte message) {
        this.message = message;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            this.message = buf.readByte();
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            buf.writeByte(this.message);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public static class Handler implements IMessageHandler<DIPermissions, IMessage> {
        public static boolean allDisabled = false;
        public static boolean mouseOversDisabled = false;
        public static boolean potionEffectsDisabled = false;
        public static boolean popOffsDisabled = false;

        public static void processPermissions(EntityPlayer player, byte toggles) {
            allDisabled = (toggles & 1) != 0;
            mouseOversDisabled = (toggles & 2) != 0;
            potionEffectsDisabled = (toggles & 4) != 0;
            popOffsDisabled = (toggles & 8) != 0;

        }

        @Override
        public DIPermissions onMessage(DIPermissions message, MessageContext ctx) {
            processPermissions(DamageIndicators.proxy.getPlayer(), (byte) 0);
            return null;
        }
    }
}
