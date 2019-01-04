package com.chopshop166.chopshoplib.outputs;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

public abstract class TankDriveSubsystem extends Subsystem {
    public abstract DifferentialDrive getDriveTrain();

    public abstract Supplier<Integer> getLeftEncoder();

    public abstract Supplier<Integer> getRightEncoder();

    public abstract void configureLeftEncoderFollower(EncoderFollower follower);

    public abstract void configureRightEncoderFollower(EncoderFollower follower);

    public abstract Gyro getGyro();

    public abstract TankModifier getModifier(Trajectory trajectory);
}