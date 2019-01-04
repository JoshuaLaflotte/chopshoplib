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
    private EncoderFollower leftEnc;
    private EncoderFollower rightEnc;

    public static class Builder {
        private final Trajectory.Config config;
        private final List<Waypoint> waypoints = new ArrayList<>();

        public Builder(final Trajectory.Config config) {
            this.config = config;
        }

        public Builder then(final Waypoint waypoint) {
            waypoints.add(waypoint);
            return this;
        }

        public Builder then(final double x, final double y, final double angle) {
            return then(new Waypoint(x, y, angle));
        }

        public Builder clear() {
            waypoints.clear();
            return this;
        }

        public TankPathfinderCommand compile(final String name, final TankDriveSubsystem subsystem) {
            final Waypoint[] points = waypoints.toArray(new Waypoint[0]);
            final Trajectory trajectory = Pathfinder.generate(points, config);
            final TankModifier modifier = subsystem.getModifier(trajectory);

            EncoderFollower leftEnc = new EncoderFollower(modifier.getLeftTrajectory());
            subsystem.configureLeftEncoderFollower(leftEnc);

            EncoderFollower rightEnc = new EncoderFollower(modifier.getRightTrajectory());
            subsystem.configureRightEncoderFollower(rightEnc);
            return new TankPathfinderCommand(name, subsystem, leftEnc, rightEnc);
        }
    }

    protected TankPathfinderCommand(String name, TankDriveSubsystem subsystem, EncoderFollower leftEnc,
            EncoderFollower rightEnc) {
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