package net.geforcemods.securitycraft.blocks;

//import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//import java.util.Arrays;
//import java.util.List;

public interface IPasswordConvertible
{
//	public static final List<Block> BLOCKS = Arrays.asList(new Block[] {
//			SCContent.KEYPAD.get(),
//			SCContent.KEYPAD_CHEST.get(),
//			SCContent.KEYPAD_FURNACE.get()
//	});

	public Block getOriginalBlock();

	public boolean convert(PlayerEntity player, World world, BlockPos pos);
}
