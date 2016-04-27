package skygrid8.config;

import java.util.ArrayList;
import skygrid8.JsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

public class GridBlock
{
	public int weight = 10;
	String name = "minecraft:stone";
	int meta = 0;
	private IBlockState block;
	
	public ArrayList<GridPlant> plants = new ArrayList<GridPlant>();
	public ArrayList<Integer> biomes = new ArrayList<Integer>();
	
	public GridBlock(JsonObject json)
	{
		this.readFromJson(json);
	}
	
	public GridBlock(IBlockState state)
	{
		this.block = state;
	}
	
	public GridBlock(Block b)
	{
		this(b.getDefaultState());
	}
	
	public GridBlock(Block b, int meta)
	{
		this(b.getStateFromMeta(meta));
	}
	
	public GridBlock(String name, int meta)
	{
		this.name = name;
		this.meta = meta;
	}
	
	public void addPlant(IBlockState state)
	{
		plants.add(new GridPlant(state));
	}
	
	public void addPlant(Block b)
	{
		addPlant(b.getDefaultState());
	}
	
	public void addPlant(Block b, int meta)
	{
		addPlant(b.getStateFromMeta(meta));
	}
	
	public void addPlant(String name, int meta)
	{
		plants.add(new GridPlant(name, meta));
	}
	
	public boolean hasPlants()
	{
		return plants != null && plants.size() > 0;
	}
	
	public IBlockState getState()
	{
		if(block == null)
		{
			Block b = Block.getBlockFromName(name);
			
			if(b != null)
			{
				block = meta < 0? b.getDefaultState() : b.getStateFromMeta(meta);
			}
		}
		
		return block != null? block : Blocks.stone.getDefaultState();
	}
	
	public void writeToJson(JsonObject json)
	{
		json.addProperty("block", name);
		json.addProperty("meta", meta);
		json.addProperty("weight", weight);
		
		JsonArray pList = new JsonArray();
		for(GridPlant plant : plants)
		{
			JsonObject tmp = new JsonObject();
			tmp.addProperty("block", plant.name);
			tmp.addProperty("meta", plant.meta);
			pList.add(tmp);
		}
		json.add("plants", pList);
		
		JsonArray bList = new JsonArray();
		for(int id : biomes)
		{
			BiomeGenBase b = BiomeGenBase.getBiome(id);
			
			if(b != null)
			{
				JsonPrimitive jp = new JsonPrimitive(BiomeGenBase.biomeRegistry.getNameForObject(b).toString());
				bList.add(jp);
			}
		}
		json.add("biomes", bList);
	}
	
	public void readFromJson(JsonObject json)
	{
		name = JsonHelper.GetString(json, "block", "minecraft:stone");
		meta = JsonHelper.GetNumber(json, "meta", -1).intValue();
		weight = JsonHelper.GetNumber(json, "weight", weight).intValue();
		
		plants = new ArrayList<GridPlant>();
		for(JsonElement e : JsonHelper.GetArray(json, "plants"))
		{
			if(e == null || !e.isJsonObject())
			{
				continue;
			}
			
			JsonObject tmp = e.getAsJsonObject();
			String pn = JsonHelper.GetString(tmp, "block", "minecraft:stone");
			int pm = JsonHelper.GetNumber(tmp, "meta", -1).intValue();
			plants.add(new GridPlant(pn, pm));
		}
		
		biomes = new ArrayList<Integer>();
		for(JsonElement e : JsonHelper.GetArray(json, "biomes"))
		{
			if(e == null || !e.isJsonPrimitive())
			{
				continue;
			}
			
			String bName = e.getAsString();
			BiomeGenBase b = BiomeGenBase.biomeRegistry.getObject(new ResourceLocation(bName));
			
			if(b != null)
			{
				int bID = BiomeGenBase.getIdForBiome(b);
				if(!biomes.contains(bID))
				{
					biomes.add(bID);
				}
			}
		}
	}
	
	public static GridBlock parseLegacy(String string)
	{
		String[] parts = string.split(":");
		
		if(parts.length < 2)
		{
			return null;
		}
		
		ResourceLocation blockRes = new ResourceLocation(parts[0], parts[1]);
		Block b = Block.blockRegistry.getObject(blockRes);
		
		if(b == null)
		{
			return null;
		}
		
		if(parts.length > 2)
		{
			try
			{
				int meta = Integer.parseInt(parts[2]);
				return new GridBlock(b, meta);
			} catch(Exception e){}
		}
		
		return new GridBlock(b);
	}
	
	public static class GridPlant
	{
		String name = "minecraft:tall_grass";
		int meta = 0;
		IBlockState block;
		
		public GridPlant(IBlockState block)
		{
			this.block = block;
			meta = block.getBlock().getMetaFromState(block);
			name = Block.blockRegistry.getNameForObject(block.getBlock()).toString();
		}
		
		public GridPlant(String name, int meta)
		{
			this.name = name;
			this.meta = meta;
		}
		
		public IBlockState getState()
		{
			if(block == null)
			{
				Block b = Block.getBlockFromName(name);
				
				if(b != null)
				{
					block = meta < 0? b.getDefaultState() : b.getStateFromMeta(meta);
				}
			}
			
			return block != null? block : Blocks.stone.getDefaultState();
		}
	}
}
