package net.darkhax.botanypots.common.api.command.generator.soil;

import com.google.gson.JsonObject;
import net.darkhax.botanypots.common.api.command.generator.DataHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class TaggedSoilGenerator implements SoilGenerator {

    private final TagKey<Item> tag;
    private final JsonObject display;

    public TaggedSoilGenerator(String tag, JsonObject display) {
        this(TagKey.create(Registries.ITEM, Objects.requireNonNull(ResourceLocation.tryParse(tag))), display);
    }

    public TaggedSoilGenerator(TagKey<Item> tag, JsonObject display) {
        this.tag = tag;
        this.display = display;
    }

    @Override
    public boolean canGenerateSoil(Level level, ItemStack stack) {
        return stack.is(this.tag);
    }

    @Override
    public JsonObject generateData(Level level, ItemStack stack) {
        final JsonObject output = new JsonObject();
        output.add("bookshelf:load_conditions", DataHelper.array(DataHelper.requiresItem(stack.getItem())));
        output.addProperty("type", "botanypots:soil");
        output.add("input", DataHelper.item(stack.getItem()));
        output.add("display", this.display);
        return output;
    }
}