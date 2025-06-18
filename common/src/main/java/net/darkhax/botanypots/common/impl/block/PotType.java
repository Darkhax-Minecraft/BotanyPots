package net.darkhax.botanypots.common.impl.block;

public enum PotType {

    /**
     * Can grow crops but can not auto harvest.
     */
    BASIC,

    /**
     * Grows crops and auto-harvests.
     */
    HOPPER,

    /**
     * Always renders as a fully grown crop. Does not tick or have any other logic.
     */
    WAXED
}
