package com.textureconverter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {


    private static void zipOutput(Path outputFolder) throws IOException {
        Path outputDir = Path.of("output");
        Path zipFile = outputFolder.resolve("Pack.zip");

        Files.createDirectories(outputFolder);

        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(zipFile));
             Stream<Path> files = Files.walk(outputDir)) {
            files.filter(Files::isRegularFile).forEach(file -> {
                try {
                    // Keep paths such as gui/items.png inside the zip.
                    String entryName = outputDir.relativize(file)
                            .toString()
                            .replace('\\', '/');
                    zip.putNextEntry(new ZipEntry(entryName));
                    Files.copy(file, zip);
                    zip.closeEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static void buildAtlases(Path textureFolder, int tileSize, boolean version, boolean fromOffs){
        for(int i=0; i<2; i++){
            Path mappingFile = Path.of("src");
            if(!fromOffs) {
                //path to blocks
                if (i == 0) {
                    if (version) {
                        mappingFile = Path.of("src/main/resources/mappings/blockLocs1.14+.json");
                    } else {
                        mappingFile = Path.of("src/main/resources/mappings/blockLocs1.13-.json");
                    }
                }

                if (i == 1) {
                    mappingFile = Path.of("src/main/resources/mappings/itemLocs.json");
                }
            }
            else{
                mappingFile = Path.of("src/main/resources/mappings/oneOffs1.13-moon.json");
            }
            //root path
            Path inputRoot = textureFolder;

            try (Reader reader = Files.newBufferedReader(mappingFile)) {
                JsonObject mapping = JsonParser.parseReader(reader).getAsJsonObject();

                //set tileSize
                AtlasBuilder builder = new AtlasBuilder(tileSize, fromOffs);


                for (var item : mapping.getAsJsonArray("textures")) {
                    JsonObject texture = item.getAsJsonObject();

                    Path source = inputRoot.resolve(texture.get("source").getAsString()
                    );

                    //check if texture exists
                    if (!Files.isRegularFile(source)) {
                        Path fallback = Path.of("src/input/hardTextures")
                                .resolve(texture.get("source").getAsString());

                        if (Files.isRegularFile(fallback)) {
                            source = fallback;
                        } else {
                            System.err.println("Texture not found in selected folder or hardTextures: "
                                    + texture.get("source").getAsString());
                            continue;
                        }
                    }

                    int rotation = texture.has("rotation")
                            ? texture.get("rotation").getAsInt()
                            : 0;

                    builder.addTexture(
                            source.toFile(),
                            texture.get("tileX").getAsInt(),
                            texture.get("tileY").getAsInt(),
                            rotation
                    );
                }
                if(i==0) {
                    builder.save(Path.of("output/terrain.png").toFile());
                }
                if(i==1) {
                    builder.save(Path.of("output/gui/items.png").toFile());
                }
                System.out.println("Atlas created successfully!");

            } catch (Exception e) {
                System.err.println("Conversion failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public static void buildOneOffs(Path textureFolder, int tileSize, boolean version){
        Path mappingFile;
        if(version) {
            mappingFile = Path.of("src/main/resources/mappings/oneOffs1.14+.json");
        }
        else{
            mappingFile = Path.of("src/main/resources/mappings/oneOffs1.13-.json");
        }
        try (Reader reader = Files.newBufferedReader(mappingFile)) {
            JsonObject mapping = JsonParser.parseReader(reader).getAsJsonObject();

            for (var item : mapping.getAsJsonArray("files")) {
                JsonObject file = item.getAsJsonObject();

                String sourcePath = file.get("source").getAsString();
                Path source = textureFolder.resolve(sourcePath);
                if (!Files.isRegularFile(source)) {
                    Path fallback = Path.of("src/input/hardTextures").resolve(sourcePath);
                    if (Files.isRegularFile(fallback)) {
                        source = fallback;
                    } else {
                        System.err.println("One-off texture not found in selected folder or hardTextures: " + sourcePath);
                        continue;
                    }
                }

                Path destination = Path.of("output")
                        .resolve(file.get("destination").getAsString());

                Files.createDirectories(destination.getParent());
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("Could not copy one-off files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(Path textureFolder, int tileSize, boolean version, Path outputFolder) {
        Boolean fromOffs = false;
        buildAtlases(textureFolder, tileSize, version, fromOffs);
        buildOneOffs(textureFolder, tileSize, version);

        try {
            zipOutput(outputFolder);
            System.out.println("Created Pack");
        } catch (IOException e) {
            System.err.println("Could not create output.zip: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
