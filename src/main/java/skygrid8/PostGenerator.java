package skygrid8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import net.minecraft.entity.boss.EntityDragon;
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
	public static HashMap<String,ArrayList<BlockPos>> tileLoc = new HashMap<String,ArrayList<BlockPos>>();
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		String key = world.provider.getDimensionId() + ":" + chunkX + ":" + chunkZ;
		ArrayList<BlockPos> list = tileLoc.get(key);
		
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
				ArrayList<String> entities = new ArrayList<String>();
				
				switch(world.provider.getDimensionId())
				{
					case -1:
						entities = SG_Settings.spawnN;
						break;
					case 1:
						entities = SG_Settings.spawnE;
						break;
					default:
						entities = SG_Settings.spawnO;
						break;
				}
				
				if(entities.size() > 0)
				{
					spawner.getSpawnerBaseLogic().setEntityName(entities.get(random.nextInt(entities.size())));
				}
			}
		}
		
		list.clear();
		
		if(world.provider.getDimensionId() == 1 && chunkX == 0 && chunkZ == 0)
		{
			EntityDragon dragon = new EntityDragon(world);
			dragon.setPosition(0, SG_Settings.height + 16, 0);
			world.spawnEntityInWorld(dragon);
		}
	}
	
	public static void addLocation(int dimension, int chunkX, int chunkZ, BlockPos pos)
	{
		String key = dimension + ":" + chunkX + ":" + chunkZ;
		ArrayList<BlockPos> list = tileLoc.get(key);
		list = list != null? list : new ArrayList<BlockPos>();
		
		if(!list.contains(pos))
		{
			list.add(pos);
			tileLoc.put(key, list);
		}
	}
	
	public static void resetLocations()
	{
		tileLoc.clear();
	}
}
