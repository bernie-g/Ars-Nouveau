package com.hollingsworth.arsnouveau.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.builder.AnimationBuilder;

import java.util.function.Supplier;

public class PacketEntityAnimationSync {

    private final int entityID;
    private final String animationController;
    private final String animation;

    public PacketEntityAnimationSync(Entity entity, String animationController, String animation) {
        this.entityID = entity.getEntityId();
        this.animationController = animationController;
        this.animation = animation;
    }

    public PacketEntityAnimationSync(int id, String animationController, String animation){
        this.entityID = id;
        this.animationController = animationController;
        this.animation = animation;
    }

    public static PacketEntityAnimationSync decode(PacketBuffer buf) {
        return new PacketEntityAnimationSync(buf.readInt(), buf.readString(), buf.readString());
    }

    public static void encode(PacketEntityAnimationSync msg, PacketBuffer buf) {
        buf.writeInt(msg.entityID);
        buf.writeString(msg.animationController);
        buf.writeString(msg.animation);
    }

    public static class Handler {
        public static void handle(final PacketEntityAnimationSync message, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().setPacketHandled(true);
                return;
            }

            ctx.get().enqueueWork(new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    Minecraft mc = Minecraft.getInstance();
                    ClientWorld world = mc.world;

                    if(world.getEntityByID(message.entityID) instanceof IAnimatable){
                        System.out.println("Adding animation for " + message.entityID );
                        IAnimatable animatedEntity = (IAnimatable) world.getEntityByID(message.entityID);
                        animatedEntity.getAnimationManager().setAnimationSpeed(2f);
                        animatedEntity.getAnimationManager()
                                .get(message.animationController).setAnimation(new AnimationBuilder().addAnimation(message.animation));
                    }
                };
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
