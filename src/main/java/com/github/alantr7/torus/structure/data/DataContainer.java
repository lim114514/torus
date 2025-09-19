package com.github.alantr7.torus.structure.data;

import com.github.alantr7.bytils.buffer.ByteArrayReader;
import com.github.alantr7.bytils.buffer.ByteArrayWriter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class DataContainer {

    boolean isDirty;

    private final Map<String, Data<Object>> entries = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> Data<T> persist(String name, Data.Type<T> type) {
        Data<T> data = new Data<>(this, type);
        entries.put(name, (Data<Object>) data);

        return data;
    }

    @SuppressWarnings("unchecked")
    public <T> Data<T> persist(String name, Data.Type<T> type, T defaultValue) {
        Data<T> data = new Data<>(this, type);
        entries.put(name, (Data<Object>) data);

        data.value = defaultValue;
        return data;
    }

    public byte[] toBytes() {
        ByteArrayWriter buffer = new ByteArrayWriter();
        buffer.writeU1(entries.size());

        entries.forEach((key, entry) -> {
            buffer.writeString(key);
            buffer.writeU1(entry.type.id);
            switch (entry.type.id) {
                // INT
                case 0 -> buffer.writeBytes(ByteArrayWriter.toBytes((int) entry.value, 4));
                // FLOAT
                case 1 -> buffer.writeBytes(ByteArrayWriter.toBytes(Float.floatToIntBits((float) entry.value), 4));
                // STRING
                case 2 -> buffer.writeString((String) entry.value);
            }
        });

        return buffer.getBuffer();
    }

    public static DataContainer fromBytes(ByteArrayReader buffer) {
        int entriesCount = buffer.readU1();
        DataContainer container = new DataContainer();

        for (int i = 0; i < entriesCount; i++) {
            String key = buffer.readString();
            int typeId = buffer.readU1();
            Data.Type type;
            Object value;
            switch (typeId) {
                // INT
                case 0 -> {
                    type = Data.Type.INT;
                    value = ByteArrayReader.toInt(buffer.readBytes(4));
                }
                // FLOAT
                case 1 -> {
                    type = Data.Type.INT;
                    value = Float.intBitsToFloat(ByteArrayReader.toInt(buffer.readBytes(4)));
                }
                // STRING
                case 2 -> {
                    type = Data.Type.STRING;
                    value = buffer.readString();
                }
                default -> {
                    type = null;
                    value = null;
                }
            }

            if (type != null && value != null) {
                Data data = new Data(container, type);
                data.value = value;
                container.entries.put(key, data);

                Bukkit.broadcastMessage("    - DCE " + type.name + " - " + key + ": " + value);
            }
        }

        return container;
    }

}
