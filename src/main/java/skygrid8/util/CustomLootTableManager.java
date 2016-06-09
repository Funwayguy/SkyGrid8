package skygrid8.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.Level;
import skygrid8.core.SkyGrid8;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class CustomLootTableManager
{
	static ArrayList<LootMapping> LOOT_MAP = new ArrayList<LootMapping>();
	
	// Vanilla compatible GSON parser
    static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
    
	public static void LoadLoot(File file, LootTableManager manager)
	{
		if(!file.exists())
		{
			if(file.getParentFile() != null)
			{
				file.getParentFile().mkdirs();
			}
			GenerateDefault(file);
		}
		
		JsonObject json = JsonHelper.ReadObjectFromFile(file);
		
		LOOT_MAP.clear();
		
		for(JsonElement e : JsonHelper.GetArray(json, "placement"))
		{
			if(e == null || !e.isJsonObject())
			{
				continue;
			}
			
			LootMapping map = new LootMapping();
			map.readFromJson(e.getAsJsonObject());
			LOOT_MAP.add(map);
		}
		
		for(JsonElement e : JsonHelper.GetArray(json, "lootTables"))
		{
			if(e == null || !e.isJsonObject())
			{
				continue;
			}
			
			// Load custom format
			CustomLootTable tbl = new CustomLootTable();
			tbl.readFromJson(e.getAsJsonObject());
			
			// Format to vanilla
			JsonArray fmtAry = new JsonArray();
			fmtAry.add(tbl.getFormatted());
			
			JsonObject fmtObj = new JsonObject();
			fmtObj.add("pools", fmtAry);
			String fmt = new GsonBuilder().setPrettyPrinting().create().toJson(fmtObj);
			
			// Register table
			try
			{
				ResourceLocation tableID = new ResourceLocation(tbl.name);
				LootTable vanTable = ForgeHooks.loadLootTable(GSON, tableID, fmt, true);
				LoadingCache<ResourceLocation, LootTable> registeredLootTables = ObfuscationReflectionHelper.getPrivateValue(LootTableManager.class, manager, "field_186527_c", "registeredLootTables");
				registeredLootTables.put(tableID, vanTable); // FORCE IT IN!
			} catch(Exception ex)
			{
				SkyGrid8.logger.log(Level.ERROR, "Unable to register custom loot table " + tbl.name, ex);
				continue;
			}
			
			SkyGrid8.logger.log(Level.INFO, "Registered custom loot table " + tbl.name);
		}
	}
	
	private static void GenerateDefault(File file)
	{
		JsonObject parentJson = new JsonObject();
		
		JsonArray mappings = new JsonArray();
		JsonObject jMap = new JsonObject();
		LootMapping map = new LootMapping();
		map.tables.add(new ResourceLocation(SkyGrid8.MODID + ":ingots"));
		map.tables.addAll(LootTableList.getAll());
		map.writeToJson(jMap);
		mappings.add(jMap);
		parentJson.add("placement", mappings);
		
		// Generic loot table with a few weighted ingots
		JsonArray tables = new JsonArray();
		CustomLootTable ltb = new CustomLootTable();
		ltb.name = SkyGrid8.MODID + ":ingots";
		ltb.maxItems = 3;
		CustomLootEntry len = new CustomLootEntry();
		len.id = "minecraft:diamond";
		len.maxStack = 8;
		len.weight = 1;
		ltb.entries.add(len);
		len = new CustomLootEntry();
		len.id = "minecraft:gold_ingot";
		len.maxStack = 8;
		len.weight = 2;
		ltb.entries.add(len);
		len = new CustomLootEntry();
		len.id = "minecraft:iron_ingot";
		len.maxStack = 8;
		len.weight = 3;
		ltb.entries.add(len);
		tables.add(ltb.writeToJson(new JsonObject()));
		parentJson.add("lootTables", tables);
		
		JsonHelper.WriteToFile(file, parentJson);
	}
	
	public static ResourceLocation getTable(World world, BlockPos pos, Random rand)
	{
		int dimension = world.provider.getDimension();
		Biome biome = world.getBiomeGenForCoords(pos);
		ResourceLocation biomeReg = biome.getRegistryName();
		IBlockState state = world.getBlockState(pos);
		ResourceLocation blockReg = state.getBlock().getRegistryName();
		
		ArrayList<ResourceLocation> list = new ArrayList<ResourceLocation>();
		
		for(LootMapping entry : LOOT_MAP)
		{
			if(entry.isApplicable(dimension, biomeReg, blockReg))
			{
				list.addAll(entry.tables);
			}
		}
		
		if(list.size() <= 0)
		{
			return LootTableList.EMPTY;
		} else
		{
			return list.get(rand.nextInt(list.size()));
		}
	}
	
	public static class LootMapping
	{
		ArrayList<Integer> dimensions = new ArrayList<Integer>();
		ArrayList<ResourceLocation> biomes = new ArrayList<ResourceLocation>();
		ArrayList<ResourceLocation> blocks = new ArrayList<ResourceLocation>();
		ArrayList<ResourceLocation> tables = new ArrayList<ResourceLocation>();
		
		public boolean isApplicable(int dim, ResourceLocation biome, ResourceLocation block)
		{
			return (dimensions.size() <= 0 || dimensions.contains(dim)) && (biomes.size() <= 0 || biomes.contains(biome) && (blocks.size() <= 0 || blocks.contains(block)));
		}
		
		public void readFromJson(JsonObject json)
		{
			biomes.clear();
			for(JsonElement element : JsonHelper.GetArray(json, "biomes"))
			{
				if(element == null || !element.isJsonPrimitive())
				{
					continue;
				}
				
				ResourceLocation loc = new ResourceLocation(element.getAsString());
				
				if(loc != null)
				{
					biomes.add(loc);
				}
			}
			
			dimensions.clear();
			for(JsonElement element : JsonHelper.GetArray(json, "dimensions"))
			{
				if(element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber())
				{
					continue;
				}
				
				dimensions.add(element.getAsInt());
			}
			
			blocks.clear();
			for(JsonElement element : JsonHelper.GetArray(json, "blocks"))
			{
				if(element == null || !element.isJsonPrimitive())
				{
					continue;
				}
				
				ResourceLocation loc = new ResourceLocation(element.getAsString());
				
				if(loc != null)
				{
					blocks.add(loc);
				}
			}
			
			tables.clear();
			for(JsonElement element : JsonHelper.GetArray(json, "tables"))
			{
				if(element == null || !element.isJsonPrimitive())
				{
					continue;
				}
				
				ResourceLocation loc = new ResourceLocation(element.getAsString());
				
				if(loc != null)
				{
					tables.add(loc);
				}
			}
		}
		
		public JsonObject writeToJson(JsonObject json)
		{
			JsonArray blAry = new JsonArray();
			for(ResourceLocation loc : blocks)
			{
				blAry.add(new JsonPrimitive(loc.toString()));
			}
			json.add("blocks", blAry);
			
			JsonArray bAry = new JsonArray();
			for(ResourceLocation loc : biomes)
			{
				bAry.add(new JsonPrimitive(loc.toString()));
			}
			json.add("biomes", bAry);
			
			JsonArray dAry = new JsonArray();
			for(Integer i : dimensions)
			{
				dAry.add(new JsonPrimitive(i == null? 0 : i));
			}
			json.add("dimensions", dAry);
			
			JsonArray tAry = new JsonArray();
			for(ResourceLocation loc : tables)
			{
				tAry.add(new JsonPrimitive(loc.toString()));
			}
			json.add("tables", tAry);
			
			return json;
		}
	}
	
	public static class CustomLootTable
	{
		ArrayList<CustomLootEntry> entries = new ArrayList<CustomLootEntry>();
		String name = "generic";
		int minItems = 1;
		int maxItems = 100;
		
		public void readFromJson(JsonObject json)
		{
			this.name = JsonHelper.GetString(json, "name", "generic");
			this.minItems = JsonHelper.GetNumber(json, "minItems", 1).intValue();
			this.maxItems = JsonHelper.GetNumber(json, "maxItems", 100).intValue();
			
			entries.clear();
			for(JsonElement e : JsonHelper.GetArray(json, "entries"))
			{
				if(e == null || !e.isJsonObject())
				{
					continue;
				}
				
				CustomLootEntry entry = new CustomLootEntry();
				entry.readFromJson(e.getAsJsonObject());
				entries.add(entry);
			}
		}
		
		public JsonObject writeToJson(JsonObject json)
		{
			json.addProperty("name", name);
			json.addProperty("minItems", minItems);
			json.addProperty("maxItems", maxItems);
			
			JsonArray eAry = new JsonArray();
			for(CustomLootEntry e : entries)
			{
				eAry.add(e.writeToJson(new JsonObject()));
			}
			json.add("entries", eAry);
			
			return json;
		}
		
		public JsonObject getFormatted()
		{
			JsonObject json = new JsonObject();
			
			JsonObject rolls = new JsonObject();
			rolls.addProperty("min", minItems);
			rolls.addProperty("max", maxItems);
			json.add("rolls", rolls);
			
			JsonArray eAry = new JsonArray();
			for(CustomLootEntry e : entries)
			{
				eAry.add(e.getFormatted());
			}
			json.add("entries", eAry);
			
			return json;
		}
	}
	
	public static class CustomLootEntry
	{
		String id = "minecraft:diamond";
		int minStack = 1;
		int maxStack = 1;
		int meta = 0;
		int weight = 1;
		
		public void readFromJson(JsonObject json)
		{
			this.id = JsonHelper.GetString(json, "id", "minecraft:diamond");
			this.minStack = JsonHelper.GetNumber(json, "minStack", minStack).intValue();
			this.maxStack = JsonHelper.GetNumber(json, "maxStack", maxStack).intValue();
			this.meta = JsonHelper.GetNumber(json, "meta", 0).intValue();
			this.weight = JsonHelper.GetNumber(json, "weight", 1).intValue();
		}
		
		public JsonObject writeToJson(JsonObject json)
		{
			json.addProperty("id", id);
			json.addProperty("minStack", minStack);
			json.addProperty("maxStack", maxStack);
			json.addProperty("meta", meta);
			json.addProperty("weight", weight);
			return json;
		}
		
		public JsonObject getFormatted()
		{
			JsonObject json = new JsonObject();
			
			json.addProperty("type", "item");
			json.addProperty("name", id);
			json.addProperty("weight", weight);
			
			JsonArray fList = new JsonArray();
			
			if(meta > 0)
			{
				JsonObject func = new JsonObject();
				func.addProperty("function", "set_data");
				func.addProperty("data", meta);
				fList.add(func);
			}
			
			if(maxStack > 1)
			{
				JsonObject func = new JsonObject();
				func.addProperty("function", "set_count");
				if(maxStack != minStack)
				{
					JsonObject cnt = new JsonObject();
					cnt.addProperty("min", minStack);
					cnt.addProperty("max", maxStack);
					func.add("count", cnt);
				} else
				{
					func.addProperty("count", maxStack);
				}
				fList.add(func);
			}
			
			json.add("functions", fList);
			
			return json;
		}
	}
}
