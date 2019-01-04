package com.chopshop166.chopshoplib.outputs;

import com.chopshop166.chopshoplib.commands.TankPathfinderCommand;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

/**
 * A {@link Subsystem} that contains information for a Tank Drive.
 */
public abstract class TankDriveSubsystem extends Subsystem {
    /**
     * Get the drive train from the robot.
     * <p>
     * Assume that the subsystem is using the built in WPIlib utilities.
     * 
     * @return The drive train.
     */
    public abstract DifferentialDrive getDriveTrain();

    /**
     * Get the left encoder value.
     * 
     * @return The left encoder value.
     */
    public abstract Integer getLeftEncoder();

    /**
     * Get the right encoder value.
     * 
     * @return The right encoder value.
     */
    public abstract Integer getRightEncoder();

    /**
     * Configure the left {@link EncoderFollower} for this particular drive train.
     * 
     * @param follower The follower to modify.
     */
    public abstract void configureLeftEncoderFollower(EncoderFollower follower);

    /**
     * Configure the right {@link EncoderFollower} for this particular drive train.
     * <p>
     * Assumes the same values as the left by default.
     * 
     * @param follower The follower to modify.
     */
    public void configureRightEncoderFollower(EncoderFollower follower) {
        configureLeftEncoderFollower(follower);
    }

    /**
     * Get the {@link Gyro} instance on the robot.
     * 
     * @return The {@link Gyro} in question.
     */
    public abstract Gyro getGyro();

    /**
     * Get a modifier for a {@link Trajectory} for this particular drive train.
     * 
     * @param trajectory The source trajectory.
     * @return A modifier for this drive train.
     */
    public abstract TankModifier getModifier(Trajectory trajectory);

    /**
     * Convenience function to create a builder.
     * 
     * @param config The configuration for the path.
     * @return The builder object.
     */
    public TankPathfinderCommand.Builder path(Trajectory.Config config) {
        return new TankPathfinderCommand.Builder(config);
    }
}