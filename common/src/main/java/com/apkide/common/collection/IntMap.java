package com.apkide.common.collection;

import static java.util.Arrays.fill;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.SerializeHelper;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;
import com.apkide.common.Storeable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

public class IntMap<E extends Serializable> implements Storeable, Serializable {


	private static final long serialVersionUID = 2568449418902072008L;
	private static final int[] SIZES = new int[]{5, 11, 17, 37, 67, 131, 257, 521, 1031, 2053,
			4099, 8209, 16411, 32771, 65537, 131101, 262147, 524309, 1048583, 2097169, 4194319,
			8388617, 16777259, 33554467, 67108879, 134217757, 268435459, 536870923, 1073741827,
			2147383649
	};

	public final Iterator ITERATOR = new Iterator();
	private int[] keys;
	private int[] oldKeys;
	private Object[] values;
	private Object[] oldValues;
	private int slots;
	private int count;
	private int sizeExp;

	public IntMap() {
		sizeExp = 0;
		keys = new int[SIZES[sizeExp]];
		values = new Object[SIZES[sizeExp]];
		slots = 0;
		count = 0;
	}

	public IntMap(int capacity) {
		sizeExp = 0;
		while (SIZES[sizeExp] < capacity * 2) {
			sizeExp++;
		}
		keys = new int[SIZES[sizeExp]];
		values = new Object[SIZES[sizeExp]];
		slots = 0;
		count = 0;
	}

	public void clear() {
		if (slots > 0) {
			fill(keys, 0);
			slots = 0;
			count = 0;
		}
	}

	public void clear(int size) {
		if (slots > 0) {
			if (keys.length < size) {
				fill(keys, 0);
			} else {
				sizeExp = 0;
				while (SIZES[sizeExp] < size * 2) {
					sizeExp++;
				}

				keys = new int[SIZES[sizeExp]];
				values = new Object[SIZES[sizeExp]];
			}

			slots = 0;
			count = 0;
		}
	}

	public void insert(@NonNull IntMap<E> map) {
		for (int i = 0; i < map.keys.length; i++) {
			int key = map.keys[i];
			Object value = map.values[i];
			if (key == Integer.MAX_VALUE)
				insert(0, (E) value);
			else if (key != 0 && key != Integer.MIN_VALUE)
				insert(key, (E) value);
		}
	}

	public void put(int key, @Nullable E value) {
		if (key == 0) key = Integer.MAX_VALUE;

		int index = (key & Integer.MAX_VALUE) % keys.length;
		int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;
		boolean inserted = false;
		int emptyIndex = -1;

		for (int curKey = keys[index]; curKey != 0; curKey = keys[index]) {
			if (curKey == key) {

				if (values[index] != value) {
					keys[index] = Integer.MIN_VALUE;
					count--;
				} else
					inserted = true;

			} else if (curKey == Integer.MIN_VALUE)
				emptyIndex = index;

			index = (index + step) % keys.length;
		}

		if (!inserted) {
			if (emptyIndex != -1)
				index = emptyIndex;

			keys[index] = key;
			values[index] = value;
			count++;
			if (emptyIndex == -1)
				slots++;

			if (slots * 2 > keys.length)
				rehash();
		}
	}

	public void insert(int key, @Nullable E value) {
		if (key == 0) key = Integer.MAX_VALUE;

		int index = (key & Integer.MAX_VALUE) % keys.length;
		int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;
		boolean inserted = false;
		int emptyIndex = -1;

		for (int curKey = keys[index]; curKey != 0; curKey = keys[index]) {
			if (curKey == key) {
				if (values[index] == value) return;

			} else if (curKey == Integer.MIN_VALUE)
				emptyIndex = index;

			index = (index + step) % keys.length;
		}

		if (!inserted) {
			if (emptyIndex != -1)
				index = emptyIndex;

			keys[index] = key;
			values[index] = value;
			count++;
			if (emptyIndex == -1)
				slots++;

			if (slots * 2 > keys.length)
				rehash();
		}
	}

	public boolean contains(int key, @NonNull E value) {
		if (key == 0) key = Integer.MAX_VALUE;

		int index = (key & Integer.MAX_VALUE) % keys.length;
		int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;

		for (int curKey = keys[index]; curKey != key ||
				values[index] != value; curKey = keys[index]) {
			if (curKey == 0) return false;

			index = (index + step) % keys.length;
		}

		return true;
	}

	public void remove(int key, @NonNull E value) {
		if (key == 0) key = Integer.MAX_VALUE;

		int index = (key & Integer.MAX_VALUE) % keys.length;
		int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;

		for (int curKey = keys[index]; curKey != 0; curKey = keys[index]) {
			if (curKey == key && values[index] == value) {
				keys[index] = Integer.MIN_VALUE;
				count--;
			}

			index = (index + step) % keys.length;
		}
	}

	public void remove(int key) {
		if (key == 0) key = Integer.MAX_VALUE;

		int index = (key & Integer.MAX_VALUE) % keys.length;
		int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;

		for (int curKey = keys[index]; curKey != 0; curKey = keys[index]) {
			if (curKey == key) {
				keys[index] = Integer.MIN_VALUE;
				count--;
			}

			index = (index + step) % keys.length;
		}
	}

	public boolean contains(int key) {
		if (key == 0) key = Integer.MAX_VALUE;

		int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;
		int index = (key & Integer.MAX_VALUE) % keys.length;

		for (int curKey = keys[index]; curKey != key; curKey = keys[index]) {
			if (curKey == 0) return false;

			index = (index + step) % keys.length;
		}

		return true;
	}

	public int count(int key) {
		if (key == 0) key = Integer.MAX_VALUE;

		int count = 0;
		int step = (key & Integer.MAX_VALUE) % (this.keys.length - 2) + 1;
		int index = (key & Integer.MAX_VALUE) % this.keys.length;

		for (int curKey = keys[index]; curKey != 0; curKey = keys[index]) {
			if (curKey == key) count++;

			index = (index + step) % this.keys.length;
		}

		return count;
	}

	@Nullable
	public E get(int key) {
		if (key == 0) key = Integer.MAX_VALUE;

		int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;
		int index = (key & Integer.MAX_VALUE) % keys.length;

		for (int curKey = keys[index]; curKey != key; curKey = keys[index]) {
			if (curKey == 0) return null;

			index = (index + step) % keys.length;
		}

		return values[index] == null ? null : (E) values[index];
	}

	private void rehash() {
		int[] newKeys;
		Object[] newValues;
		if (count * 2 > keys.length) {
			sizeExp++;
			oldKeys = null;
			oldValues = null;
			newKeys = new int[SIZES[sizeExp]];
			newValues = new Object[SIZES[sizeExp]];
		} else if (oldKeys != null && oldKeys.length == keys.length) {
			newKeys = oldKeys;

			fill(newKeys, 0);

			newValues = oldValues;
			oldKeys = keys;
			oldValues = values;
		} else {
			oldKeys = keys;
			oldValues = values;
			newKeys = new int[SIZES[sizeExp]];
			newValues = new Object[SIZES[sizeExp]];
		}

		int newSlots = 0;

		for (int i = 0; i < keys.length; i++) {
			int key = keys[i];
			if (key != 0 && key != Integer.MIN_VALUE) {
				int step = (key & Integer.MAX_VALUE) % (newKeys.length - 2) + 1;
				int index = (key & Integer.MAX_VALUE) % newKeys.length;

				while (newKeys[index] != 0) {
					index = (index + step) % newKeys.length;
				}

				newKeys[index] = key;
				newValues[index] = values[i];
				newSlots++;
			}
		}

		keys = newKeys;
		values = newValues;
		slots = newSlots;
	}

	public int getSize() {
		return count;
	}

	@NonNull
	public Iterator iterator() {
		return new Iterator();
	}

	@Override
	public void store(@NonNull StoreOutputStream out) throws IOException {
		out.writeInt(count);
		out.writeInt(sizeExp);

		for (int i = 0; i < keys.length; ++i) {
			int key = keys[i];
			if (key != 0 && key != Integer.MIN_VALUE) {
				out.writeInt(keys[i]);
				byte[] bytes = SerializeHelper.serialize(values[i]);
				out.writeInt(bytes == null ? 0 : bytes.length);
				if (bytes != null)
					out.write(bytes);
			}
		}
	}

	@Override
	public void restore(@NonNull StoreInputStream in) throws IOException {
		int size = in.readInt();
		sizeExp = in.readInt();
		keys = new int[SIZES[sizeExp]];
		values = new Object[SIZES[sizeExp]];
		slots = 0;
		count = 0;

		for (int i = 0; i < size; i++) {
			int key = in.readInt();
			int length = in.readInt();
			byte[] bytes = new byte[length];
			if (length == 0) {
				insert(key, null);
			} else {
				in.read(bytes);
				Object object = SerializeHelper.deserialize(bytes, null);
				insert(key, (E) object);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IntMap<E> intIntMap = (IntMap<E>) o;

		if (slots != intIntMap.slots) return false;
		if (count != intIntMap.count) return false;
		if (sizeExp != intIntMap.sizeExp) return false;
		if (!Arrays.equals(keys, intIntMap.keys)) return false;
		if (!Arrays.equals(oldKeys, intIntMap.oldKeys)) return false;
		if (!Arrays.equals(values, intIntMap.values)) return false;
		return Arrays.equals(oldValues, intIntMap.oldValues);
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(keys);
		result = 31 * result + Arrays.hashCode(oldKeys);
		result = 31 * result + Arrays.hashCode(values);
		result = 31 * result + Arrays.hashCode(oldValues);
		result = 31 * result + slots;
		result = 31 * result + count;
		result = 31 * result + sizeExp;
		return result;
	}

	public class Iterator {
		private int index = 0;
		private int step = 0;
		private int key;
		private Object value;
		private int theKey;

		private Iterator() {
		}

		public void init() {
			index = 0;
			step = 0;
		}

		public void init(int key) {
			if (key == 0) key = Integer.MAX_VALUE;

			theKey = key;
			step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;
			index = (key & Integer.MAX_VALUE) % keys.length;
		}

		public boolean hasNext() {
			if (step == 0) {
				while (index < keys.length) {
					key = keys[index];
					if (key != 0 && key != Integer.MIN_VALUE) {
						if (key == Integer.MAX_VALUE)
							key = 0;

						value = values[index];
						index++;
						return true;
					}

					index++;
				}
				return false;
			} else {
				while (true) {
					key = keys[index];
					if (key == 0) return false;

					if (key == theKey) {
						if (key == Integer.MAX_VALUE)
							key = 0;

						value = values[index];
						index = (index + step) % keys.length;
						return true;
					}

					index = (index + step) % keys.length;
				}
			}
		}

		public int nextKey() {
			return key;
		}

		@Nullable
		public E nextValue() {
			return (E) value;
		}
	}
}
