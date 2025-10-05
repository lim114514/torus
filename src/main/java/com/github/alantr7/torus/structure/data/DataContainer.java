package com.github.alantr7.torus.structure.data;

import com.github.alantr7.bytils.buffer.ByteArrayReader;
import com.github.alantr7.bytils.buffer.ByteArrayWriter;
import com.github.alantr7.torus.math.StringPool;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class DataContainer {

    @Getter @Setter
    boolean isDirty;

    private final Map<String, Data<Object>> entries = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> Data<T> persist(String name, Data.Type<T> type) {
        return persist(name, type, null);
    }

    @SuppressWarnings("unchecked")
    public <T> Data<T> persist(String name, Data.Type<T> type, T defaultValue) {
        Data data = entries.computeIfAbsent(name, v -> {
            Data d = new Data(this, type);
            d.value = defaultValue;

            return d;
        });

        if (data.type != type)
            throw new RuntimeException("Conflicting data types!");

        return data;
    }

    public byte[] toBytes(StringPool keys) {
        ByteArrayWriter buffer = new ByteArrayWriter();
        buffer.writeU1(entries.size());

        entries.forEach((key, entry) -> {
            buffer.writeU1((entry.type.id << 4) | keys.pool(key));
            switch (entry.type.id) {
                // BYTE
                case 0 -> buffer.writeBytes(new byte[] { (byte) entry.value });
                // INT
                case 1 -> buffer.writeBytes(ByteArrayWriter.toBytes((int) entry.value, 4));
                // FLOAT
                case 2 -> buffer.writeBytes(ByteArrayWriter.toBytes(Float.floatToIntBits((float) entry.value), 4));
                // STRING
                case 3 -> buffer.writeString((String) entry.value);
                // BYTE[]
                case 4 -> {
                    buffer.writeU2(((byte[]) entry.value).length);
                    buffer.writeBytes((byte[]) entry.value);
                }
            }
        });

        return buffer.getBuffer();
    }

    public static DataContainer fromBytes(ByteArrayReader buffer, StringPool keys) {
        int entriesCount = buffer.readU1();
        DataContainer container = new DataContainer();

        for (int i = 0; i < entriesCount; i++) {
            int packed = buffer.readU1();
            int typeId = (packed >> 4) & 0xf;
            String key = keys.at(packed & 0xf);
            Data.Type type;
            Object value;

            switch (typeId) {
                // BYTE
                case 0 -> {
                    type = Data.Type.BYTE;
                    value = buffer.readBytes(1)[0];
                }
                // INT
                case 1 -> {
                    type = Data.Type.INT;
                    value = ByteArrayReader.toInt(buffer.readBytes(4));
                }
                // FLOAT
                case 2 -> {
                    type = Data.Type.INT;
                    value = Float.intBitsToFloat(ByteArrayReader.toInt(buffer.readBytes(4)));
                }
                // STRING
                case 3 -> {
                    type = Data.Type.STRING;
                    value = buffer.readString();
                }
                // BYTE[]
                case 4 -> {
                    type = Data.Type.BYTE_ARRAY;
                    value = buffer.readBytes(ByteArrayReader.toInt(buffer.readBytes(2)));
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
            }
        }

        return container;
    }

}
