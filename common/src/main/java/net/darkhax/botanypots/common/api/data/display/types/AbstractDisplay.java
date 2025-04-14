package net.darkhax.botanypots.common.api.data.display.types;

/**
 * Provides the groundwork for a display that supports the built-in RenderOptions system. This system handles things
 * like scale, rotation, and offsets defined by the user.
 *
 * @param <T> The type of options used by the display.
 */
public abstract class AbstractDisplay<T extends RenderOptions> implements Display {

    private final T renderOptions;

    public AbstractDisplay(T renderOptions) {
        this.renderOptions = renderOptions;
    }

    /**
     * Gets the render options to use for the display. These are usually defined by a user and used to configure how the
     * display is rendered.
     *
     * @return The options for the renderer.
     */
    public T renderOptions() {
        return this.renderOptions;
    }
}