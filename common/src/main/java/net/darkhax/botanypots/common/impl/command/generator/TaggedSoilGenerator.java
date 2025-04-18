package net.darkhax.botanypots.common.impl.command.generator;

import com.google.gson.JsonObject;
import net.darkhax.botanypots.common.api.command.generator.DataHelper;
import net.darkhax.botanypots.common.api.command.generator.soil.SoilGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * This soil generator will create soils that map items in a tag to a single display output.
 */
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
    public boolean canGenerateSoil(ServerLevel level, ItemStack stack) {
        return stack.is(this.tag);
    }

    @Override
    public JsonObject generateData(ServerLevel level, ItemStack stack) {
        final JsonObject output = new JsonObject();
        output.add("bookshelf:load_conditions", DataHelper.array(DataHelper.requiresItem(stack.getItem())));
        output.addProperty("type", "botanypots:soil");
        output.add("input", DataHelper.item(stack.getItem()));
        output.add("display", this.display);
        return output;
    }
}