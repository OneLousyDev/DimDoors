package org.dimdev.dimdoors.client;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EntranceRiftBlockEntityRenderer extends BlockEntityRenderer<EntranceRiftBlockEntity> {
    private static final Identifier KEY_PATH = new Identifier("dimdoors:textures/other/keyhole.png");
    private static final Identifier KEYHOLE_LIGHT = new Identifier("dimdoors:textures/other/keyhole_light.png");
    private static final Random RANDOM = new Random(31100L);
    private static final List<RenderLayer> LAYERS = IntStream.range(0, 16).mapToObj((i) -> {
        return MyRenderLayer.getDimensionalPortal(i + 1);
    }).collect(ImmutableList.toImmutableList());

    public EntranceRiftBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(EntranceRiftBlockEntity entrance, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        Direction orientation = entrance.getOrientation();
        Vector3f vec = orientation.getOpposite().getUnitVector();
        vec.scale((float) (orientation == Direction.NORTH || orientation == Direction.WEST || orientation == Direction.UP ? 0.01 : 0.01 - 1));
        double d = entrance.getPos().getSquaredDistance(this.dispatcher.camera.getPos(), true);
        int k = this.method_3592(d);
        float g = 0.75F;
        Matrix4f matrix4f = matrixStack.peek().getModel();
        this.drawAllVertices(entrance, g, 0.15F, matrix4f, vertexConsumerProvider.getBuffer(LAYERS.get(0)));

        for(int l = 1; l < k; ++l) {
            this.drawAllVertices(entrance, g, 2.0F / (float)(18 - l), matrix4f, vertexConsumerProvider.getBuffer(LAYERS.get(l)));
        }
//        Vec3d offset = new Vec3d(vec);
//        DimensionalPortalRenderer.renderDimensionalPortal(
//                entrance.getPos().getX() + offset.x,
//                entrance.getPos().getY() + offset.y,
//                entrance.getPos().getZ() + offset.z,
//                //entrance.orientation.getHorizontalAngle(),
//                //entrance.orientation.getDirectionVec().getY() * 90,
//                orientation,
//                16,
//                16,
//                entrance.getColors(16));
    }

    protected int method_3592(double d) {
        if (d > 36864.0D) {
            return 1;
        } else if (d > 25600.0D) {
            return 3;
        } else if (d > 16384.0D) {
            return 5;
        } else if (d > 9216.0D) {
            return 7;
        } else if (d > 4096.0D) {
            return 9;
        } else if (d > 1024.0D) {
            return 11;
        } else if (d > 576.0D) {
            return 13;
        } else {
            return d > 256.0D ? 14 : 15;
        }
    }

    private void drawAllVertices(EntranceRiftBlockEntity blockEntity, float f, float g, Matrix4f matrix4f, VertexConsumer vertexConsumer) {
        float h = (RANDOM.nextFloat() * 0.5F + 0.1F) * g;
        float i = (RANDOM.nextFloat() * 0.5F + 0.4F) * g;
        float j = (RANDOM.nextFloat() * 0.5F + 0.5F) * g;
        this.drawVertices(blockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, h, i, j, Direction.SOUTH);
        this.drawVertices(blockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, h, i, j, Direction.NORTH);
        this.drawVertices(blockEntity, matrix4f, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, h, i, j, Direction.EAST);
        this.drawVertices(blockEntity, matrix4f, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, h, i, j, Direction.WEST);
        this.drawVertices(blockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, h, i, j, Direction.DOWN);
        this.drawVertices(blockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, f, f, 1.0F, 1.0F, 0.0F, 0.0F, h, i, j, Direction.UP);
    }

    private void drawVertices(EntranceRiftBlockEntity endPortalBlockEntity, Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j, float k, float l, float m, float n, float o, float p, Direction direction) {
        vertexConsumer.vertex(matrix4f, f, h, j).color(n, o, p, 1.0F).next();
        vertexConsumer.vertex(matrix4f, g, h, k).color(n, o, p, 1.0F).next();
        vertexConsumer.vertex(matrix4f, g, i, l).color(n, o, p, 1.0F).next();
        vertexConsumer.vertex(matrix4f, f, i, m).color(n, o, p, 1.0F).next();
    }
}
