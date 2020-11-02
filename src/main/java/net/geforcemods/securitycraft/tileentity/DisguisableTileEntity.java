package net.geforcemods.securitycraft.tileentity;

//import net.fabricmc.api.EnvType;
//import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.misc.ModuleType;
//import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
//import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
//import net.minecraftforge.client.model.ModelDataManager;
//import net.minecraftforge.client.model.data.IModelData;
//import net.minecraftforge.client.model.data.ModelDataMap;
//import net.minecraftforge.fml.DistExecutor;
//import net.minecraftforge.fml.network.PacketDistributor;

public class DisguisableTileEntity extends CustomizableTileEntity
{
	public DisguisableTileEntity(BlockEntityType<?> type)
	{
		super(type);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		super.onModuleInserted(stack, module);

//		if(!world.isClient && module == ModuleType.DISGUISE) // TODO
//			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new RefreshDisguisableModel(pos, true, stack));
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		super.onModuleRemoved(stack, module);

//		if(!world.isClient && module == ModuleType.DISGUISE) // TODO
//			SecurityCraft.channel.send(PacketDistributor.ALL.noArg(), new RefreshDisguisableModel(pos, false, stack));
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{ModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return null;
	}

//	@Override // Forge method
//	public IModelData getModelData()
//	{
//		return new ModelDataMap.Builder().withInitial(DisguisableDynamicBakedModel.DISGUISED_BLOCK_RL, getCachedState().getBlock().getRegistryName()).build();
//	}
//
//	@Override // Forge method
//	public void onLoad()
//	{
//		super.onLoad();
//
//		if(world != null && world.isClient)
//			refreshModel();
//	}
//
//	public void refreshModel()
//	{ // TODO
//		DistExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
//			ModelDataManager.requestModelDataRefresh(this);
//			MinecraftClient.getInstance().worldRenderer.scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
//		});
//	}
}
