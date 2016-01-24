package skygrid8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
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
        
        ArrayList<TileEntityChest> pendingChests = new ArrayList<TileEntityChest>();
        
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
                		
                		if(i < 255 && iblockstate.getBlock() instanceof BlockFarmland)
                		{
                			IBlockState crop = SG_Settings.fBlockList.size() <= 0? null : SG_Settings.fBlockList.get(random.nextInt(SG_Settings.fBlockList.size()));
                			
                			if(crop != null)
                			{
                				chunkprimer.setBlockState(j, i + 1, k, crop);
                			}
                		} else if(iblockstate.getBlock().getClass() == BlockChest.class)
                		{
                			TileEntityChest cTile = new TileEntityChest();
                			WeightedRandomChestContent.generateChestContents(random, ChestGenHooks.getItems(lootChests.get(random.nextInt(lootChests.size())), random), cTile, 8);
                			cTile.setWorldObj(worldObj); // Must be done after contents are set
                			cTile.setPos(new BlockPos(j, i, k));
                			pendingChests.add(cTile);
                		}
                	}
                }
            }
        }

        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        
        for(TileEntityChest ct : pendingChests)
        {
        	chunk.addTileEntity(ct);
        }
        
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
		if(!SG_Settings.populate)
		{
			return;
		}
		
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
	
	static ArrayList<String> lootChests = new ArrayList<String>();
	
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
