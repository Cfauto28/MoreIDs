package net.fmg793.moreids.mixin.id;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.fmg793.moreids.id.NBTTagShortArray;
import net.fmg793.moreids.impl.id.IShortArrayProvider;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;

@Mixin(NbtCompound.class)
public class NbtCompoundMixin implements IShortArrayProvider {

	@Shadow
	private Map elements;

	@Unique
	public void setShortArray(String string1, short[] s2) {
		this.elements.put(string1, (new NBTTagShortArray(s2)).setName(string1));
	}

	@Unique
	public short[] getShortArray(String string1) {
		if (!this.elements.containsKey(string1)) {
			return new short[0];
		} else {
			if (this.elements.get(string1) instanceof NBTTagShortArray) {
				return ((NBTTagShortArray)this.elements.get(string1)).shortArray;
			} else {
				return NBTTagShortArray.convertByteArrayToShortArray(((NbtByteArray)this.elements.get(string1)).value);
			}
		}
	}
}
