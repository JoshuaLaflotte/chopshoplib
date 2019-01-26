package com.chopshop166.chopshoplib.commands

import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.command.CommandGroup
import edu.wpi.first.wpilibj.command.ConditionalCommand
import edu.wpi.first.wpilibj.command.InstantCommand
import edu.wpi.first.wpilibj.command.PrintCommand
import edu.wpi.first.wpilibj.command.WaitCommand
import edu.wpi.first.wpilibj.command.WaitForChildren

fun sequence(name : String, items : SequentialBuilder.() -> Unit) : Command {
    val group = SequentialBuilder()
    group.items()
    val cmd = group.build()
    cmd.name = name
    return cmd
}

fun wait(time : Double) = WaitCommand(time)

fun exec(block : () -> Unit) = InstantCommand(block)

@DslMarker
annotation class CommandBuilderMarker

@CommandBuilderMarker
public abstract class BuilderBase {

    val cmds = arrayListOf<Pair<Command, Double>>()

    fun addCommand(command : Command, timeout : Double = Double.NaN) {
        cmds.add(Pair(command, timeout))
    }

    operator fun Command.unaryPlus() {
        addCommand(this)
    }

    operator fun String.unaryPlus() {
        addCommand(PrintCommand(this))
    }

    infix fun Command.timeout(timeout : kotlin.Double) {
        addCommand(this, timeout)
    }

    infix fun (()->Boolean).implies(onTrue : Command) : Command {
        val lambda = this
        return object : ConditionalCommand(onTrue) {
            override fun condition() = lambda()
        }
    }

    infix fun (()->Boolean).implies(commands : Pair<Command, Command>) : Command {
        val (onTrue, onFalse) = commands
        val lambda = this
        return object : ConditionalCommand(onTrue, onFalse) {
            override fun condition() = lambda()
        }
    }

    infix fun Command.otherwise(onFalse : Command) = Pair(this, onFalse)

    abstract fun build() : Command
}

public class SequentialBuilder : BuilderBase() {

    override fun build() =
        object : CommandGroup() {
            init {
                for ((cmd, timeout) in cmds) {
                    if(timeout.isNaN()) {
                        addSequential(cmd)
                    } else {
                        addSequential(cmd, timeout)
                    }
                }
            }
        }

    fun parallel(waitAfter : Boolean = true, body : ParallelBuilder.() -> Unit) {
        val par = ParallelBuilder(waitAfter)
        par.body()
        +par.build()
    }
}

class ParallelBuilder(val waitAfter : Boolean = true) : BuilderBase() {

    override fun build() =
        object : CommandGroup() {
            init {
                for ((cmd, timeout) in cmds) {
                    if(timeout.isNaN()) {
                        addParallel(cmd)
                    } else {
                        addParallel(cmd, timeout)
                    }
                }
                if(waitAfter) addSequential(WaitForChildren())
            }
        }

    fun sequential(body : SequentialBuilder.() -> Unit) {
        val seq = SequentialBuilder()
        seq.body()
        +seq.build()
    }
}

fun testSequence() : Command =
    sequence("Sample") {
        +"A"
        parallel {
            +"B"
            PrintCommand("C") timeout 2.0
            sequential {
                +"F1"
                wait(3.14)
                +"F2"
                exec {
                    System.out.println("2 + 2 = " + (2 + 2))
                }
            }
        }
        +"D"
        PrintCommand("E") timeout 3.0
        +"G"
        {-> true} implies (PrintCommand("It's true") otherwise PrintCommand("It's False"))
    }

fun testChain() : Command =
    CommandChain("SampleChain").apply {
        then(PrintCommand("A"))
        then(PrintCommand("B"),
             TimeoutCommand(2.0, PrintCommand("C")),
             CommandChain().apply {
                 then(PrintCommand("F1"))
                 then(WaitCommand(3.14))
                 then(PrintCommand("F2"))
                 then(InstantCommand() {
                     System.out.println("2 + 2 = " + (2 + 2))
                 })
             })
        then(PrintCommand("D"))
        then(3.0, PrintCommand("E"))
        then(PrintCommand("G"))
    }