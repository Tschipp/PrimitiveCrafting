package tschipp.primitivecrafting.network;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tschipp.primitivecrafting.PrimitiveCrafting;
import tschipp.primitivecrafting.common.crafting.IPrimitiveRecipe;
import tschipp.primitivecrafting.common.crafting.RecipeRegistry;

public class Craft implements IMessage, IMessageHandler<Craft, IMessage>
{

	public ItemStack held;
	public int slot;
	public IPrimitiveRecipe recipe;
	
	public Craft()
	{
	}
	
	public Craft(ItemStack held, IPrimitiveRecipe recipe, int slot)
	{
		this.recipe = recipe;
		this.held = held;
		this.slot = slot;
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
				ItemStack held = player.isCreative() ? message.held : player.inventory.getItemStack();
				ItemStack under = player.inventory.getStackInSlot(message.slot);
				message.recipe.craft(held, under, player, held, message.slot);
			}
		});

		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.held = ByteBufUtils.readItemStack(buf);
		this.recipe = RecipeRegistry.getRecipe(new ResourceLocation(ByteBufUtils.readUTF8String(buf)));
		this.slot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeItemStack(buf, held);
		ByteBufUtils.writeUTF8String(buf, recipe.getRegistryName().toString());
		buf.writeInt(slot);
	}

}
