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
    private final Trajectory.Config config;
    private EncoderFollower leftEncf;
    private EncoderFollower rightEncf;
    private final List<Waypoint> waypoints = new ArrayList<>();

    public TankPathfinderCommand(final String name, final TankDriveSubsystem subsystem,
            final Trajectory.Config config) {
        super(name, subsystem);

        this.subsystem = subsystem;
        this.config = config;
    }

    public TankPathfinderCommand then(final Waypoint waypoint) {
        waypoints.add(waypoint);
        return this;
    }

    public TankPathfinderCommand then(final double x, final double y, final double angle) {
        return then(new Waypoint(x, y, angle));
    }

    public TankPathfinderCommand clear() {
        waypoints.clear();
        return this;
    }

    public TankPathfinderCommand compile() {
        final Waypoint[] points = waypoints.toArray(new Waypoint[0]);
        final Trajectory trajectory = Pathfinder.generate(points, config);
        final TankModifier modifier = subsystem.getModifier(trajectory);

        leftEncf = new EncoderFollower(modifier.getLeftTrajectory());
        subsystem.configureLeftEncoderFollower(leftEncf);

        rightEncf = new EncoderFollower(modifier.getRightTrajectory());
        subsystem.configureRightEncoderFollower(rightEncf);
        return this;
    }

    @Override
    protected void initialize() {
        super.initialize();
        if (leftEncf == null || rightEncf == null) {
            compile();
        }
    }

    @Override
    protected void execute() {
        super.execute();
        final double l = leftEncf.calculate(subsystem.getLeftEncoder().get());
        final double r = rightEncf.calculate(subsystem.getRightEncoder().get());

        final double gyroHeading = subsystem.getGyro().getAngle();
        final double desiredHeading = Pathfinder.r2d(leftEncf.getHeading());

        final double angleDifference = Pathfinder.boundHalfDegrees(desiredHeading - gyroHeading);
        final double turn = 0.8 * (-1.0 / 80.0) * angleDifference;

        subsystem.getDriveTrain().arcadeDrive(l + turn, r - turn);
    }

    @Override
    protected boolean isFinished() {
        return leftEncf.isFinished() && rightEncf.isFinished();
    }

}