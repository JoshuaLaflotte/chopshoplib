package com.chopshop166.chopshoplib.sensors;

import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * A {@link Potentiometer} that can be controlled via the dashboard.
 */
public class MockPotentiometer extends SendableBase implements Potentiometer {

    private PIDSourceType sourceType;
    private double value;

    public void set(final double value) {
        this.value = value;
    }

    @Override
    public void initSendable(final SendableBuilder builder) {
        builder.setSmartDashboardType("Potentiometer");
        builder.addDoubleProperty("Value", this::get, this::set);
    }

    @Override
    public void setPIDSourceType(final PIDSourceType pidSource) {
        sourceType = pidSource;
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return sourceType;
    }

    @Override
    public double pidGet() {
        return get();
    }

    @Override
    public double get() {
        return value;
    }
}