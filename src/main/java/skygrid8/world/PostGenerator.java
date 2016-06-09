package skygrid8.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Loader;
import skygrid8.compat.abyssalcraft.SGACPlugin;
import skygrid8.core.SG_Settings;
import skygrid8.util.CustomLootTableManager;

public class PostGenerator implements IWorldGenerator
{
	public static HashMap<String,ArrayList<BlockPos>> tileLoc = new HashMap<String,ArrayList<BlockPos>>();
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
	{
		String key = world.provider.getDimension() + ":" + chunkX + ":" + chunkZ;
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
			} else if(tile instanceof TileEntityLockableLoot)
			{
				TileEntityLockableLoot invo = (TileEntityLockableLoot)tile;
				ResourceLocation lootRes = CustomLootTableManager.getTable(world, pos, random);
				invo.setLootTable(lootRes, random.nextLong());
			} else if(tile instanceof TileEntityMobSpawner)
			{
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
				ArrayList<String> entities = new ArrayList<String>();
				
				switch(world.provider.getDimension())
				{
					case -1:
						entities = SG_Settings.spawnN;
						break;
					case 1:
						entities = SG_Settings.spawnE;
						break;
					default:
						if(Loader.isModLoaded("abyssalcraft"))
							entities = SGACPlugin.assignList(world.provider.getDimension());
						else entities = SG_Settings.spawnO;
						break;
				}
				
				if(entities.size() > 0)
				{
					spawner.getSpawnerBaseLogic().setEntityName(entities.get(random.nextInt(entities.size())));
				}
			}
		}
		
		list.clear();
		
		if(world.provider.getDimension() == 1 && chunkX == 0 && chunkZ == 0)
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
