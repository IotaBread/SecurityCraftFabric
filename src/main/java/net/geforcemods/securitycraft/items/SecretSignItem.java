package net.geforcemods.securitycraft.items;

//import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class SecretSignItem extends WallStandingBlockItem
{
	private final String translationKey;

	public SecretSignItem(Settings properties, Block floor, Block wall, String translationKey)
	{
		super(floor, wall, properties);

		this.translationKey = translationKey;
	}

	@Override
	public String getTranslationKey()
	{
		return translationKey;
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		return getTranslationKey();
	}

	@Override
	public boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
	{
		boolean flag = super.postPlacement(pos, world, player, stack, state);

		if(!flag && player != null)
		{
			SecretSignTileEntity te = (SecretSignTileEntity)world.getBlockEntity(pos);

			te.setEditor(player);

//			if(world.isClient) // TODO
//				SecurityCraft.proxy.displayEditSecretSignGui(te);
		}

		return flag;
	}
}
