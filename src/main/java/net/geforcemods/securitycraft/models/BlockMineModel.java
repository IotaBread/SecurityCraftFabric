package net.geforcemods.securitycraft.models;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
//import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
//import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
//import net.minecraftforge.client.ForgeHooksClient;

import java.util.List;
import java.util.Random;

public class BlockMineModel implements BakedModel
{
	private final BakedModel defaultModel;
	private final BakedModel guiModel;

	public BlockMineModel(BakedModel defaultModel, BakedModel guiModel)
	{
		this.defaultModel = defaultModel;
		this.guiModel = guiModel;
	}

//	@Override // Forge method
//	public boolean doesHandlePerspectives()
//	{
//		return true;
//	}
//
//	@Override // Forge method
//	public BakedModel handlePerspective(Mode cameraTransformType, MatrixStack matrix)
//	{
//		if(cameraTransformType == Mode.GUI)
//			return ForgeHooksClient.handlePerspective(guiModel, cameraTransformType, matrix);
//		else
//			return ForgeHooksClient.handlePerspective(defaultModel, cameraTransformType, matrix);
//	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand)
	{
		return defaultModel.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion()
	{
		return defaultModel.useAmbientOcclusion();
	}

	@Override
	public boolean hasDepth()
	{
		return defaultModel.hasDepth();
	}

	@Override
	public boolean isSideLit()
	{
		return defaultModel.isSideLit();
	}

	@Override
	public boolean isBuiltin()
	{
		return defaultModel.isBuiltin();
	}

	@Override
	public Sprite getSprite()
	{
		return defaultModel.getSprite();
	}

	@Override
	public ModelTransformation getTransformation() {
		return null; // TODO
	}

	@Override
	public ModelOverrideList getOverrides()
	{
		return defaultModel.getOverrides();
	}
}
