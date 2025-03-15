package org.shuangfa114.controllablerockets;

import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.utils.TooltipUtils;
import earth.terrarium.botarium.common.energy.base.BotariumEnergyItem;
import earth.terrarium.botarium.common.energy.impl.SimpleEnergyContainer;
import earth.terrarium.botarium.common.energy.impl.WrappedItemEnergyContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WeldingToolsItem extends Item implements BotariumEnergyItem<WrappedItemEnergyContainer> {

    public WeldingToolsItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public WrappedItemEnergyContainer getEnergyStorage(ItemStack itemStack) {
        return new WrappedItemEnergyContainer(itemStack, new SimpleEnergyContainer(2500L, 20L, 20L));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        WrappedItemEnergyContainer energy = this.getEnergyStorage(pStack);
        pTooltipComponents.add(TooltipUtils.getEnergyComponent(energy.getStoredEnergy(), energy.getMaxCapacity()));
        pTooltipComponents.add(TooltipUtils.getMaxEnergyInComponent(energy.maxInsert()));
        TooltipUtils.addDescriptionComponent(pTooltipComponents, ConstantComponents.ENERGIZER_INFO);
    }

    public boolean isBarVisible(@NotNull ItemStack stack) {
        return this.getEnergyStorage(stack).getStoredEnergy() > 0L;
    }

    public int getBarWidth(@NotNull ItemStack stack) {
        WrappedItemEnergyContainer energyStorage = this.getEnergyStorage(stack);
        return (int) ((double) energyStorage.getStoredEnergy() / (double) energyStorage.getMaxCapacity() * (double) 13.0F);
    }

    public int getBarColor(@NotNull ItemStack stack) {
        return 6544578;
    }
}
