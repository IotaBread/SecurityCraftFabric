package net.geforcemods.securitycraft.items;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class FakeLiquidBucketItem extends BucketItem
{
	public FakeLiquidBucketItem(Supplier<? extends Fluid> supplier, Settings builder)
	{
		super(supplier.get(), builder);

		DispenserBlock.registerBehavior(this, new ItemDispenserBehavior() {
			private final ItemDispenserBehavior instance = new ItemDispenserBehavior();

			@Override
			public ItemStack dispenseSilently(BlockPointer source, ItemStack stack)
			{
				BucketItem bucket = (BucketItem)stack.getItem();
				BlockPos pos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
				World world = source.getWorld();

				if(bucket.placeFluid(null, world, pos, null))
				{
					bucket.onEmptied(world, stack, pos);
					return new ItemStack(Items.BUCKET);
				}
				else return instance.dispense(source, stack);
			}
		});
	}
}
