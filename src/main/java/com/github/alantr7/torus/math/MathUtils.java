package com.github.alantr7.torus.math;

import com.github.alantr7.torus.world.Direction;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class MathUtils {

    public static final Vector3i V3I_ZERO = new Vector3i(0, 0, 0);
    public static final Vector3i V3I_ONE = new Vector3i(1, 1, 1);

    public static void applyRotation(Vector3f vector, float angle) {
        double distance = new Vector2f(vector.x, vector.z).length();
        double currentAngle = Math.toDegrees(Math.atan2(vector.x, vector.z));
        vector.x = (float) (Math.sin(Math.toRadians(currentAngle -angle)) * distance);
        vector.z = (float) (Math.cos(Math.toRadians(currentAngle -angle)) * distance);
    }

    public static boolean hasFlag(int mask, int flag) {
        return (mask & flag) != 0;
    }

    public static int setFlag(int mask, int flag, boolean toggle) {
        return toggle ? (mask | flag) : (mask & ~flag);
    }

    public static byte[] rotateVectors(byte[] parentBounds, Direction direction) {
        byte[] bounds = new byte[parentBounds.length];

        for (int i = 0; i < bounds.length; i += 3) {
            float distance = (float) Math.sqrt(parentBounds[i] * parentBounds[i] + parentBounds[i + 2] * parentBounds[i + 2]);
            float angle = (float) Math.toRadians(direction.rotH) + (float) Math.atan2(parentBounds[i + 2], parentBounds[i]);

            bounds[i] = (byte) Math.round((float) Math.cos(angle) * distance);
            bounds[i + 2] = (byte) Math.round((float) Math.sin(angle) * distance);
            bounds[i + 1] = parentBounds[i + 1];
        }

        return bounds;
    }

    public static float[] rotateVectors(float[] parentBounds, Direction direction) {
        float[] bounds = new float[parentBounds.length];

        for (int i = 0; i < bounds.length; i += 3) {
            float distance = (float) Math.sqrt(parentBounds[i] * parentBounds[i] + parentBounds[i + 2] * parentBounds[i + 2]);
            float angle = (float) Math.toRadians(direction.rotH) + (float) Math.atan2(parentBounds[i + 2], parentBounds[i]);

            bounds[i] = (float) Math.cos(angle) * distance;
            bounds[i + 2] = (float) Math.sin(angle) * distance;
            bounds[i + 1] = parentBounds[i + 1];
        }

        return bounds;
    }

}
