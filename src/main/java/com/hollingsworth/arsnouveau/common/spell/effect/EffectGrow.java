package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;

public class EffectGrow  extends AbstractEffect {

    public EffectGrow() {
        super(ModConfig.EffectGrowID, "Grow");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(rayTraceResult instanceof BlockRayTraceResult) {
            for(BlockPos blockpos : SpellUtil.calcAOEBlocks(shooter, ((BlockRayTraceResult) rayTraceResult).getPos(), (BlockRayTraceResult) rayTraceResult, getBuffCount(augments, AugmentAOE.class))){
                //BlockPos blockpos = ((BlockRayTraceResult) rayTraceResult).getPos();
                ItemStack stack = new ItemStack(Items.BONE_MEAL);
                if(world instanceof ServerWorld)
                    BoneMealItem.applyBonemeal(stack, world, blockpos, FakePlayerFactory.getMinecraft((ServerWorld) world));
            }
        }
    }
    public static boolean applyBonemeal(World worldIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos);
        if (blockstate.getBlock() instanceof IGrowable) {
            IGrowable igrowable = (IGrowable)blockstate.getBlock();
            if (igrowable.canGrow(worldIn, pos, blockstate, worldIn.isRemote)) {
                if (!worldIn.isRemote && !World.isOutsideBuildHeight(pos)) {
                    if (igrowable.canUseBonemeal(worldIn, worldIn.rand, pos, blockstate)) {
                        igrowable.grow((ServerWorld)worldIn, worldIn.rand, pos, blockstate);
                        worldIn.notifyBlockUpdate(pos, blockstate, blockstate, 3);
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public int getManaCost() {
        return 70;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.BONE_BLOCK;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    protected String getBookDescription() {
        return "Causes plants to accelerate in growth, but this does not provide mana for nearby Mana Condensers.";
    }
}
