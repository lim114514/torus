package com.github.alantr7.torus.math;

import org.joml.Vector2f;
import org.joml.Vector2i;
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

}
