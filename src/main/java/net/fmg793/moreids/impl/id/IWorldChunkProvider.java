package net.fmg793.moreids.impl.id;

import net.fmg793.moreids.id.ChunkNibbleStorageExt;

public interface IWorldChunkProvider {
	public default short getBlockArray(int value) {
		throw new AbstractMethodError();
	}

	public default short[] getBlocks() {
		throw new AbstractMethodError();
	}

	public default ChunkNibbleStorageExt getBlockMetadata() {
		throw new AbstractMethodError();
	}

	public default ChunkNibbleStorageExt getSkyLight() {
		throw new AbstractMethodError();
	}

	public default ChunkNibbleStorageExt getBlockLight() {
		throw new AbstractMethodError();
	}
}
