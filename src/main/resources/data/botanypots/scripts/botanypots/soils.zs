import mods.botanypots.ZenSoil;
val soils = <recipetype:botanypots:soil>;


// soils.create(id, input, renderBlock, growthModifier, category);
// Growth can be any value less than or equal to 1. Higher = faster, 0 = no change.
// category may also be a string array.
val stoneSoil = soils.create("examplepack:stone", <tag:items:forge:stone>, <blockstate:minecraft:stone>, 0.15, "stone");


val dirt = soils.getSoil("botanypots:soil/dirt");

// Adds a category.
// addCategory(category);
dirt.addCategory("test");

// Removes a category.
// removeCategory(category);
dirt.removeCategory("dirt");

// Removes all categories.
// clearCategories();
dirt.clearCategories();

// Sets the input.
// setInput(input);
dirt.setInput(<tag:items:forge:stone>);

// Set display block.
// setDisplay(state);
dirt.setDisplay(<blockstate:minecraft:stone>);

// Set growth modifier.
// setGrowthModifier(modifier);
dirt.setGrowthModifier(0.20);