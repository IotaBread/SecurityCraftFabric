package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
//import net.geforcemods.securitycraft.tileentity.BlockPocketTileEntity;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.minecraft.block.Block;

import java.util.function.Supplier;

public class BlockPocketBlock extends BaseReinforcedBlock implements IBlockPocket
{
	public BlockPocketBlock(Settings settings, Supplier<Block> vB)
	{
		super(settings, vB);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world)
//	{
//		return new BlockPocketTileEntity();
//	}
}
