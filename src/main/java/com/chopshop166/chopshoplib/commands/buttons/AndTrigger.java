package com.chopshop166.chopshoplib.commands.buttons;

import static java.util.Arrays.stream;

import edu.wpi.first.wpilibj.buttons.Trigger;

/**
 * A {@link Trigger} that triggers if every trigger passed to it is triggered.
 */
public class AndTrigger extends Trigger {

    private final Trigger[] triggers;

    public AndTrigger(final Trigger... triggers) {
        super();
        this.triggers = triggers.clone();
    }

    @Override
    public boolean get() {
        return stream(triggers).map(Trigger::get).reduce(true, (a, b) -> a && b);
    }
}
