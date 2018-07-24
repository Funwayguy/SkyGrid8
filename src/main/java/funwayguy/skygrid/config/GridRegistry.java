package funwayguy.skygrid.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import funwayguy.skygrid.core.SkyGrid;
import funwayguy.skygrid.util.JsonHelper;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.*;

public class GridRegistry
{
	public static final List<GridBlock> blocksOverworld = new ArrayList<>();
	public static final List<GridBlock> blocksNether = new ArrayList<>();
	public static final List<GridBlock> blocksEnd = new ArrayList<>();
	public static final List<GridBlock> blocksAbyssalWasteland = new ArrayList<>();
	public static final List<GridBlock> blocksDreadlands = new ArrayList<>();
	public static final List<GridBlock> blocksOmothol = new ArrayList<>();
	public static final List<GridBlock> blocksDarkRealm = new ArrayList<>();
	
	public static final Map<String, GridCache> randomCache = new HashMap<>();
	
	public static class GridCache
	{
		private final List<GridBlock> list;
		private final int weight;
		
		private GridCache(List<GridBlock> list, int weight)
		{
			this.list = list;
			this.weight = weight;
		}
	}

	public static List<GridBlock> getRandom(Random rand, List<GridBlock> list, Biome biome, int dimension, int amount)
	{
		String key = dimension + (biome == null ? "" : "," + Biome.getIdForBiome(biome));
		GridCache cache = randomCache.get(key);
		
		if(cache != null)
		{
			return getRandom(rand, cache, amount);
		}
		
		List<GridBlock> tmp = list;
		
		if(biome != null)
		{
			tmp = new ArrayList<>();
			int biomeID = Biome.getIdForBiome(biome);
			
			for(GridBlock gb : list)
			{
				if(gb == null)
				{
					continue;
				}
				
				if(gb.biomes == null || gb.biomes.size() <= 0 || gb.biomes.contains(biomeID))
				{
					tmp.add(gb);
				}
			}
		}
		
		cache = new GridCache(tmp, getTotalWeight(tmp));
		randomCache.put(key, cache);
		
		return getRandom(rand, cache, amount);
	}
	
	private static GridBlock fallback = new GridBlock(Blocks.BEDROCK);
	
	private static List<GridBlock> getRandom(Random rand, GridCache cache, int amount)
	{
		List<Float> picks = new ArrayList<>();
		
		for(int i = 0; i < amount; i++)
		{
			picks.add(rand.nextFloat() * cache.weight);
		}
		
		picks.sort(Comparator.naturalOrder());
		
		List<GridBlock> blocks = new ArrayList<>();
		int cnt = 0;
		int n = 0;
		
		for(GridBlock entry : cache.list)
		{
			if(n >= amount)
			{
				break;
			}
			
			cnt += entry.weight;
			
			while(n < amount && cnt >= picks.get(n))
			{
				blocks.add(entry);
				n++;
			}
		}
		
		if(blocks.size() <= 0)
		{
			blocks.add(fallback);
		}
		
		Collections.shuffle(blocks, rand);
		
		return blocks;
	}
	
	private static int getTotalWeight(List<GridBlock> list)
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
		randomCache.clear();
		
		final String preFix = "config/skygrid/";
		
		File f = new File(preFix + "overworld.json");
		blocksOverworld.clear();
		
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
		SkyGrid.logger.log(Level.INFO, "Loaded " + blocksOverworld.size() + " Overworld grid blocks");
		
		f = new File(preFix + "nether.json");
		blocksNether.clear();
		
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
		
		SkyGrid.logger.log(Level.INFO, "Loaded " + blocksNether.size() + " Nether grid blocks");
		
		f = new File(preFix + "end.json");
		blocksEnd.clear();
		
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
		
		SkyGrid.logger.log(Level.INFO, "Loaded " + blocksEnd.size() + " End grid blocks");

		if(Loader.isModLoaded("abyssalcraft")){
			f = new File(preFix + "abyssal_wasteland.json");
			blocksAbyssalWasteland.clear();
			
			if(!f.exists())
			{
				generateDefaults(f, blocksAbyssalWasteland);
			} else
			{
				JsonArray list = JsonHelper.ReadArrayFromFile(f);
				for(JsonElement e : list)
				{
					if(e == null || !e.isJsonObject())
					{
						continue;
					}
					
					blocksAbyssalWasteland.add(new GridBlock(e.getAsJsonObject()));
				}
			}
			
			SkyGrid.logger.log(Level.INFO, "Loaded " + blocksAbyssalWasteland.size() + " Abyssal Wasteland grid blocks");
			
			f = new File(preFix + "dreadlands.json");
			blocksDreadlands.clear();
			
			if(!f.exists())
			{
				generateDefaults(f, blocksDreadlands);
			} else
			{
				JsonArray list = JsonHelper.ReadArrayFromFile(f);
				for(JsonElement e : list)
				{
					if(e == null || !e.isJsonObject())
					{
						continue;
					}
					
					blocksDreadlands.add(new GridBlock(e.getAsJsonObject()));
				}
			}
			
			SkyGrid.logger.log(Level.INFO, "Loaded " + blocksDreadlands.size() + " Dreadlands grid blocks");
			
			f = new File(preFix + "omothol.json");
			blocksOmothol.clear();
			
			if(!f.exists())
			{
				generateDefaults(f, blocksOmothol);
			} else
			{
				JsonArray list = JsonHelper.ReadArrayFromFile(f);
				for(JsonElement e : list)
				{
					if(e == null || !e.isJsonObject())
					{
						continue;
					}
					
					blocksOmothol.add(new GridBlock(e.getAsJsonObject()));
				}
			}
			
			SkyGrid.logger.log(Level.INFO, "Loaded " + blocksOmothol.size() + " Omothol grid blocks");
			
			f = new File(preFix + "dark_realm.json");
			blocksDarkRealm.clear();
			
			if(!f.exists())
			{
				generateDefaults(f, blocksDarkRealm);
			} else
			{
				JsonArray list = JsonHelper.ReadArrayFromFile(f);
				for(JsonElement e : list)
				{
					if(e == null || !e.isJsonObject())
					{
						continue;
					}
					
					blocksDarkRealm.add(new GridBlock(e.getAsJsonObject()));
				}
			}
			
			SkyGrid.logger.log(Level.INFO, "Loaded " + blocksDarkRealm.size() + " Dark Realm grid blocks");
		}
	}
	
	private static List<GridBlock> defBlockList;
	private static JsonArray defBlockJson;
	
	public static void generateDefaults(File f, List<GridBlock> blocklist)
	{
		if(defBlockList != null)
		{
			blocklist.addAll(defBlockList);
			JsonHelper.WriteToFile(f, defBlockJson);
			return;
		}
		
		defBlockList = new ArrayList<>();
		
		GridBlock farmland = new GridBlock(Blocks.FARMLAND);
		GridBlock soulsand = new GridBlock(Blocks.SOUL_SAND);
		GridBlock plainsand = new GridBlock(Blocks.SAND);
		GridBlock grass = new GridBlock(Blocks.GRASS);
		
		JsonArray list = new JsonArray();
		for(Block b : Block.REGISTRY)
		{
			if(b == Blocks.GRASS || b == Blocks.SAND || b == Blocks.SOUL_SAND || b == Blocks.FARMLAND)
			{
				continue; // Wait till all the plants are added before saving
			} else if(b instanceof BlockLiquid || b instanceof BlockFluidBase || b instanceof BlockIce || (!b.getDefaultState().isFullCube() && b != Blocks.CHEST))
			{
				continue;
			} else if(b instanceof BlockCrops || b instanceof BlockStem)
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
				continue;
			}
			
			GridBlock tmp = new GridBlock(b);
			list.add(tmp.writeToJson(new JsonObject()));
			defBlockList.add(tmp);
		}
		
		list.add(farmland.writeToJson(new JsonObject()));
		list.add(soulsand.writeToJson(new JsonObject()));
		list.add(plainsand.writeToJson(new JsonObject()));
		list.add(grass.writeToJson(new JsonObject()));
		defBlockList.add(farmland);
		defBlockList.add(soulsand);
		defBlockList.add(plainsand);
		defBlockList.add(grass);
		
		defBlockJson = list;
		blocklist.addAll(defBlockList);
		
		JsonHelper.WriteToFile(f, defBlockJson);
	}
}
