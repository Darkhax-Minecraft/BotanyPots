package net.darkhax.botanypots.addons.rei.ui;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.display.Display;

import java.util.List;

public interface CropDisplay extends Display {

    List<Widget> setupDisplay(Rectangle bounds);
}
