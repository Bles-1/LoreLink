package net.ultrastudios.lorelink.utils.actioncontext.blockusecontext;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public record BlockUseContext(
        @NotNull ItemStack pStack,
        @NotNull BlockState pState,
        @NotNull Level level,
        @NotNull BlockPos pPos,
        @NotNull Player pPlayer,
        @NotNull InteractionHand pHand,
        @NotNull BlockHitResult pHitResult
) { }