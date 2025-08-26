package net.darkhax.botanypots.impl;

import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

@Mod(BotanyPotsMod.MOD_ID)
public class NeoForgeMod {

    public NeoForgeMod(IEventBus bus) {
        BotanyPotsMod.init();
        bus.addListener(NeoForgeMod::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BotanyPotBlockEntity.TYPE.get(), (be, side) -> {
            if (side == Direction.DOWN) {
                return new SidedInvWrapper(be, Direction.DOWN);
            }
            return null;
        });
    }
}