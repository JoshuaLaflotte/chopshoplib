package com.chopshop166.chopshoplib.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitForChildren;

/**
 * A declarative command sequence class.
 * <p>
 * Allows creating command groups by chaining calls to {@link #then}
 */
public class CommandChain extends CommandGroup {

    /**
     * Create a CommandChain.
     */
    public CommandChain() {
        super();
    }

    /**
     * Create a CommandChain with the given name.
     * 
     * @param name The name of the command chain.
     */
    public CommandChain(final String name) {
        super(name);
    }

    /**
     * Create a CommandChain preloaded with commands to be run in parallel.
     * 
     * @param cmds The first commands to run.
     */
    public CommandChain(final Command... cmds) {
        super();
        addCommands(cmds);
    }

    /**
     * Create a CommandChain with a name and commands to run.
     * 
     * @param name The name of the command chain.
     * @param cmds The first commands to run.
     */
    public CommandChain(final String name, final Command... cmds) {
        super(name);
        addCommands(cmds);
    }

    /**
     * Do a set of commands after the ones already provided.
     * 
     * @param cmds The commands to run next.
     * @return {@code this} for chaining calls.
     */
    public CommandChain then(final Command... cmds) {
        addCommands(cmds);
        return this;
    }

    /**
     * Do a set of commands after the ones already provided, with a timeout.
     * 
     * @param timeout The maximum amount of time before moving on to the next
     *                commands.
     * @param cmds    The commands to run next.
     * @return {@code this} for chaining calls.
     */
    public CommandChain then(final double timeout, final Command... cmds) {
        addCommands(timeout, cmds);
        return this;
    }

    /**
     * Add all provided commands as a group.
     * 
     * @param cmds The commands to run next.
     */
    private void addCommands(final Command... cmds) {
        if (cmds.length == 1) {
            if (cmds[0] != null) {
                addSequential(cmds[0]);
            }
        } else if (cmds.length != 0) {
            for (final Command c : cmds) {
                if (c != null) {
                    addParallel(c);
                }
            }
            addSequential(new WaitForChildren());
        }
    }

    /**
     * Add all provided commands as a group with a timeout.
     * 
     * @param timeout The maximum amount of time before moving on to the next
     *                commands.
     * @param cmds    The commands to run next.
     */
    private void addCommands(final double timeout, final Command... cmds) {
        if (cmds.length == 1) {
            if (cmds[0] != null) {
                addSequential(cmds[0], timeout);
            }
        } else if (cmds.length != 0) {
            for (final Command c : cmds) {
                if (c != null) {
                    addParallel(c, timeout);
                }
            }
            addSequential(new WaitForChildren());
        }
    }
}
