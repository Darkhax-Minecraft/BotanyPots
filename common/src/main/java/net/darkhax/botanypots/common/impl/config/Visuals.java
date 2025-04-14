package net.darkhax.botanypots.common.impl.config;

import net.darkhax.pricklemc.common.api.annotations.Value;

public class Visuals {

    @Value(comment = "Pots within this distance will render their soils/crops. Reducing the distance can improve performance in areas with many pots, but will cause pots in the distance to stop rendering.")
    public int pot_view_distance = 48;

    @Value(comment = "Disabling the growth animation will cause pots to always render at their largest size instead of scaling up as they grow. This is purely visual and benchmarks consistently show that disabling this option does not improve performance.")
    public boolean use_growth_animation = true;

    @Value(comment = "When soil rendering is disabled soils will not appear in botany pots. While not recommended, this can improve performance on low-end hardware and in extreme situations.")
    public boolean render_soil = true;

    @Value(comment = "When crop rendering is disabled crops will not appear in botany pots. While not recommended, this can improve performance on low-end hardware and in extreme situations.")
    public boolean render_crop = true;
}