package net.darkhax.botanypots.data.displaystate.types;

import net.minecraft.util.Mth;

import java.util.List;

public abstract class PhasedDisplayState extends DisplayState {

    public abstract List<DisplayState> getDisplayPhases();

    public DisplayState getPhase(float progress) {

        final int phaseIndex = Math.min(Mth.floor(this.getDisplayPhases().size() * progress), this.getDisplayPhases().size() - 1);
        return this.getDisplayPhases().get(phaseIndex);
    }
}