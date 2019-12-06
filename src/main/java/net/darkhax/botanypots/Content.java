package net.darkhax.botanypots;

import java.util.ArrayList;
import java.util.List;

import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.botanypots.block.BlockBotanyPot;
import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntityType;

public class Content {
    
    /**
     * The tile entity type for all botany pots. They share the same tile entity type. Keep in
     * mind that in modern versions of Minecraft a tile entity can only be associated with a
     * predefined list of blocks. If you want to make your own type of botany pot you will need
     * to hack it into the original type's list of blocks somehow, or make your own tile entity
     * type.
     */
    private final TileEntityType<TileEntityBotanyPot> tileBotanyPot;
    
    /**
     * The basic botany pot with no fancy colors or upgrades. This is primarily stored so it
     * can be referenced later on as an icon.
     */
    private final Block basicBotanyPotBlock;
    
    /**
     * A list of all the known botany pot blocks.
     */
    private final List<Block> botanyPots = new ArrayList<>();
    
    public Content(RegistryHelper registry) {
        
        // Normal Botany Pots
        this.basicBotanyPotBlock = registry.registerBlock(new BlockBotanyPot(), "botany_pot");
        this.botanyPots.add(this.basicBotanyPotBlock);
        
        for (final DyeColor dyeColor : DyeColor.values()) {
            
            this.botanyPots.add(registry.registerBlock(new BlockBotanyPot(), dyeColor.getName() + "_botany_pot"));
        }
        
        // Hopper Botany Pots
        this.botanyPots.add(registry.registerBlock(new BlockBotanyPot(true), "hopper_botany_pot"));
        
        for (final DyeColor dyeColor : DyeColor.values()) {
            
            this.botanyPots.add(registry.registerBlock(new BlockBotanyPot(true), "hopper_" + dyeColor.getName() + "_botany_pot"));
        }
        
        // Tile Entity
        this.tileBotanyPot = registry.registerTileEntity(TileEntityBotanyPot::new, "botany_pot", this.botanyPots.toArray(new Block[0]));
    }
    
    public TileEntityType<TileEntityBotanyPot> getPotTileType () {
        
        return this.tileBotanyPot;
    }
    
    public Block getBasicBotanyPot () {
        
        return this.basicBotanyPotBlock;
    }
}