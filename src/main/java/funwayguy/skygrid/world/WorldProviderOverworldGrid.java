package funwayguy.skygrid.world;

import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.core.SG_Settings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderOverworldGrid extends WorldProvider
{
    /**
     * Returns a new chunk provider which generates chunks for this world
     */
	@Override
    public IChunkGenerator createChunkGenerator()
    {
    	return new ChunkProviderGrid(this.world, this.getSeed(), GridRegistry.blocksOverworld);
    }
    
    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    @Override
    public boolean canCoordinateBeSpawn(int x, int z)
    {
        return x == 0 && z == 0;
    }
    
    @Override
    public BlockPos getSpawnPoint()
    {
        return new BlockPos(0, SG_Settings.height + 1, 0);
    }
    
    @Override
    public BlockPos getSpawnCoordinate()
    {
        return this.getSpawnPoint();
    }
    
    @Override
    public BlockPos getRandomizedSpawnPoint()
    {
    	return this.getSpawnPoint();
    }
    
	@Override
    public DimensionType getDimensionType()
    {
        return DimensionType.OVERWORLD;
    }

    /**
     * Called to determine if the chunk at the given chunk coordinates within the provider's world can be dropped. Used
     * in WorldProviderSurface to prevent spawn chunks from being unloaded.
     */
	@Override
    public boolean canDropChunk(int x, int z)
    {
        return !this.world.isSpawnChunk(x, z) || !this.world.provider.getDimensionType().shouldLoadSpawn();
    }
}
