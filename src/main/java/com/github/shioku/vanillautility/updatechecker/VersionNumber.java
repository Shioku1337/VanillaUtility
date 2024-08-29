package com.github.shioku.vanillautility.updatechecker;

public record VersionNumber(int major, int minor, int patch) {
  public static VersionNumber fromString(String version) {
    String[] split = version.split("\\.");
    return new VersionNumber(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
  }
}
