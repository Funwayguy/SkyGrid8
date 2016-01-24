package skygrid8;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import skygrid8.core.SG_Settings;

public class ChunkProviderGrid implements IChunkProvider
{
    private World worldObj;
    private Random random;
    private final ArrayList<IBlockState> gridBlocks;
    
    public ChunkProviderGrid(World world, long seed, ArrayList<IBlockState> blocks)
    {
    	worldObj = world;
    	random = new Random(seed);
    	gridBlocks = blocks;
    }
    
	@Override
	public boolean chunkExists(int x, int z)
	{
		return true;
	}
	
	@Override
	public Chunk provideChunk(int x, int z)
	{
        ChunkPrimer chunkprimer = new ChunkPrimer();

        for (int i = 0; i < 256 && i < SG_Settings.height; i += SG_Settings.dist)
        {
            for (int j = 0; j < 16; ++j)
            {
                for (int k = 0; k < 16; ++k)
                {
                    IBlockState iblockstate = gridBlocks.size() <= 0? Blocks.bedrock.getDefaultState() : gridBlocks.get(random.nextInt(gridBlocks.size()));
                    
                	if((x*16 + j)%SG_Settings.dist != 0 || (z*16 + k)%SG_Settings.dist != 0 || iblockstate == null)
                	{
                		chunkprimer.setBlockState(j, i, k, Blocks.air.getDefaultState());
                	} else
                	{
                		chunkprimer.setBlockState(j, i, k, iblockstate);
                	}
                }
            }
        }

        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, x * 16, z * 16, 16, 16);
        byte[] abyte = chunk.getBiomeArray();

        for (int l = 0; l < abyte.length; ++l)
        {
            abyte[l] = (byte)abiomegenbase[l].biomeID;
        }

        chunk.generateSkylightMap();
        return chunk;
	}
	
	@Override
	public Chunk provideChunk(BlockPos blockPosIn)
	{
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
	}
	
	@Override
	public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_)
	{
        int i = p_73153_2_ * 16;
        int j = p_73153_3_ * 16;
        BlockPos blockpos = new BlockPos(i, 0, j);
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(new BlockPos(i + 16, 0, j + 16));
        
        biomegenbase.decorate(this.worldObj, this.random, blockpos);
	}
	
	@Override
	public boolean func_177460_a(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_)
	{
		return false;
	}
	
	@Override
	public boolean saveChunks(boolean p_73151_1_, IProgressUpdate progressCallback)
	{
		return true;
	}
	
	@Override
	public boolean unloadQueuedChunks()
	{
		return false;
	}
	
	@Override
	public boolean canSave()
	{
		return true;
	}
	
	@Override
	public String makeString()
	{
		return "SkyGrid";
	}
	
	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(pos);
        return biomegenbase.getSpawnableList(creatureType);
	}
	
	@Override
	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position)
	{
		return null;
	}
	
	@Override
	public int getLoadedChunkCount()
	{
		return 0;
	}
	
	@Override
	public void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_)
	{
	}
	
	@Override
	public void saveExtraData()
	{
	}
	
}
