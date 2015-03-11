package tmechworks.command;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class BlockInfoCommand extends CommandBase
{

    @Override
    public String getCommandName ()
    {
        return "tmech_blockInfo";
    }

    @Override
    public int getRequiredPermissionLevel ()
    {
        return 2;
    }

    @Override
    public String getCommandUsage (ICommandSender sender)
    {
        return "/tmech_blockInfo [Issue the command while the player is above the block]";
    }

    @Override
    public void processCommand (ICommandSender sender, String[] args)
    {
        World world = sender.getEntityWorld();
        if (world == null || !(sender instanceof EntityPlayer))
        {
            sender.addChatMessage(new ChatComponentText("Error: This command should be run from in game only"));
            return;
        }

        ChunkCoordinates coords = sender.getPlayerCoordinates();

        int blockY = coords.posY - 1;

        sender.addChatMessage(new ChatComponentText("Checking block in X: " + coords.posX + " Y: " + blockY + " Z: " + coords.posX));

        if (!world.blockExists(coords.posX, blockY, coords.posZ))
        {
            sender.addChatMessage(new ChatComponentText("The Block does not exists, check another location"));
            return;
        }

        Block block = world.getBlock(coords.posX, blockY, coords.posZ);

        if (block == null)
        {
            sender.addChatMessage(new ChatComponentText("The Block is null"));
            return;
        }

        if (block == Blocks.air)
        {
            sender.addChatMessage(new ChatComponentText("The Block is air"));
            return;
        }

        UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(block);

        if (identifier != null)
        {
            sender.addChatMessage(new ChatComponentText("Block: " + identifier.modId + ":" + identifier.name));
        }

        String blockName = block.getUnlocalizedName();
        sender.addChatMessage(new ChatComponentText("Block Unlocalizedname: " + blockName));

        int metadata = world.getBlockMetadata(coords.posX, blockY, coords.posZ);
        sender.addChatMessage(new ChatComponentText("Metadata: " + metadata));

        boolean hasTE = block.hasTileEntity(metadata);
        sender.addChatMessage(new ChatComponentText("Has Tile Entity: " + hasTE));

        if (!hasTE)
        {
            return;
        }

        TileEntity te = world.getTileEntity(coords.posX, blockY, coords.posZ);
        if (te == null)
        {
            sender.addChatMessage(new ChatComponentText("Tile Entity was not found"));
            return;
        }

        NBTTagCompound nbt = new NBTTagCompound();
        te.writeToNBT(nbt);

        sender.addChatMessage(new ChatComponentText("Tile Entity NBT: " + nbt.toString()));
    }

}
