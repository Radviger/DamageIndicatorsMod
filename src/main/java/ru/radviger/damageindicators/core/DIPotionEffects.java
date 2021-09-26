package ru.radviger.damageindicators.core;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class DIPotionEffects implements IMessage {
    int entityID = -1;
    List<PotionEffect> potionEffects = new ArrayList<>();

    public DIPotionEffects() {
    }

    public DIPotionEffects(EntityLivingBase elb, List<PotionEffect> potionEffects) {
        this.entityID = elb.getEntityId();
        this.potionEffects = potionEffects;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            this.entityID = buf.readInt();
            int count = buf.readInt();

            for (int i = 0; i < count; ++i) {
                this.potionEffects.add(new PotionEffect(Potion.getPotionById(buf.readInt()), buf.readInt()));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    @Override
    public void toBytes(ByteBuf buf) {
        try {
            buf.writeInt(this.entityID);
            buf.writeInt(this.potionEffects.size());

            for (PotionEffect e : this.potionEffects) {
                buf.writeInt(Potion.getIdFromPotion(e.getPotion()));
                buf.writeInt(e.getDuration());
            }
        } catch (Throwable var3) {
            var3.printStackTrace();
        }

    }

    public static class Handler implements IMessageHandler<DIPotionEffects, IMessage> {
        @Override
        public DIPotionEffects onMessage(DIPotionEffects message, MessageContext ctx) {
            if (message.entityID != -1 && !message.potionEffects.isEmpty()) {
                DIEventBus.potionEffects.put(message.entityID, message.potionEffects);
            }

            return null;
        }
    }
}
