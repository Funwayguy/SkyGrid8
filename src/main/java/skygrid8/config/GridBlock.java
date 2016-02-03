package skygrid8.config;

import java.util.ArrayList;
import skygrid8.JsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public class GridBlock
{
	public IBlockState block = Blocks.stone.getDefaultState();
	
	public ArrayList<IBlockState> plants = new ArrayList<IBlockState>();
	
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
	
	public void addPlant(IBlockState state)
	{
		plants.add(state);
	}
	
	public void addPlant(Block b)
	{
		addPlant(b.getDefaultState());
	}
	
	public void addPlant(Block b, int meta)
	{
		addPlant(b.getStateFromMeta(meta));
	}
	
	public boolean hasPlants()
	{
		return plants != null && plants.size() > 0;
	}
	
	public void writeToJson(JsonObject json)
	{
		json.addProperty("block", Block.blockRegistry.getNameForObject(block.getBlock()).toString());
		json.addProperty("meta", block.getBlock().getMetaFromState(block));
		
		JsonArray pList = new JsonArray();
		for(IBlockState state : plants)
		{
			JsonObject tmp = new JsonObject();
			tmp.addProperty("block", Block.blockRegistry.getNameForObject(state.getBlock()).toString());
			tmp.addProperty("meta", state.getBlock().getMetaFromState(state));
			pList.add(tmp);
		}
		json.add("plants", pList);
	}
	
	public void readFromJson(JsonObject json)
	{
		Block b = Block.getBlockFromName(JsonHelper.GetString(json, "block", "minecraft:stone"));
		b = b != null? b : Blocks.stone;
		int meta = JsonHelper.GetNumber(json, "meta", -1).intValue();
		
		IBlockState state = meta < 0? b.getDefaultState() : b.getStateFromMeta(meta);
		block = state != null? state : Blocks.stone.getDefaultState();
		
		plants = new ArrayList<IBlockState>();
		for(JsonElement e : JsonHelper.GetArray(json, "plants"))
		{
			if(e == null || !e.isJsonObject())
			{
				continue;
			}
			
			JsonObject tmp = e.getAsJsonObject();
			b = Block.getBlockFromName(JsonHelper.GetString(tmp, "block", "minecraft:stone"));
			b = b != null? b : Blocks.stone;
			meta = JsonHelper.GetNumber(tmp, "meta", -1).intValue();
			state = meta < 0? b.getDefaultState() : b.getStateFromMeta(meta);
			plants.add(state != null? state : Blocks.stone.getDefaultState());
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
}
