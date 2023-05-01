package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.botanypots.data.displaystate.DisplayState;

public class BotanyPotsCommon {

    public static Content content;

    public BotanyPotsCommon() {

        DisplayState.init();
        this.content = new Content();
        Services.REGISTRIES.loadContent(content);
    }
}