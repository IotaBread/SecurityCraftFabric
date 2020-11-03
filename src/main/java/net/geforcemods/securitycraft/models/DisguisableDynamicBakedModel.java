package net.geforcemods.securitycraft.models;

//import net.geforcemods.securitycraft.blocks.DisguisableBlock;
//import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.render.model.BakedModel;
//import net.minecraft.client.render.model.BakedQuad;
//import net.minecraft.client.render.model.json.ModelOverrideList;
//import net.minecraft.client.texture.Sprite;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.world.BlockRenderView;
//import net.minecraftforge.client.model.data.IDynamicBakedModel;
//import net.minecraftforge.client.model.data.IModelData;
//import net.minecraftforge.client.model.data.ModelProperty;
//import net.minecraftforge.registries.ForgeRegistries;
//
//import javax.annotation.Nonnull;
//import java.util.List;
//import java.util.Random;

public class DisguisableDynamicBakedModel //implements IDynamicBakedModel // TODO
{
//	public static final ModelProperty<Identifier> DISGUISED_BLOCK_RL = new ModelProperty<>();
//	private final Identifier defaultStateRl;
//	private final BakedModel oldModel;
//
//	public DisguisableDynamicBakedModel(Identifier defaultStateRl, BakedModel oldModel)
//	{
//		this.defaultStateRl = defaultStateRl;
//		this.oldModel = oldModel;
//	}
//
//	@Override
//	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData modelData)
//	{
//		Identifier rl = modelData.getData(DISGUISED_BLOCK_RL);
//
//		if(rl != defaultStateRl)
//		{
//			Block block = ForgeRegistries.BLOCKS.getValue(rl);
//
//			if(block != null)
//			{
//				final BakedModel model = MinecraftClient.getInstance().getBlockRenderManager().getModel(block.getDefaultState());
//
//				if(model != null && model != this)
//					return model.getQuads(block.getDefaultState(), side, rand, modelData);
//			}
//		}
//
//		return oldModel.getQuads(state, side, rand, modelData);
//	}
//
//	@Override
//	public Sprite getSprite(IModelData modelData)
//	{
//		Identifier rl = modelData.getData(DISGUISED_BLOCK_RL);
//
//		if(rl != defaultStateRl)
//		{
//			Block block = ForgeRegistries.BLOCKS.getValue(rl);
//
//			if(block != null && !(block instanceof DisguisableBlock))
//				return MinecraftClient.getInstance().getBlockRenderManager().getModel(block.getDefaultState()).getParticleTexture(modelData);
//		}
//
//		return oldModel.getParticleTexture(modelData);
//	}
//
//	@Override
//	@Nonnull
//	public IModelData getModelData(BlockRenderView world, BlockPos pos, BlockState state, IModelData tileData)
//	{
//		BlockEntity te = world.getBlockEntity(pos);
//
//		if(te instanceof DisguisableTileEntity)
//		{
//			Block block = ((DisguisableTileEntity)te).getCachedState().getBlock();
//
//			if(block instanceof DisguisableBlock)
//			{
//				BlockState disguisedState = ((DisguisableBlock)block).getDisguisedBlockState(world, pos);
//
//				if(disguisedState != null)
//				{
//					tileData.setData(DISGUISED_BLOCK_RL, disguisedState.getBlock().getRegistryName());
//					return tileData;
//				}
//			}
//		}
//
//		tileData.setData(DISGUISED_BLOCK_RL, defaultStateRl);
//		return tileData;
//	}
//
//	@Override
//	public Sprite getSprite()
//	{
//		return oldModel.getSprite();
//	}
//
//	@Override
//	public boolean hasDepth()
//	{
//		return false;
//	}
//
//	@Override
//	public boolean isBuiltin()
//	{
//		return false;
//	}
//
//	@Override
//	public boolean useAmbientOcclusion()
//	{
//		return true;
//	}
//
//	@Override
//	public ModelOverrideList getOverrides()
//	{
//		return null;
//	}
//
//	@Override
//	public boolean isSideLit()
//	{
//		return false;
//	}
}
