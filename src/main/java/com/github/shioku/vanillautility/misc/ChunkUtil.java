package com.github.shioku.vanillautility.misc;

public class ChunkUtil {

  public static int[] toChunkCoords(int x, int z) {
    return new int[] { x >> 4, z >> 4 };
  }

  public static int[] fromChunkCoords(int x, int z) {
    return new int[] { x << 4, z << 4 };
  }
}
