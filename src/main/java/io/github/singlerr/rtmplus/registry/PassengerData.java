package io.github.singlerr.rtmplus.registry;

import jp.ngt.rtm.entity.train.EntityTrainBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Getter
@Setter
@AllArgsConstructor
public class PassengerData {

    private EntityTrainBase train;

    private Entity passengerEntity;

    private double rotationOffset;

    private Vec3d vectorOffset;
}
