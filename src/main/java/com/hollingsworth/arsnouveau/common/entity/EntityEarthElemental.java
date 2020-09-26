package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.event.ProcessOreEvent;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketEntityAnimationSync;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.core.manager.AnimationManager;

public class EntityEarthElemental extends CreatureEntity implements IAnimatable
{
    AnimationManager manager = new AnimationManager();

    AnimationController<EntityEarthElemental> smeltController = new AnimationController(this, "smeltController", 20, this::smeltPredicate);

    AnimationController<EntityEarthElemental> idleController = new AnimationController(this, "idleController", 20, this::idlePredicate);


    private <E extends Entity & IAnimatable> boolean smeltPredicate(AnimationTestPredicate<E> event) {
        return true;
    }
    private <E extends Entity & IAnimatable> boolean idlePredicate(AnimationTestPredicate<E> event) {

        if(this.getHeldStack().isEmpty()){
            manager.setAnimationSpeed(1f);
            //System.out.println("idling");
            //idleController.setAnimation(new AnimationBuilder().addAnimation("idle", true));
        }else{
            return true;
        }
        return true;
    }

    protected EntityEarthElemental(EntityType<? extends CreatureEntity> type, World worldIn) {
        super(type, worldIn);
        registerControllers();
    }
    public EntityEarthElemental(World world){
        super(ModEntities.ENTITY_EARTH_ELEMENTAL_TYPE,world);
        registerControllers();
    }

    public void registerControllers(){
        manager.addAnimationController(smeltController);
        manager.addAnimationController(idleController);
    }

    @Override
    protected void registerData() {
        super.registerData();

    }

    @Override
    protected boolean processInteract(PlayerEntity player, Hand hand) {
        System.out.println(this.getHeldStack());
        System.out.println(this.getEntityId());
        return super.processInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if(getHeldStack().isEmpty() && !world.isRemote){
            for(ItemEntity itementity : this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(1.0D, 0.0D, 1.0D))) {
                if (!itementity.removed && !itementity.getItem().isEmpty() && !itementity.cannotPickup()) {
                    this.updateEquipmentIfNeeded(itementity);
                    System.out.println("sending packet");
                    Networking.sendToNearby(world, this, new PacketEntityAnimationSync(this.getEntityId(), "smeltController", "smelting"));
                    EventQueue.getInstance().addEvent(new ProcessOreEvent(this, 20 * 20));
                }
            }
        }
    }
    @Override
    protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
        if(this.getHeldStack().isEmpty()){
            setHeldStack(itemEntity.getItem());
            itemEntity.remove();
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, this.getSoundCategory(),1.0F, 1.0F);
        }
    }
    public void setHeldStack(ItemStack stack){
//        this.dataManager.set(HELD_ITEM,stack);
        this.setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
    }

    public ItemStack getHeldStack(){
        return this.getHeldItemMainhand();
//        return this.dataManager.get(HELD_ITEM);
    }


    @Override
    public AnimationManager getAnimationManager() {
        return manager;
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        if(getHeldStack() != null) {
            CompoundNBT itemTag = new CompoundNBT();
            getHeldStack().write(itemTag);
            tag.put("held", itemTag);
        }
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if(tag.contains("held"))
            setHeldStack(ItemStack.read((CompoundNBT)tag.get("held")));
    }
}
