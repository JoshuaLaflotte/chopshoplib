package com.chopshop166.chopshoplib.sensors;

import edu.wpi.first.wpilibj.GyroBase;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public interface PIDGyro extends Gyro, PIDSource, Sendable {

    public static PIDGyro wrap(final PIDGyro encoder) {
        return encoder;
    }

    public static <T extends Gyro & PIDSource & Sendable> PIDGyro wrap(final T gyro) {
        return new PIDGyro() {

            @Override
            public double getAngle() {
                return gyro.getAngle();
            }

            @Override
            public PIDSourceType getPIDSourceType() {
                return gyro.getPIDSourceType();
            }

            @Override
            public double getRate() {
                return gyro.getRate();
            }

            @Override
            public void reset() {
                gyro.reset();
            }

            @Override
            public double pidGet() {
                return gyro.pidGet();
            }

            @Override
            public void setPIDSourceType(PIDSourceType pidSource) {
                gyro.setPIDSourceType(pidSource);
            }

            @Override
            public void calibrate() {
                gyro.calibrate();
            }

            @Override
            public void free() {
                gyro.free();
            }

            @Override
            public void close() throws Exception {
                gyro.close();
            }

            @Override
            public String getName() {
                return gyro.getName();
            }

            @Override
            public void setName(String name) {
                gyro.setName(name);
            }

            @Override
            public String getSubsystem() {
                return gyro.getSubsystem();
            }

            @Override
            public void setSubsystem(String subsystem) {
                gyro.setSubsystem(subsystem);
            }

            @Override
            public void initSendable(SendableBuilder builder) {
                gyro.initSendable(builder);
            }
        };
    }

    public static PIDGyro mock() {
        return wrap(new GyroBase() {
            private double angle;
            private double rate;

            public void setAngle(final double angle) {
                this.angle = angle;
            }

            public void setRate(final double rate) {
                this.rate = rate;
            }

            @Override
            public double getAngle() {
                return angle;
            }

            @Override
            public double getRate() {
                return rate;
            }

            @Override
            public void reset() {
                angle = 0;
            }

            @Override
            public void calibrate() {
            }

            @Override
            public void free() {
            }

            @Override
            public void initSendable(SendableBuilder builder) {
                builder.setSmartDashboardType("Gyro");
                builder.addDoubleProperty("Value", this::getAngle, this::setAngle);
                builder.addDoubleProperty("Rate", this::getRate, this::setRate);
            }
        });
    }

}