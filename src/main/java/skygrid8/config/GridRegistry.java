package skygrid8.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockStem;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.IPlantable;
import org.apache.logging.log4j.Level;
import skygrid8.JsonHelper;
import skygrid8.core.SkyGrid8;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GridRegistry
{
	public static ArrayList<GridBlock> blocksOverworld = new ArrayList<GridBlock>();
	public static ArrayList<GridBlock> blocksNether = new ArrayList<GridBlock>();
	public static ArrayList<GridBlock> blocksEnd = new ArrayList<GridBlock>();
	
	public static GridBlock getRandom(Random rand, ArrayList<GridBlock> list)
	{
		int total = getTotalWeight(list);
		float r = rand.nextFloat() * total;
		int cnt = 0;
		
		for(GridBlock entry : list)
		{
			cnt += entry.weight;
			if(cnt >= r)
			{
				return entry;
			}
		}
		
		return new GridBlock(Blocks.bedrock);
	}
	
	public static int getTotalWeight(ArrayList<GridBlock> list)
	{
		int t = 0;
		
		for(GridBlock g : list)
		{
			t += g.weight;
		}
		
		return t;
	}
	
	public static void loadBlocks()
	{
		File f = new File("config/skygrid8_overworld.json");
		blocksOverworld = new ArrayList<GridBlock>();
		
		if(!f.exists())
		{
			generateDefaults(f, blocksOverworld);
		} else
		{
			JsonArray list = JsonHelper.ReadArrayFromFile(f);
			for(JsonElement e : list)
			{
				if(e == null || !e.isJsonObject())
				{
					continue;
				}
				
				blocksOverworld.add(new GridBlock(e.getAsJsonObject()));
			}
		}
		SkyGrid8.logger.log(Level.INFO, "Loaded " + blocksOverworld.size() + " Overworld grid blocks");
		
		f = new File("config/skygrid8_nether.json");
		blocksNether = new ArrayList<GridBlock>();
		
		if(!f.exists())
		{
			generateDefaults(f, blocksNether);
		} else
		{
			JsonArray list = JsonHelper.ReadArrayFromFile(f);
			for(JsonElement e : list)
			{
				if(e == null || !e.isJsonObject())
				{
					continue;
				}
				
				blocksNether.add(new GridBlock(e.getAsJsonObject()));
			}
		}
		
		SkyGrid8.logger.log(Level.INFO, "Loaded " + blocksNether.size() + " Nether grid blocks");
		
		f = new File("config/skygrid8_end.json");
		blocksEnd = new ArrayList<GridBlock>();
		
		if(!f.exists())
		{
			generateDefaults(f, blocksEnd);
		} else
		{
			JsonArray list = JsonHelper.ReadArrayFromFile(f);
			for(JsonElement e : list)
			{
				if(e == null || !e.isJsonObject())
				{
					continue;
				}
				
				blocksEnd.add(new GridBlock(e.getAsJsonObject()));
			}
		}
		
		SkyGrid8.logger.log(Level.INFO, "Loaded " + blocksEnd.size() + " End grid blocks");
	}
	
	public static void saveBlocks()
	{
		JsonArray oList = new JsonArray();
		for(GridBlock g : blocksOverworld)
		{
			JsonObject j = new JsonObject();
			g.writeToJson(j);
			oList.add(j);
		}
		JsonHelper.WriteToFile(new File("config/skygrid8_overworld.json"), oList);
		
		JsonArray nList = new JsonArray();
		for(GridBlock g : blocksNether)
		{
			JsonObject j = new JsonObject();
			g.writeToJson(j);
			nList.add(j);
		}
		JsonHelper.WriteToFile(new File("config/skygrid8_nether.json"), nList);
		
		JsonArray eList = new JsonArray();
		for(GridBlock g : blocksEnd)
		{
			JsonObject j = new JsonObject();
			g.writeToJson(j);
			eList.add(j);
		}
		JsonHelper.WriteToFile(new File("config/skygrid8_end.json"), eList);
	}
	
	public static void generateDefaults(File f, ArrayList<GridBlock> blockList)
	{
		GridBlock farmland = new GridBlock(Blocks.farmland);
		GridBlock soulsand = new GridBlock(Blocks.soul_sand);
		GridBlock plainsand = new GridBlock(Blocks.sand);
		GridBlock grass = new GridBlock(Blocks.grass);
		
		JsonArray list = new JsonArray();
		for(Block b : Block.blockRegistry)
		{
			if(b == Blocks.grass)
			{
				grass = new GridBlock(b);
				blockList.add(grass);
				continue;
			} else if(b == Blocks.sand)
			{
				plainsand = new GridBlock(b);
				blockList.add(plainsand);
				continue;
			} else if(b == Blocks.soul_sand)
			{
				soulsand = new GridBlock(b); // Written to list when plants have been added
				blockList.add(soulsand);
				continue;
			} else if(b == Blocks.farmland)
			{
				farmland = new GridBlock(b); // Written to list when plants have been added
				blockList.add(farmland);
				continue;
			} else if(b instanceof BlockDynamicLiquid || (!b.isFullCube() && b != Blocks.chest))
			{
				continue;
			}

			JsonObject jBlk = new JsonObject();
			GridBlock tmp = new GridBlock(b);
			tmp.writeToJson(jBlk);
			list.add(jBlk);
			blockList.add(tmp);
		}
		
		for(Block b : Block.blockRegistry)
		{
			if(b instanceof BlockCrops || b instanceof BlockStem)
			{
				farmland.addPlant(b);
				continue;
			} else if(b instanceof BlockNetherWart)
			{
				soulsand.addPlant(b);
				continue;
			} else if(b instanceof BlockCactus || b instanceof BlockReed)
			{
				plainsand.addPlant(b);
				continue;
			} else if(b instanceof IPlantable)
			{
				grass.addPlant(b);
			}
		}
		
		JsonObject jBlk = new JsonObject();
		farmland.writeToJson(jBlk);
		list.add(jBlk);
		jBlk = new JsonObject();
		soulsand.writeToJson(jBlk);
		list.add(jBlk);
		jBlk = new JsonObject();
		plainsand.writeToJson(jBlk);
		list.add(jBlk);
		jBlk = new JsonObject();
		grass.writeToJson(jBlk);
		list.add(jBlk);
		
		JsonHelper.WriteToFile(f, list);
	}
}
