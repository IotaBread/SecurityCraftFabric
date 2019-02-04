package net.geforcemods.securitycraft.network.packets;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCInitSentryAnimation implements IMessage
{
	public BlockPos pos;
	public boolean animate, animateUpwards;

	public PacketCInitSentryAnimation() {}

	public PacketCInitSentryAnimation(BlockPos sentryPos, boolean animate, boolean animateUpwards)
	{
		pos = sentryPos;
		this.animate = animate;
		this.animateUpwards = animateUpwards;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		animate = buf.readBoolean();
		animateUpwards = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeBoolean(animate);
		buf.writeBoolean(animateUpwards);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCInitSentryAnimation, IMessage>
	{
		@Override
		public IMessage onMessage(PacketCInitSentryAnimation message, MessageContext ctx)
		{
			List<EntityCreature> sentries = Minecraft.getMinecraft().thePlayer.worldObj.<EntityCreature>getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(message.pos, message.pos.add(1, 1, 1)));

			if(!sentries.isEmpty())
			{
				((EntitySentry)sentries.get(0)).animateUpwards = message.animateUpwards;
				((EntitySentry)sentries.get(0)).animate = message.animate;
			}

			return null;
		}
	}
}
