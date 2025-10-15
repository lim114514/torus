package com.github.alantr7.torus.world;

import com.github.alantr7.bytils.buffer.ByteArrayReader;
import com.github.alantr7.bytils.buffer.ByteArrayWriter;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.math.StringPool;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import org.bukkit.Bukkit;
import org.joml.Vector2i;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TorusRegion {

    public static final int SECTION_LENGTH_KEYS             = 2048;
    public static final int SECTION_LENGTH_CHUNKS_OFFSETS   = 3072;
    public static final int SECTION_LENGTH_CHUNK_DATA       = 16_384;

    public static final int CHUNKS_IN_REGION = 1024;

    public final TorusWorld world;
    public final File regionFile;

    public final int x, z;

    public final StringPool strings = new StringPool();
    public final Map<Vector2i, TorusChunk> chunks = new HashMap<>();

    public byte[] header = new byte[SECTION_LENGTH_KEYS + SECTION_LENGTH_CHUNKS_OFFSETS];

    public TorusRegion(TorusWorld world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
        this.regionFile = new File(world.regionsDirectory, "r." + x + "." + z + ".dat");
    }

    private void createFile() {
        try {
            RandomAccessFile file = new RandomAccessFile(regionFile, "rw");
            file.setLength(SECTION_LENGTH_KEYS + SECTION_LENGTH_CHUNKS_OFFSETS);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void load() throws Exception {
        if (!regionFile.exists())
            return;

        RandomAccessFile raf = new RandomAccessFile(regionFile, "r");
        raf.readFully(header);

        // Read string pool
        for (int i = 0; i < SECTION_LENGTH_KEYS; i += 16) {
            if (header[i] == 0)
                break;

            String rawString = new String(header, i, 16);
            strings.pool(rawString.substring(0, rawString.indexOf(0)));
        }

        raf.close();
    }

    public void save() throws Exception {
        boolean hasDirtyChunks = false;
        for (TorusChunk chunk : chunks.values()) {
            if (chunk.isDirty) {
                hasDirtyChunks = true;
                break;
            }
        }

        if (!hasDirtyChunks)
            return;

        if (!regionFile.exists()) {
            createFile();
        }

        RandomAccessFile raf = new RandomAccessFile(regionFile, "rw");
        for (TorusChunk chunk : chunks.values()) {
            saveChunk(raf, chunk);
        }

        raf.close();
    }

    private void loadChunk(RandomAccessFile raf, TorusChunk chunk) throws Exception {
        int index = (chunk.position.x & 31) + (chunk.position.y & 31) * 32;
        int regionFileOffset = ByteArrayReader.toInt(header, SECTION_LENGTH_KEYS + index * 3, 3);

        // Chunk is empty
        if (regionFileOffset == 0) {
            return;
        }

        raf.seek(regionFileOffset);

        int chunkSize = ByteArrayReader.toInt(new byte[] { raf.readByte(), raf.readByte() });
        chunk.size = chunkSize;

        byte[] buffer = new byte[chunkSize];
        raf.readFully(buffer);

        ByteArrayReader reader = new ByteArrayReader(buffer);
        while (reader.hasNext()) {
            int basePointer = reader.getPointer();
            int structureId = ByteArrayReader.toInt(reader.readBytes(2));

            // Occupation
            if (structureId == 1) {
                int packedXZ = reader.readU1() & 0xff;
                int x = (packedXZ >> 4) & 0x0f;
                int z = packedXZ & 0x0f;
                int y = ByteArrayReader.toInt(reader.readBytes(2));

                int packedStructureXZ = reader.readU1() & 0xff;
                int sX = ((packedStructureXZ >> 4)) - 7;
                int sZ = (packedStructureXZ & 0x0f) - 7;
                int sY = ByteArrayReader.toInt(reader.readBytes(2));

                BlockLocation occupation = new BlockLocation(world, x + (chunk.position.x << 4), y, z + (chunk.position.y << 4));
                BlockLocation structureLocation = occupation.getRelative(sX, sY - y, sZ);

                chunk.occupations.put(occupation, structureLocation);
            }

            // Structure
            else {
                int length = ByteArrayReader.toInt(reader.readBytes(2));
                try {
                    StructureInstance structure = StructureInstance.fromBytes(this, chunk, reader, structureId);
                    if (structure != null) {
                        chunk._placeStructureWithOccupations(structure);
                    } else {
                        System.err.println("Could not load structure in " + chunk.position.x + ", " + chunk.position.y + " at offset #" + basePointer);
                    }
                } catch (Exception e) {
                    System.err.println("Corrupted structure in region " + x + ", " + z + " with ID: " + TorusPlugin.getInstance().getStructureRegistry().getStructure(structureId) + " (" + structureId + ")");
                }
                reader.setPointer(basePointer + length + 4);
            }
        }
    }

    // Finds empty space to save a NEW chunk
    // Go through header and find unsaved chunk. Check if that location is busy.
    private int _allocateChunkData(int chunkIndex) {
        int end = (int) regionFile.length();
        System.arraycopy(ByteArrayWriter.toBytes(end, 3), 0, header, SECTION_LENGTH_KEYS + chunkIndex * 3, 3);
        return end;
    }

    private void saveChunk(RandomAccessFile raf, TorusChunk chunk) throws Exception {
        if (!chunk.isDirty)
            return;

        // Find chunk's section in the file
        int index = (chunk.position.x & 31) + (chunk.position.y & 31) * 32;
        int regionFileOffset = ByteArrayReader.toInt(header, SECTION_LENGTH_KEYS + index * 3, 3);
        if (regionFileOffset == 0) {
            regionFileOffset = _allocateChunkData(index);

            raf.seek(SECTION_LENGTH_KEYS + index * 3);
            raf.write(ByteArrayWriter.toBytes(regionFileOffset, 3));
        }

        raf.seek(regionFileOffset + 2);

        ByteArrayWriter writer = new ByteArrayWriter(SECTION_LENGTH_CHUNK_DATA);
        int keysLength = strings.getSize();

        // Save structures
        for (StructureInstance structure : chunk.structures.values()) {
            structure.save(writer, strings);
        }

        // Save occupations that don't belong to this chunk
        for (Map.Entry<BlockLocation, BlockLocation> entry : chunk.occupations.entrySet()) {
            BlockLocation occupation = entry.getKey();
            BlockLocation structureLocation = entry.getValue();

            if (chunk.contains(structureLocation))
                continue;

            writer.writeU2(1);
            writer.writeU1(((occupation.x & 15) << 4) | (occupation.z & 15));
            writer.writeU2(occupation.y);

            writer.writeU1(((structureLocation.x - occupation.x + 7) << 4) | (structureLocation.z - occupation.z + 7));
            writer.writeU2(structureLocation.y);
        }

        raf.write(writer.getBuffer());
        int chunkSize = writer.getPointer();

        chunk.size = chunkSize;

        // Save new keys
        if (keysLength < strings.getSize()) {
            for (int i = keysLength; i < strings.getSize(); i++) {
                raf.seek(i * 16L);

                String key = strings.at(i);
                if (key.length() > 16) {
                    throw new Exception("Key length must be shorter than or equal 16.");
                }
                raf.write(strings.at(i).getBytes(StandardCharsets.UTF_8));
            }
        }

        // Save chunk size
        raf.seek(regionFileOffset);
        raf.write(ByteArrayWriter.toBytes(chunkSize, 2));

        chunk.isDirty = false;
    }

    TorusChunk getOrLoadChunk(int x, int z) {
        return chunks.computeIfAbsent(new Vector2i(x, z), k -> {
            TorusChunk chunk = new TorusChunk(world, k);
            if (!regionFile.exists())
                return chunk;

            try {
                RandomAccessFile raf = new RandomAccessFile(regionFile, "r");
                loadChunk(raf, chunk);
                raf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return chunk;
        });
    }

    public Collection<TorusChunk> getLoadedChunks() {
        return chunks.values();
    }

}