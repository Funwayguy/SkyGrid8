package skygrid8.core;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * A container for all the configurable settings in the mod
 */
public class SG_Settings
{
	public static boolean rngSpacing = false;
	public static boolean populate = false;
	public static int height = 128;
	public static int dist = 4;
	public static ArrayList<String> spawnO = new ArrayList<String>();
	public static ArrayList<String> spawnN = new ArrayList<String>();
	public static ArrayList<String> spawnE = new ArrayList<String>();
	
	public static ArrayList<String> lootChests = new ArrayList<String>();
	
	static
	{
		HashMap<String, ChestGenHooks> lootMap = ObfuscationReflectionHelper.getPrivateValue(ChestGenHooks.class, null, "chestInfo");
		lootChests.addAll(lootMap.keySet());
		
		if(lootChests.size() <= 0)
		{
			lootChests.add(ChestGenHooks.DUNGEON_CHEST);
		}
	}
}
