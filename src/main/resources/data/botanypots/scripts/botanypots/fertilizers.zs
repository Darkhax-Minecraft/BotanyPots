import mods.botanypots.ZenFertilizer;

val fertilizers = <recipetype:botanypots:fertilizer>;



// fertilizers.create(id, input, ticks);
// fertilizers.create(id, input, minTicks, maxTicks);
val stickFertilizer = fertilizers.create("examplepack:test", <item:minecraft:stick>, 1000);


val bonemeal = fertilizers.getFertilizer("botanypots:fertilizers/bone_meal");

// Set the input of the fertilizer.
// setInput(input);
bonemeal.setInput(<item:minecraft:stick>);

// Sets the growth tick amount.
// setGrowthAmount(ticks);
// setGrowthAmount(min, max);

bonemeal.setGrowthAmount(1200, 1500);