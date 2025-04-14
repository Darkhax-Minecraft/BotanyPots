package net.darkhax.botanypots.common.api.data.display.types;

/**
 * Holds information about a displayable object.
 */
public interface Display {

    /**
     * The type of the display. This is used to handle serialization and binding a display to a renderer.
     *
     * @return The type of the display.
     */
    DisplayType<?> getType();
}