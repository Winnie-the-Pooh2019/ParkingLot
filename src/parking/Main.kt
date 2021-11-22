package parking

enum class Action(val purpose: String) {
    BY("by"),
    CREATE("create"),
    PARK("park"),
    LEAVE("leave"),
    STATUS("status"),
    EXIT("exit")
}

enum class ByAction(val purpose: String) {
    REG("reg"),
    SPOT("spot"),
}

data class Car(val number: String, val color: String)

fun main() {
    var parking = Array<Car?>(0) { null }

    whileLoop@ while (true) {
        val input = readLine()!!.split(" ")

        when {
            input[0].contains(Action.BY.purpose) -> {
                if (parking.isEmpty()) {
                    println("Sorry, a parking lot has not been created.")
                    continue
                }

                val arguments = input[0].split("_").filter { it != Action.BY.purpose }.zipWithNext()[0]

                val runRes = runBy(arguments, parking, input[1])

                if (runRes == "")
                    println(
                        "No cars with ${
                            if (arguments.second == ByAction.REG.purpose)
                                "registration number"
                            else "color"
                        } ${input[1]} were found."
                    )
                else
                    println(runRes)
            }

            input[0] == Action.CREATE.purpose -> {
                parking = Array(input[1].toInt()) { null }
                println("Created a parking lot with ${input[1].toInt()} spots.")
            }

            input[0] == Action.PARK.purpose -> {
                if (parking.isEmpty()) {
                    println("Sorry, a parking lot has not been created.")
                    continue
                }

                val index = parking.indexOfFirst { it == null }

                if (index == -1) {
                    println("Sorry, the parking lot is full.")
                    continue
                }

                parking[index] = Car(input[1], input[2])

                println("${parking[index]!!.color} car parked in spot ${index + 1}.")
            }

            input[0] == Action.LEAVE.purpose -> {
                if (parking.isEmpty()) {
                    println("Sorry, a parking lot has not been created.")
                    continue
                }

                if (parking[input[1].toInt() - 1] == null) {
                    println("There is no car in spot ${input[1].toInt()}.")
                } else {
                    parking[input[1].toInt() - 1] = null
                    println("Spot ${input[1].toInt()} is free.")
                }
            }

            input[0] == Action.STATUS.purpose -> {
                if (parking.isEmpty()) {
                    println("Sorry, a parking lot has not been created.")
                    continue
                }

                val parkMap = parking.getStatus()

                if (parkMap.isEmpty())
                    println("Parking lot is empty.")
                else
                    parkMap.forEach { println("${it.key + 1} ${it.value!!.number} ${it.value!!.color}") }
            }

            input[0] == Action.EXIT.purpose -> return
        }
    }
}

fun runBy(arguments: Pair<String, String>, parking: Array<Car?>, param: String): String {
    return when (arguments.first) {
        ByAction.REG.purpose -> {
            parking.regByColor(param)
        }
        ByAction.SPOT.purpose -> {
            if (arguments.second == ByAction.REG.purpose)
                parking.spotByReg(param)
            else
                parking.spotByColor(param)
        }
        else -> {
            ""
        }
    }
}

fun Array<Car?>.spotByReg(number: String) =
    this.mapIndexed { index, car -> index to car }.filter { it.second != null && it.second!!.number == number }
        .joinToString(", ") { (it.first + 1).toString() }

fun Array<Car?>.spotByColor(color: String) =
    this.mapIndexed { index, car -> index to car }
        .filter { it.second != null && it.second!!.color.lowercase() == color.lowercase() }
        .joinToString(", ") { (it.first + 1).toString() }

fun Array<Car?>.regByColor(color: String) =
    this.filterNotNull().filter { it.color.lowercase() == color.lowercase() }.joinToString(", ") { it.number }

fun Array<Car?>.getStatus() = this.mapIndexed { index, car -> index to car }.toMap().filter { it.value != null }