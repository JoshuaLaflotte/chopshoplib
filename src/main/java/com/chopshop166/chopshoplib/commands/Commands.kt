package com.chopshop166.chopshoplib.commands

import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.command.CommandGroup
import edu.wpi.first.wpilibj.command.PrintCommand
import edu.wpi.first.wpilibj.command.WaitForChildren

fun sequence(name : String, items : SequentialBuilder.() -> Unit) : Command {
    val group = SequentialBuilder()
    group.items()
    val cmd = group.build()
    cmd.name = name
    return cmd
}

@DslMarker
annotation class CommandBuilderMarker

@CommandBuilderMarker
public abstract class BuilderBase {

    val cmds = arrayListOf<Pair<Command, Double>>()

    operator fun Command.unaryPlus() {
        cmds.add(Pair(this, Double.NaN))
    }

    infix fun Command.timeout(timeout : Double) {
        cmds.add(Pair(this, timeout))
    }

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
        +PrintCommand("A")
        parallel {
            +PrintCommand("B")
            PrintCommand("C") timeout 2.0
            sequential {
                +PrintCommand("F1")
                +PrintCommand("F2")
            }
        }
        +PrintCommand("D")
        PrintCommand("E") timeout 3.0
        +PrintCommand("G")
    }

fun testChain() : Command =
    CommandChain("SampleChain").apply {
        then(PrintCommand("A"))
        then(PrintCommand("B"),
             TimeoutCommand(2.0, PrintCommand("C")),
             CommandChain().apply {
                 then(PrintCommand("F1"))
                 then(PrintCommand("F2"))
             })
        then(PrintCommand("D"))
        then(3.0, PrintCommand("E"))
        then(PrintCommand("G"))
    }