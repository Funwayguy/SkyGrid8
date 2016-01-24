package skygrid8;

import skygrid8.core.SG_Settings;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderOverworldGrid extends WorldProvider
{
	@Override
	public String getDimensionName()
	{
		return "SkyGrid";
	}
	
	@Override
	public String getInternalNameSuffix()
	{
		return "_skygrid";
	}
	
    /**
     * Returns a new chunk provider which generates chunks for this world
     */
    public IChunkProvider createChunkGenerator()
    {
    	return new ChunkProviderGrid(this.worldObj, this.getSeed(), SG_Settings.oBlockList);
    }
    
    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    public boolean canCoordinateBeSpawn(int x, int z)
    {
        return x == 0 && z == 0;
    }

    public BlockPos getSpawnPoint()
    {
        return new BlockPos(0, SG_Settings.height + 1, 0);
    }

    public BlockPos getSpawnCoordinate()
    {
        return this.getSpawnPoint();
    }

    public BlockPos getRandomizedSpawnPoint()
    {
    	return this.getSpawnPoint();
    }
}
