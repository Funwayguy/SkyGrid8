package funwayguy.skygrid.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import funwayguy.skygrid.util.JsonHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class GridBlock
{
	public int weight = 10;
	private ResourceLocation name;
	private int meta;
	private IBlockState block;
	
	public List<GridPlant> plants = new ArrayList<>();
	public List<Integer> biomes = new ArrayList<>();
	
	public GridBlock(JsonObject json)
	{
		this.readFromJson(json);
	}
	
	public GridBlock(IBlockState state)
	{
		this.block = state;
		this.meta = state.getBlock().getMetaFromState(state);
		this.name = state.getBlock().getRegistryName();
	}
	
	public GridBlock(Block b)
	{
		this(b.getDefaultState());
	}
	
	@SuppressWarnings("deprecation")
	public GridBlock(Block b, int meta)
	{
		this(b.getStateFromMeta(meta));
	}
	
	public GridBlock(String name, int meta)
	{
		this.name = new ResourceLocation(name);
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
	
	@SuppressWarnings("deprecation")
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
	
	@SuppressWarnings("deprecation")
	public IBlockState getState()
	{
		if(block == null)
		{
			Block b = Block.REGISTRY.getObject(name);
			
			if(b != Blocks.AIR)
			{
				block = meta < 0? b.getDefaultState() : b.getStateFromMeta(meta);
			}
		}
		
		return block != null? block : Blocks.STONE.getDefaultState();
	}
	
	public void writeToJson(JsonObject json)
	{
		json.addProperty("block", name.toString());
		json.addProperty("meta", meta);
		json.addProperty("weight", weight);
		
		JsonArray pList = new JsonArray();
		for(GridPlant plant : plants)
		{
			JsonObject tmp = new JsonObject();
			tmp.addProperty("block", plant.name.toString());
			tmp.addProperty("meta", plant.meta);
			pList.add(tmp);
		}
		json.add("plants", pList);
		
		JsonArray bList = new JsonArray();
		for(int id : biomes)
		{
			Biome b = Biome.getBiome(id);
			
			if(b != null)
			{
				JsonPrimitive jp = new JsonPrimitive(Biome.REGISTRY.getNameForObject(b).toString());
				bList.add(jp);
			}
		}
		json.add("biomes", bList);
	}
	
	public void readFromJson(JsonObject json)
	{
		name = new ResourceLocation(JsonHelper.GetString(json, "block", "minecraft:stone"));
		meta = JsonHelper.GetNumber(json, "meta", -1).intValue();
		weight = JsonHelper.GetNumber(json, "weight", weight).intValue();
		
		plants = new ArrayList<>();
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
		
		biomes = new ArrayList<>();
		for(JsonElement e : JsonHelper.GetArray(json, "biomes"))
		{
			if(e == null || !e.isJsonPrimitive())
			{
				continue;
			}
			
			String bName = e.getAsString();
			Biome b = Biome.REGISTRY.getObject(new ResourceLocation(bName));
			
			if(b != null)
			{
				int bID = Biome.getIdForBiome(b);
				if(!biomes.contains(bID))
				{
					biomes.add(bID);
				}
			}
		}
	}
	
	public static class GridPlant
	{
		private final ResourceLocation name;
		private final int meta;
		private final IBlockState block;
		
		public GridPlant(IBlockState block)
		{
			this.block = block;
			meta = block.getBlock().getMetaFromState(block);
			name = block.getBlock().getRegistryName();
		}
		
		@SuppressWarnings("deprecation")
		public GridPlant(String name, int meta)
		{
			this.name = new ResourceLocation(name);
			this.meta = meta;
			
            Block b = Block.REGISTRY.getObject(this.name);
            
            if(b != Blocks.AIR)
            {
                block = meta < 0? b.getDefaultState() : b.getStateFromMeta(meta);
            } else
            {
                block = Blocks.STONE.getDefaultState();
            }
		}
		
		@SuppressWarnings("deprecation")
		public IBlockState getState()
		{
			return this.block;
		}
	}
}
