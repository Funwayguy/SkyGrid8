package skygrid8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fml.common.IWorldGenerator;
import skygrid8.core.SG_Settings;

public class PostGenerator implements IWorldGenerator
{
	public static HashMap<Integer,ArrayList<BlockPos>> tileLoc = new HashMap<Integer,ArrayList<BlockPos>>();
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		ArrayList<BlockPos> list = tileLoc.remove(world.provider.getDimensionId());
		
		if(list == null)
		{
			return;
		}
		
		for(BlockPos pos : list)
		{
			TileEntity tile = world.getTileEntity(pos);
			
			if(tile == null)
			{
				continue;
			} else if(tile instanceof IInventory)
			{
				IInventory invo = (IInventory)tile;
				
				if(invo.getSizeInventory() > 0)
				{
					int amount = random.nextInt(Math.min(9, 1 + invo.getSizeInventory()));
					WeightedRandomChestContent.generateChestContents(random, ChestGenHooks.getItems(SG_Settings.lootChests.get(random.nextInt(SG_Settings.lootChests.size())), random), invo, amount);
				}
			} else if(tile instanceof TileEntityMobSpawner)
			{
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
				ArrayList<String> entities = world.provider.getDimensionId() == -1? SG_Settings.spawnN : SG_Settings.spawnO;
				
				if(entities.size() > 0)
				{
					spawner.getSpawnerBaseLogic().setEntityName(entities.get(random.nextInt(entities.size())));
				}
			}
		}
	}
	
	public static void addLocation(int dimension, BlockPos pos)
	{
		ArrayList<BlockPos> list = tileLoc.get(dimension);
		list = list != null? list : new ArrayList<BlockPos>();
		
		if(!list.contains(pos))
		{
			list.add(pos);
			tileLoc.put(dimension, list);
		}
	}
	
	public static void resetLocations()
	{
		tileLoc = new HashMap<Integer,ArrayList<BlockPos>>();
	}
}
