package net.geforcemods.securitycraft.items;

//import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
//import net.geforcemods.securitycraft.containers.BlockReinforcerContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
//import net.minecraft.screen.NamedScreenHandlerFactory;
//import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.Text;
//import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
//import net.minecraftforge.fml.network.NetworkHooks;

public class UniversalBlockReinforcerItem extends Item
{
	public UniversalBlockReinforcerItem(Settings properties)
	{
		super(properties);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		if(!world.isClient && player instanceof ServerPlayerEntity)
		{
//			NetworkHooks.openGui((ServerPlayerEntity)player, new NamedScreenHandlerFactory() { // TODO
//				@Override
//				public ScreenHandler createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
//				{
//					return new BlockReinforcerContainer(windowId, inv, UniversalBlockReinforcerItem.this == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1);
//				}
//
//				@Override
//				public Text getDisplayName()
//				{
//					return new TranslatableText(getTranslationKey());
//				}
//			}, data -> data.writeBoolean(this == SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1));
		}
		return super.use(world, player, hand);
	}

	@Override
	public boolean canMine(BlockState vanillaState, World world, BlockPos pos, PlayerEntity player) //gets rid of the stuttering experienced with onBlockStartBreak
	{
		if(!player.isCreative() && player.getMainHandStack().getItem() == this)
		{
			Block block = vanillaState.getBlock();
			Block rb = IReinforcedBlock.VANILLA_TO_SECURITYCRAFT.get(block);

			if(rb != null)
			{
				BlockState convertedState = ((IReinforcedBlock)rb).getConvertedState(vanillaState);
				BlockEntity te = world.getBlockEntity(pos);
				CompoundTag tag = te.toTag(new CompoundTag());

				if(te instanceof Inventory)
					((Inventory)te).clear();

				world.setBlockState(pos, convertedState);
				te = world.getBlockEntity(pos);
				te.fromTag(convertedState, tag);
				((IOwnable)te).getOwner().set(player.getGameProfile().getId().toString(), player.getName());
				player.getMainHandStack().damage(1, player, p -> p.sendToolBreakStatus(p.getActiveHand()));
				return false;
			}
		}

		return true;
	}
}
