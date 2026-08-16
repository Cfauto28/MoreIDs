package net.fmg793.moreids.impl.id;

public interface IChunkNibbleStorageProvider {
	public default short[] getData() {
		throw new AbstractMethodError();
	}
}
