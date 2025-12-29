package com.github.alantr7.torus.model.de_provider;

import com.github.alantr7.torus.machine.OreCrusherInstance;
import com.github.alantr7.torus.machine.Windmill;
import com.github.alantr7.torus.machine.WindmillInstance;
import com.github.alantr7.torus.model.animation.Animation;
import com.github.alantr7.torus.model.animation.AnimationProvider;
import com.github.alantr7.torus.structure.StructureInstance;
import com.github.alantr7.torus.structure.Structures;
import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

public class DisplayEntitiesDefaultAnimations {

    public static final AnimationProvider<Animation> WINDMILL_BLADES_ROT = (structure, part) -> new Animation() {
        float angle = (float) (Math.random() * Math.PI / 2f) * ((WindmillInstance) structure).getEfficiency();
        @Override
        public void tick() {
            ((DisplayEntitiesPartModel) part).entityReferences.forEach(ref -> {
                Display entity = ref.getEntity();
                Transformation transform = entity.getTransformation();

                Quaternionf rotation = transform.getLeftRotation();
                rotation.setAngleAxis(angle, 0, 0, 1);

                entity.setTransformation(transform);
                entity.setInterpolationDelay(0);
                entity.setInterpolationDuration(20);
            });
            angle += Windmill.MAXIMUM_SPEED * ((WindmillInstance) structure).getEfficiency();
        }
    };

    public static final AnimationProvider<Animation> ORE_CRUSHER_WHEEL_LEFT_ROT = (structure, part) -> new Animation() {
        @Override
        public void tick() {
            if (!((OreCrusherInstance) structure).wheelsSpinning)
                return;

            ((DisplayEntitiesPartModel) part).entityReferences.forEach(ref -> {
                Display display = ref.getEntity();
                Transformation transformation = display.getTransformation();
                display.setTransformation(new Transformation(transformation.getTranslation(), new AxisAngle4f(((OreCrusherInstance) structure).wheelsAngle * 0.9f, 0, 0, 1f), transformation.getScale(), new AxisAngle4f(transformation.getRightRotation())));
                display.setInterpolationDelay(0);
                display.setInterpolationDuration(20);
            });
        }
    };

    public static final AnimationProvider<Animation> ORE_CRUSHER_WHEEL_RIGHT_ROT = (structure, part) -> new Animation() {
        @Override
        public void tick() {
            if (!((OreCrusherInstance) structure).wheelsSpinning)
                return;

            ((DisplayEntitiesPartModel) part).entityReferences.forEach(ref -> {
                Display display = ref.getEntity();
                Transformation transformation = display.getTransformation();
                display.setTransformation(new Transformation(transformation.getTranslation(), new AxisAngle4f(((OreCrusherInstance) structure).wheelsAngle, 0, 0, 1f), transformation.getScale(), new AxisAngle4f(transformation.getRightRotation())));
                display.setInterpolationDelay(0);
                display.setInterpolationDuration(20);
            });
        }
    };

    private static final Map<String, Map<String, AnimationProvider<Animation>>> defaultAnimations = Map.of(
      Structures.ORE_CRUSHER.id, Map.of("wheel_left", ORE_CRUSHER_WHEEL_LEFT_ROT, "wheel_right", ORE_CRUSHER_WHEEL_RIGHT_ROT),
      Structures.WINDMILL.id, Map.of("blades", WINDMILL_BLADES_ROT)
    );

    public static void apply(StructureInstance instance) {
        Map<String, AnimationProvider<Animation>> animations = defaultAnimations.get(instance.structure.id);
        if (animations == null)
            return;

        animations.forEach((partName, provider) -> {
            if (instance.model.getPart(partName) instanceof DisplayEntitiesPartModel de) {
                de.setAnimation(provider.create(instance, de));
            }
        });
    }

}
