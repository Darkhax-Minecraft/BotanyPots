package net.darkhax.botanypots.common.impl.data.itemdrops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import net.darkhax.botanypots.common.impl.Helpers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BlockStateDrops extends BlockDrops {

    public static final MapCodec<BlockStateDrops> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MapCodecs.BLOCK_STATE_MAP_CODEC.fieldOf("block_state").forGetter(BlockStateDrops::getHarvestState)
    ).apply(instance, BlockStateDrops::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockStateDrops> STREAM = StreamCodec.of(
            (buf, val) -> {
                Helpers.BLOCK_STATE_STREAM.encode(buf, val.getHarvestState());
                buf.writeResourceLocation(val.getTableId());
                ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buf, val.getDisplayItems());
            },
            buf -> {
                final BlockState state = Helpers.BLOCK_STATE_STREAM.decode(buf);
                final ResourceLocation tableId = buf.readResourceLocation();
                final List<ItemStack> items = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buf);
                return new BlockStateDrops(state, tableId, items);
            }
    );

    // server
    public BlockStateDrops(BlockState state) {
        super(state.getBlock(), state);
    }

    // client
    public BlockStateDrops(BlockState state, ResourceLocation tableId, List<ItemStack> displayItems) {
        super(state.getBlock(), state, tableId, displayItems);
    }
}