package com.apkide.analysis.api.clm.collections;


import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

import com.apkide.analysis.api.clm.Entity;
import com.apkide.analysis.api.clm.EntitySpace;
import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;
import com.apkide.common.collections.SetOfInt;

import java.io.IOException;
import java.util.Arrays;

public class SetOf<E extends Entity> {
   public final Iterator DEFAULT_ITERATOR = new Iterator();
   private static final int LOAD_FACTOR = 2;
   private static final int[] SIZES = new int[]{
      5,
      11,
      17,
      37,
      67,
      131,
      257,
      521,
      1031,
      2053,
      4099,
      8209,
      16411,
      32771,
      65537,
      131101,
      262147,
      524309,
      1048583,
      2097169,
      4194319,
      8388617,
      16777259,
      33554467,
      67108879,
      134217757,
      268435459,
      536870923,
      1073741827,
      2147383649
   };
   private int[] keys;
   private int[] oldkeys;
   private int slots;
   private int count;
   private int sizeexp;
   private final EntitySpace space;

   public SetOf(EntitySpace space, int size) {
      this.space = space;
      this.sizeexp = 1;

      while(SIZES[this.sizeexp] < size * LOAD_FACTOR) {
         ++this.sizeexp;
      }

      this.keys = new int[SIZES[this.sizeexp]];
      this.slots = 0;
      this.count = 0;
   }

   public SetOf(EntitySpace space) {
      this.space = space;
      this.sizeexp = 1;
      this.keys = new int[SIZES[this.sizeexp]];
      this.slots = 0;
      this.count = 0;
   }

   public SetOf(EntitySpace space, StoreInputStream stream) throws IOException {
      this.space = space;
      int count = stream.readInt();
      this.sizeexp = stream.readInt();
      this.keys = new int[SIZES[this.sizeexp]];
      this.slots = 0;
      this.count = 0;

      for(int i = 0; i < count; ++i) {
         put(stream.readInt());
      }
   }

   public void store(StoreOutputStream stream) throws IOException {
      stream.writeInt(this.count);
      stream.writeInt(this.sizeexp);

      for (int key : this.keys) {
         if (key != 0 && key != MIN_VALUE)
            stream.writeInt(key);
      }
   }

   public Iterator createIterator() {
      return new Iterator();
   }

   public void clear() {
      if (this.slots > 0) {
         Arrays.fill(this.keys, 0);

         this.slots = 0;
         this.count = 0;
      }
   }

   public void put(SetOf<? extends E> set) {
      for(int i = 0; i < set.keys.length; ++i) {
         int key = set.keys[i];
         if (key == MAX_VALUE)
            put(0);
          else if (key != 0 && key != MIN_VALUE)
            put(key);
      }
   }

   public void put(ListOf<? extends E> vec) {
      for(int i = 0; i < vec.size(); ++i) {
         put(vec.get(i));
      }
   }

   public void put(E entity) {
      if (entity == null)
         put(-1);
       else
         put(this.space.getID(entity));
   }

   private void put(int key) {
      if (key == 0)
         key = MAX_VALUE;

      int index = (key & MAX_VALUE) % this.keys.length;
      int step = (key & Integer.MAX_VALUE) % (this.keys.length - 2) + 1;
      int curKey = this.keys[index];

      int emptyIndex;
      for(emptyIndex = -1; curKey != 0; curKey = this.keys[index]) {
         if (curKey == key)
            return;

         if (curKey == MIN_VALUE)
            emptyIndex = index;

         index = (index + step) % this.keys.length;
      }

      if (emptyIndex != -1)
         index = emptyIndex;

      this.keys[index] = key;
      if (emptyIndex == -1)
         ++this.slots;

      ++this.count;
      if (this.slots * LOAD_FACTOR > this.keys.length)
         rehash();
   }

   public void remove(SetOf<? extends E> keys) {
      keys.DEFAULT_ITERATOR.init();

      while(keys.DEFAULT_ITERATOR.hasMoreElements()) {
         remove(keys.DEFAULT_ITERATOR.nextKey());
      }
   }

   public void remove(Entity entity) {
      int key = this.space.getID(entity);
      if (key == 0)
         key = MAX_VALUE;

      int index = (key & Integer.MAX_VALUE) % this.keys.length;
      int step = (key & Integer.MAX_VALUE) % (this.keys.length - 2) + 1;

      for(int curKey = this.keys[index]; curKey != key; curKey = this.keys[index]) {
         if (curKey == 0)
            return;

         index = (index + step) % this.keys.length;
      }

      this.keys[index] = MIN_VALUE;
      --this.count;
   }

   public E get() {
      int index = 0;

      while(index < this.keys.length) {
         int key = this.keys[index];
         ++index;
         if (key != 0 && key != MIN_VALUE) {
            if (key == MAX_VALUE)
               key = 0;

            return (E)this.space.getEntity(key);
         }
      }

      return null;
   }

   public boolean contains(Entity entity) {
      int key = this.space.getID(entity);
      if (key == 0)
         key = MAX_VALUE;

      int step = (key & Integer.MAX_VALUE) % (this.keys.length - 2) + 1;
      int index = (key & Integer.MAX_VALUE) % this.keys.length;

      for(int curKey = this.keys[index]; curKey != key; curKey = this.keys[index]) {
         if (curKey == 0)
            return false;

         index = (index + step) % this.keys.length;
      }

      return true;
   }

   public boolean empty() {
      return this.count == 0;
   }

   private void rehash() {
      int[] keys;
      if (this.count * LOAD_FACTOR > this.keys.length) {
         ++this.sizeexp;
         this.oldkeys = null;
         keys = new int[SIZES[this.sizeexp]];
      } else if (this.oldkeys != null && this.oldkeys.length == this.keys.length) {
         keys = this.oldkeys;

         Arrays.fill(keys, 0);

         this.oldkeys = this.keys;
      } else {
         this.oldkeys = this.keys;
         keys = new int[SIZES[this.sizeexp]];
      }

      int slots = 0;

      for (int key : this.keys) {
         if (key != 0 && key != MIN_VALUE) {
            int step = (key & Integer.MAX_VALUE) % (keys.length - 2) + 1;
            int index = (key & Integer.MAX_VALUE) % keys.length;

            while (keys[index] != 0) {
               index = (index + step) % keys.length;
            }

            keys[index] = key;
            ++slots;
         }
      }

      this.keys = keys;
      this.slots = slots;
   }

   public int size() {
      return this.count;
   }
   @Override
   public boolean equals(Object o) {
      if (!(o instanceof SetOfInt)) {
         return false;
      } else {
         SetOfInt set = (SetOfInt)o;
         if (set.size() != this.size()) {
            return false;
         } else {
            for (int j : this.keys) {
               int key = j;
               if (key != 0 && key != MIN_VALUE) {
                  if (key == MAX_VALUE)
                     key = 0;

                  if (!set.contains(key))
                     return false;
               }
            }

            return true;
         }
      }
   }

   public class Iterator {
      private int index = 0;
      private int key;

      private Iterator() {
      }

      public void init() {
         this.index = 0;
      }

      public boolean hasMoreElements() {
         while(this.index < SetOf.this.keys.length) {
            this.key = SetOf.this.keys[this.index];
            ++this.index;
            if (this.key != 0 && this.key != MIN_VALUE) {
               if (this.key == MAX_VALUE)
                  this.key = 0;

               return true;
            }
         }

         return false;
      }

      public E nextKey() {
         return (E)SetOf.this.space.getEntity(this.key);
      }
   }
}
