package net.geforcemods.securitycraft.blocks.reinforced;

//import net.geforcemods.securitycraft.tileentity.BlockPocketTileEntity;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.world.BlockView;

import java.util.function.Supplier;

public class ReinforcedRotatedCrystalQuartzPillar extends ReinforcedRotatedPillarBlock implements IBlockPocket
{
	public ReinforcedRotatedCrystalQuartzPillar(Settings settings, Supplier<Block> vB)
	{
		super(settings, vB);
	}

//	@Override // Forge method
//	public BlockEntity createTileEntity(BlockState state, BlockView world)
//	{
//		return new BlockPocketTileEntity();
//	}
}
