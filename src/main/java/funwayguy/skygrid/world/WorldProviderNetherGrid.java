package funwayguy.skygrid.world;

import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.chunk.IChunkGenerator;
import funwayguy.skygrid.config.GridRegistry;

public class WorldProviderNetherGrid extends WorldProviderHell
{

    @Override
    public IChunkGenerator createChunkGenerator()
    {
        return new ChunkProviderGrid(this.worldObj, this.worldObj.getSeed(), GridRegistry.blocksNether);
    }
}