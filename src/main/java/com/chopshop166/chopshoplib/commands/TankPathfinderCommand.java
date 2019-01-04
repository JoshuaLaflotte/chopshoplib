package com.chopshop166.chopshoplib.commands;

import java.util.ArrayList;
import java.util.List;

import com.chopshop166.chopshoplib.outputs.TankDriveSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

public final class TankPathfinderCommand extends Command {

    private final TankDriveSubsystem subsystem;
    private final EncoderFollower leftEnc;
    private final EncoderFollower rightEnc;

    /**
     * Build a {@link TankPathfinderCommand} from individual {@link Waypoint}s.
     */
    public static class Builder {
        private final Trajectory.Config config;
        private final List<Waypoint> waypoints = new ArrayList<>();

        /**
         * Store the config used to generate the {@link Trajectory}.
         * 
         * @param config The config to be stored.
         */
        public Builder(final Trajectory.Config config) {
            this.config = config;
        }

        /**
         * Add a {@link Waypoint} to the path.
         * 
         * @param waypoint The next location to move to.
         * @return {@code this} for chaining.
         */
        public Builder then(final Waypoint waypoint) {
            waypoints.add(waypoint);
            return this;
        }

        /**
         * Add a {@link Waypoint} to the path.
         * 
         * @param x     The next x coordinate to move to.
         * @param y     The next y coordinate to move to.
         * @param angle The next exit angle.
         * @return {@code this} for chaining.
         */
        public Builder then(final double x, final double y, final double angle) {
            return then(new Waypoint(x, y, angle));
        }

        /**
         * Clear all {@link Waypoint}s from the path.
         * 
         * @return {@code this} for chaining.
         */
        public Builder clear() {
            waypoints.clear();
            return this;
        }

        /**
         * Create a {@link Command} from the provided path.
         * 
         * @param name      The name of the command.
         * @param subsystem The Tank Drive to use for driving.
         * @return The created command.
         */
        public TankPathfinderCommand compile(final String name, final TankDriveSubsystem subsystem) {
            final Waypoint[] points = waypoints.toArray(new Waypoint[0]);
            final Trajectory trajectory = Pathfinder.generate(points, config);
            final TankModifier modifier = subsystem.getModifier(trajectory);

            final EncoderFollower leftEnc = new EncoderFollower(modifier.getLeftTrajectory());
            subsystem.configureLeftEncoderFollower(leftEnc);

            final EncoderFollower rightEnc = new EncoderFollower(modifier.getRightTrajectory());
            subsystem.configureRightEncoderFollower(rightEnc);
            return new TankPathfinderCommand(name, subsystem, leftEnc, rightEnc);
        }
    }

    /**
     * Create a Command.
     * 
     * @param name      The name of the command.
     * @param subsystem The drive subsystem to operate on.
     * @param leftEnc   The left encoder follower.
     * @param rightEnc  The right encoder follower.
     */
    protected TankPathfinderCommand(final String name, final TankDriveSubsystem subsystem,
            final EncoderFollower leftEnc, final EncoderFollower rightEnc) {
        super(name, subsystem);
        this.subsystem = subsystem;
        this.leftEnc = leftEnc;
        this.rightEnc = rightEnc;
    }

    @Override
    protected void execute() {
        super.execute();
        final double l = leftEnc.calculate(subsystem.getLeftEncoder());
        final double r = rightEnc.calculate(subsystem.getRightEncoder());

        final double gyroHeading = subsystem.getGyro().getAngle();
        final double desiredHeading = Pathfinder.r2d(leftEnc.getHeading());

        final double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading - gyroHeading);
        final double turn = 0.8 * (-1.0 / 80.0) * angleDifference;

        subsystem.getDriveTrain().tankDrive(l + turn, r - turn);
    }

    @Override
    protected boolean isFinished() {
        return leftEnc.isFinished() && rightEnc.isFinished();
    }

}