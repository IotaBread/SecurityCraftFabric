package net.geforcemods.securitycraft.renderers;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class EmptyRenderer<T extends Entity> extends EntityRenderer<T>
{
	public EmptyRenderer(EntityRenderDispatcher renderManager)
	{
		super(renderManager);
	}

	@Override
	public boolean shouldRender(T entity, Frustum camera, double camX, double camY, double camZ)
	{
		return false;
	}

	@Override
	public Identifier getTexture(T entity)
	{
		return null;
	}
}