package net.darkhax.botanypots.common.impl.config;

import net.darkhax.pricklemc.common.api.annotations.Value;

public class Recipes {

    @Value(comment = "Determines if basic botany pots may be crafted.")
    public boolean craft_basic_pots = true;

    @Value(comment = "Determines if hopper pots may be crafted.")
    public boolean craft_hopper_pots = true;

    @Value(comment = "Determines if wax pots may be crafted.")
    public boolean craft_wax_pots = true;
}
