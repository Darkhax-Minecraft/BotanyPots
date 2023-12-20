package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.Services;

public class BotanyPotsCommon {

    public static Content content;

    public BotanyPotsCommon() {

        content = new Content();
        Services.REGISTRIES.loadContent(content);
    }
}