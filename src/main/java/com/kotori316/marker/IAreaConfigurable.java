package com.kotori316.marker;

import net.minecraft.util.math.BlockPos;

public interface IAreaConfigurable {
    Runnable setMinMax(BlockPos min, BlockPos max);
}
