package net.darkhax.botanypots.addons.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.addons.rei.ui.CropDisplay;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class CropDisplayCategory implements DisplayCategory<CropDisplay> {

    private final CategoryIdentifier<CropDisplay> id;
    private final Component localizedName;
    
    public CropDisplayCategory(CategoryIdentifier<CropDisplay> id) {

        this.id = id;
        this.localizedName = Component.translatable("gui.jei.category.botanypots.crop");
    }
    
    @Override
    public CategoryIdentifier<? extends CropDisplay> getCategoryIdentifier() {

        return this.id;
    }
    
    @Override
    public Component getTitle() {

        return this.localizedName;
    }
    
    @Override
    public Renderer getIcon() {

        return EntryStacks.of(BuiltInRegistries.ITEM.get(new ResourceLocation(Constants.MOD_ID, "terracotta_botany_pot")));
    }
    
    @Override
    public List<Widget> setupDisplay(CropDisplay display, Rectangle bounds) {

        return display.setupDisplay(bounds);
    }
}
