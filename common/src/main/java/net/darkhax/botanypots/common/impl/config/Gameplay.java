package net.darkhax.botanypots.common.impl.config;

import net.darkhax.pricklemc.common.api.annotations.Value;

public class Gameplay {

    @Value(comment = "A global modifier applied to the growth times of all crops. Increasing this value will make crops grow faster.")
    public float global_growth_modifier = 1f;

    @Value(comment = "Determines if the item in the harvest tool slot should take durability damage.")
    public boolean damage_harvest_tool = true;

    @Value(comment = "The growth modifier applied for each level of efficiency that the harvest tool has.")
    public float efficiency_growth_modifier = 0.05f;
}