package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.Services;

public class BotanyPotsCommon {

    public final Content content;

    public BotanyPotsCommon() {

        this.content = new Content();
        Services.REGISTRIES.loadContent(content);
    }
}