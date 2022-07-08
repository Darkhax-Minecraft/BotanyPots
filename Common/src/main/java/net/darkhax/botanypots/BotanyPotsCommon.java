package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.botanypots.tempshelf.DisplayState;

public class BotanyPotsCommon {

    public final Content content;

    public BotanyPotsCommon() {

        DisplayState.init();
        this.content = new Content();
        Services.REGISTRIES.loadContent(content);
    }
}