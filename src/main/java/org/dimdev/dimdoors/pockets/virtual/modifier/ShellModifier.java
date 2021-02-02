package org.dimdev.dimdoors.pockets.virtual.modifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.util.NbtType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.util.schematic.v2.Schematic;
import org.dimdev.dimdoors.util.schematic.v2.SchematicBlockPalette;
import org.dimdev.dimdoors.world.pocket.Pocket;

public class ShellModifier implements Modifier{
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String KEY = "shell";

	private final List<Layer> layers = new ArrayList<>();

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Modifier.super.toTag(tag);

		ListTag layersTag = new ListTag();
		for (Layer layer : layers) {
			layersTag.add(layer.toTag());
		}
		tag.put("layers", layersTag);

		return tag;
	}

	@Override
	public Modifier fromTag(CompoundTag tag) {
		for (Tag layerTag : tag.getList("layers", NbtType.COMPOUND)) {
			CompoundTag compoundTag = (CompoundTag) layerTag;
			try {
				Layer layer = Layer.fromTag(compoundTag);
				layers.add(layer);
			} catch (CommandSyntaxException e) {
				LOGGER.error("could not parse Layer: " + compoundTag.toString(), e);
			}
		}

		return this;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.SHELL_MODIFIER_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void apply(Pocket pocket, PocketGenerationParameters parameters) {
		layers.forEach(layer -> drawLayer(layer, pocket, parameters.getWorld()));
	}

	private void drawLayer(Layer layer, Pocket pocket, ServerWorld world) {
		int thickness = layer.getThickness();
		final BlockState blockState = layer.getBlockState();
		BlockBox pocketBox = pocket.getBox();

		// x-planes
		BlockPos.stream(BlockBox.create(pocketBox.maxX + 1, pocketBox.minY - thickness, pocketBox.minZ - thickness, pocketBox.maxX + thickness, pocketBox.maxY + thickness, pocketBox.maxZ + thickness))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));
		BlockPos.stream(BlockBox.create(pocketBox.minX - 1, pocketBox.minY - thickness, pocketBox.minZ - thickness, pocketBox.minX - thickness, pocketBox.maxY + thickness, pocketBox.maxZ + thickness))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));

		// y-planes
		BlockPos.stream(BlockBox.create(pocketBox.minX, pocketBox.maxY + 1, pocketBox.minZ - thickness, pocketBox.maxX, pocketBox.maxY + thickness, pocketBox.maxZ + thickness))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));
		BlockPos.stream(BlockBox.create(pocketBox.minX, pocketBox.minY - 1, pocketBox.minZ - thickness, pocketBox.maxX, pocketBox.minY - thickness, pocketBox.maxZ + thickness))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));

		// z-planes
		BlockPos.stream(BlockBox.create(pocketBox.minX, pocketBox.minY, pocketBox.minZ - 1, pocketBox.maxX, pocketBox.maxY, pocketBox.minZ - thickness))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));
		BlockPos.stream(BlockBox.create(pocketBox.minX, pocketBox.minY, pocketBox.maxZ + 1, pocketBox.maxX, pocketBox.maxY, pocketBox.maxZ + thickness))
				.forEach(blockPos -> world.setBlockState(blockPos, blockState));

		pocket.expand(thickness);
	}

	public static class Layer {
		private final String blockStateString;
		private final int thickness; // TODO: maybe this could even be an equation?
		private final BlockState blockState;

		public Layer(String blockStateString, int thickness) {
			this.blockStateString = blockStateString;
			this.thickness = thickness;

			this.blockState = SchematicBlockPalette.Entry.to(blockStateString).getOrThrow(false, LOGGER::error);
		}

		public BlockState getBlockState() {
			return blockState;
		}

		public int getThickness() {
			return thickness;
		}

		public CompoundTag toTag() {
			CompoundTag tag = new CompoundTag();
			tag.putString("block_state", blockStateString);
			tag.putInt("thickness", thickness);
			return tag;
		}

		public static Layer fromTag(CompoundTag tag) throws CommandSyntaxException {
			return new Layer(tag.getString("block_state"), tag.getInt("thickness"));
		}
	}
}

