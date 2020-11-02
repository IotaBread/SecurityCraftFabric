package net.geforcemods.securitycraft.blocks.reinforced;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import java.util.HashMap;
import java.util.Map;

public interface IReinforcedBlock
{
	public static final Map<Block,Block> VANILLA_TO_SECURITYCRAFT = new HashMap<>();
	public static final Map<Block,Block> SECURITYCRAFT_TO_VANILLA = new HashMap<>();

	public Block getVanillaBlock();

	public BlockState getConvertedState(BlockState vanillaState);
}
