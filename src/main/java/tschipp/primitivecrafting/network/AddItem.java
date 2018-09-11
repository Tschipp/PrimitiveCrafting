package tschipp.primitivecrafting.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AddItem implements IMessage, IMessageHandler<AddItem, IMessage>
{
	public ItemStack stack;
	
	public AddItem()
	{
	}

	public AddItem(ItemStack stack)
	{
		this.stack = stack;
	}
	
	@Override
	public IMessage onMessage(AddItem message, MessageContext ctx)
	{
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;

		mainThread.addScheduledTask(new Runnable()
		{
			EntityPlayerMP player = ctx.getServerHandler().player;

			@Override
			public void run()
			{
				if (!player.inventory.addItemStackToInventory(message.stack))
					player.dropItem(message.stack, false);
			}
		});

		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeItemStack(buf, stack);
	}

}
