package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import javax.annotation.Nullable;

public class AugmentAOE extends AbstractAugment {
    public AugmentAOE() {
        super(ModConfig.AugmentAOEID, "AOE");
    }

    @Override
    public int getManaCost() {
        return 40;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.FIREWORK_STAR;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    protected String getBookDescription() {
        return "Spells will affect a larger area around a targetted block.";
    }
}
