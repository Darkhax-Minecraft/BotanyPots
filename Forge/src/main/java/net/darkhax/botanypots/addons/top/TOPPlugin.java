package net.darkhax.botanypots.addons.top;

import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.block.inv.BotanyPotContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.checkerframework.checker.units.qual.C;

import java.util.function.Function;

public class TOPPlugin implements IProbeInfoProvider, Function<ITheOneProbe, Void> {

    private static final ResourceLocation PLUGIN_ID = new ResourceLocation(Constants.MOD_ID, "top_pot_info");
    private static final Component HARVESTABLE = new TranslatableComponent("tooltip.botanypots.harvestable").withStyle(ChatFormatting.GREEN);

    @Override
    public ResourceLocation getID() {

        return PLUGIN_ID;
    }

    @Override
    public Void apply(ITheOneProbe probeMod) {

        probeMod.registerProvider(this);
        return null;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player player, Level level, BlockState state, IProbeHitData data) {

        if (level.getBlockEntity(data.getPos()) instanceof BlockEntityBotanyPot pot) {

            final BotanyPotContainer inv = pot.getInventory();

            if (!inv.getSoilStack().isEmpty()) {

                final IProbeInfo soilInfo = info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                soilInfo.item(inv.getSoilStack());
                soilInfo.mcText(new TranslatableComponent("tooltip.botanypots.soil", inv.getSoilStack().getHoverName()));
            }

            if (!inv.getCropStack().isEmpty()) {

                final IProbeInfo seedInfo = info.horizontal(info.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                seedInfo.item(inv.getCropStack());
                seedInfo.mcText(new TranslatableComponent("tooltip.botanypots.seed", inv.getCropStack().getHoverName()));
            }

            if (pot.isGrowing()) {

                final int currentTime = pot.getGrowthTime();
                final int requiredTime = inv.getRequiredGrowthTime();
                final int remainingTime = requiredTime - currentTime;

                if (remainingTime > 0) {

                    info.mcText(new TranslatableComponent("tooltip.botanypots.time", StringUtil.formatTickDuration(requiredTime - currentTime)));

                    final ProgressStyle progressStyle = new ProgressStyle();
                    progressStyle.filledColor(0xff32CD32);
                    progressStyle.alternateFilledColor(0xff259925);
                    progressStyle.prefix("tooltip.botanypots.progress");
                    progressStyle.suffix("%");

                    final int f = Mth.floor(((float) currentTime / requiredTime) * 100f);
                    info.progress(f, 100, progressStyle);
                }

                else {

                    info.mcText(HARVESTABLE);
                }
            }
        }
    }
}
