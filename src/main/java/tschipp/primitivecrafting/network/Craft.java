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
import tschipp.primitivecrafting.common.crafting.IPrimitiveRecipe;
import tschipp.primitivecrafting.common.crafting.RecipeRegistry;

public class Craft implements IMessage, IMessageHandler<Craft, IMessage>
{

	public int slot;
	public IPrimitiveRecipe recipe;
	
	public Craft()
	{
	}
	
	public Craft(int slot, IPrimitiveRecipe recipe)
	{
		this.slot = slot;
		this.recipe = recipe;
	}

	@Override
	public IMessage onMessage(Craft message, MessageContext ctx)
	{
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;

		mainThread.addScheduledTask(new Runnable()
		{
			EntityPlayerMP player = ctx.getServerHandler().player;

			@Override
			public void run()
			{
				ItemStack held = player.inventory.getItemStack();
				ItemStack under = player.inventory.getStackInSlot(message.slot);

				message.recipe.craft(held, under, player);
			}
		});

		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int hash = buf.readInt();
		this.recipe = RecipeRegistry.getFromHash(hash);
		this.slot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(recipe.hashCode());
		buf.writeInt(slot);
	}

}
