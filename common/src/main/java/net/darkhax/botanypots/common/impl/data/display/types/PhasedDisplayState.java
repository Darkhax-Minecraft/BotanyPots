package net.darkhax.botanypots.common.impl.data.display.types;

import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.minecraft.util.Mth;

import java.util.List;

public abstract class PhasedDisplayState implements Display {

    public abstract List<Display> getDisplayPhases();

    public Display getPhase(float progress) {
        final int phaseIndex = Math.min(Mth.floor(this.getDisplayPhases().size() * progress), this.getDisplayPhases().size() - 1);
        return this.getDisplayPhases().get(phaseIndex);
    }
}