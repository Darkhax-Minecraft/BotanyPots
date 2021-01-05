import mods.botanypots.ZenCrop;
val crops = <recipetype:botanypots:crop>;

// Simple crop entry
// crops.create(id, seedInput, renderBlock, growthTicks, soilCategory);
val goldCrop = crops.create("examplepack:gold", <item:minecraft:gold_nugget>, <blockstate:minecraft:gold_block>, 3000, "dirt");

// Crop with multiple render blocks and multiple soil categories.
// crops.create(id, seedInput, renderBlockArray, growthTicks, soilCategoryArray);
val ironCrop = crops.create("examplepack:iron", <item:minecraft:iron_nugget>, [<blockstate:minecraft:iron_block>, <blockstate:minecraft:iron_ore>], 3000, ["dirt", "nether"]);


val wheat = crops.getCrop("botanypots:crops/wheat");

// Adds a soil category.
wheat.addCategory("stone");

// Removes a soil category.
wheat.removeCategory("dirt");

// Removes all soil categories.
//wheat.clearCategories();

// Adding new drop entries.
// addDrop(item, chance);
wheat.addDrop(<item:minecraft:gold_nugget>, 1); // 100% drop rate
wheat.addDrop(<item:minecraft:gold_ingot>, 0.75); // 75% drop rate

// addDrop(item, chance, rolls);
wheat.addDrop(<item:minecraft:iron_nugget>, 1, 4); // 100% drop rate AND 4 attempts.

// addDrop(item, chance, minRolls, maxRolls);
wheat.addDrop(<item:minecraft:iron_ingot>, 0.75, 1, 5); // 75% drop rate AND 1 to 5 attempts.

// Removes all drops.
//wheat.clearDrops();

// Removes a drop.
// removeDrop(ingredientToRemove);
wheat.removeDrop(<tag:items:forge:seeds>);

// Set the growth ticks of the crop.
wheat.setGrowthTicks(10000);

// Sets the seed item.
wheat.setSeed(<tag:items:forge:seeds/wheat>);

// Sets the display block.
wheat.setDisplay(<blockstate:minecraft:gold_block>);

// Sets the display to multiple blocks stacked.
wheat.setDisplay([<blockstate:minecraft:iron_block>, <blockstate:minecraft:iron_ore>]);