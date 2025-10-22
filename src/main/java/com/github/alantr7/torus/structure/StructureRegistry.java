package com.github.alantr7.torus.structure;

import com.github.alantr7.bukkitplugin.annotations.core.Invoke;
import com.github.alantr7.bukkitplugin.annotations.core.InvokePeriodically;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.bytils.buffer.ByteArrayReader;
import com.github.alantr7.bytils.buffer.ByteArrayWriter;
import com.github.alantr7.torus.TorusPlugin;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.*;

@Singleton
public class StructureRegistry {

    private int nextStructureId = 2;

    private final Map<String, Structure> loaded = new HashMap<>();

    private final Map<Integer, Structure> loadedByNumericIds = new LinkedHashMap<>();

    private final Map<String, Integer> structuresIds = new HashMap<>();

    private final Set<Structure> saveQuery = new LinkedHashSet<>();

    @Invoke(Invoke.Schedule.BEFORE_PLUGIN_ENABLE)
    private void init() {
        load();
        registerStructures();
    }

    private void load() {
        File file = new File(TorusPlugin.getInstance().getDataFolder(), "id_map.dat");
        if (!file.exists())
            return;

        try {
            ByteArrayReader reader = new ByteArrayReader(Files.readAllBytes(file.toPath()));
            while (reader.hasNext()) {
                int numericId = ByteArrayReader.toInt(reader.readBytes(2));
                String id = reader.readString();

                structuresIds.put(id, numericId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @InvokePeriodically(interval = 20 * 60)
    @Invoke(Invoke.Schedule.AFTER_PLUGIN_DISABLE)
    public void save() {
        if (saveQuery.isEmpty())
            return;

        File file = new File(TorusPlugin.getInstance().getDataFolder(), "id_map.dat");
        file.getParentFile().mkdirs();

        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(raf.length());

            ByteArrayWriter writer = new ByteArrayWriter();

            for (Structure str : saveQuery) {
                writer.writeU2(str.numericId);
                writer.writeString(str.id);
            }

            saveQuery.clear();

            raf.write(writer.getBuffer());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerStructures() {
        register(Structures.BLAST_FURNACE);
        register(Structures.BLOCK_BREAKER);
        register(Structures.PUMP);
        register(Structures.ORE_CRUSHER);
        register(Structures.ORE_WASHER);
        register(Structures.QUARRY);

        register(Structures.ENERGY_CABLE);
        register(Structures.ITEM_CABLE);
        register(Structures.FLUID_CABLE);

        register(Structures.CONNECTOR);

        register(Structures.FLUID_TANK);

        register(Structures.TURRET);

        register(Structures.COAL_GENERATOR);
        register(Structures.SOLAR_GENERATOR);
        register(Structures.POWER_BANK);
    }

    public void register(Structure structure) {
        if (structure.numericId != -1) {
            throw new RuntimeException("Structure must not have numeric id already assigned when registering it");
        }

        if (structuresIds.containsKey(structure.id)) {
            structure.numericId = structuresIds.get(structure.id);
        } else {
            structure.numericId = nextStructureId++;
            structuresIds.put(structure.id, structure.numericId);
            saveQuery.add(structure);
        }

        loaded.put(structure.id, structure);
        loadedByNumericIds.put(structure.numericId, structure);
    }

    public Structure getStructure(String id) {
        return loaded.get(id);
    }

    public Structure getStructure(int id) {
        return loadedByNumericIds.get(id);
    }

}
