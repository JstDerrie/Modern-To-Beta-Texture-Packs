package com.textureconverter;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

public class AtlasBuilder {

    private int ATLAS_TILES = 16;

    private final int tileSize;
    private final BufferedImage atlas;

    public AtlasBuilder(int tileSize, boolean fromOffs) {
        if (tileSize <= 0) {
            throw new IllegalArgumentException("tileSize must be greater than 0");
        }
        if(fromOffs) {
            ATLAS_TILES = 1;
        }

        this.tileSize = tileSize;
        int atlasSize = tileSize * ATLAS_TILES;
        this.atlas = new BufferedImage(
                atlasSize,
                atlasSize,
                BufferedImage.TYPE_INT_ARGB
        );
    }

    public void addTexture(File file, int tileX, int tileY, int rotation) throws IOException {
        if (tileX < 0 || tileX >= ATLAS_TILES ||
                tileY < 0 || tileY >= ATLAS_TILES) {
            throw new IllegalArgumentException("Invalid atlas coordinates");
        }
        if (rotation % 90 != 0) {
            throw new IllegalArgumentException("Rotation must be a multiple of 90 degrees");
        }

        BufferedImage source = ImageIO.read(file);
        if (source == null) {
            throw new IOException("Unsupported image: " + file);
        }
        int frameSize = Math.min(source.getWidth(), source.getHeight());

        BufferedImage firstFrame = source.getSubimage(
                0, 0, frameSize, frameSize
        );

        BufferedImage tile = new BufferedImage(
                tileSize, tileSize, BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D resizeGraphics = tile.createGraphics();
        try {
            resizeGraphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
            );
            resizeGraphics.drawImage(firstFrame, 0, 0, tileSize, tileSize, null);
        } finally {
            resizeGraphics.dispose();
        }
        Graphics2D graphics = atlas.createGraphics();
        try {
            graphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
            );
            double half = tileSize / 2.0;
            int x = tileX * tileSize;
            int y = tileY * tileSize;

            AffineTransform transform = new AffineTransform();
            transform.translate(x + half, y + half);
            transform.rotate(Math.toRadians(rotation));
            transform.translate(-half, -half);
            graphics.drawImage(tile, transform, null);
        } finally {
            graphics.dispose();
        }
    }

    public void save(File output) throws IOException {
        File parent = output.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        if (!ImageIO.write(atlas, "PNG", output)) {
            throw new IOException("Could not write PNG");
        }
    }
}
