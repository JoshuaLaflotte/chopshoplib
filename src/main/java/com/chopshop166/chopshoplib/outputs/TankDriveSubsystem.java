package com.chopshop166.chopshoplib.outputs;

import com.chopshop166.chopshoplib.commands.TankPathfinderCommand;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

public abstract class TankDriveSubsystem extends Subsystem {
    public abstract DifferentialDrive getDriveTrain();

    public abstract Integer getLeftEncoder();

    public abstract Integer getRightEncoder();

    public abstract void configureLeftEncoderFollower(EncoderFollower follower);

    public abstract void configureRightEncoderFollower(EncoderFollower follower);

    public abstract Gyro getGyro();

    public abstract TankModifier getModifier(Trajectory trajectory);

    public TankPathfinderCommand.Builder path(Trajectory.Config config) {
        return new TankPathfinderCommand.Builder(config);
    }
}