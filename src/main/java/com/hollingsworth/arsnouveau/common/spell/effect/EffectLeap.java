package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.List;

public class EffectLeap extends AbstractEffect {
    public EffectLeap() {
        super(ModConfig.EffectLeapID, "Leap");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            EntityRayTraceResult rayTraceResult1 = (EntityRayTraceResult) rayTraceResult;
            LivingEntity e = (LivingEntity) rayTraceResult1.getEntity();
            e.knockBack(e,2 + getAmplificationBonus(augments), -e.getLookVec().x,-e.getLookVec().z);
            e.setMotion(e.getMotion().add(0, 0.1+ 0.1 * getAmplificationBonus(augments), 0));
            e.velocityChanged = true;
        }
    }

    @Override
    protected String getBookDescription() {
        return "Launches the target in the direction they are looking. Amplification will increase the distance moved.";
    }

    @Override
    public Item getCraftingReagent() {
        return Items.SPIDER_EYE;
    }

    @Override
    public int getManaCost() {
        return 30;
    }
}
