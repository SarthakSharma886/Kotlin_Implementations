fun main() {

    /* High level functions are those functions which accept function as parameters or can return a function or both */

    val program = Program()


    program.addTwoNumbers(2, 7, object : MyInterface {   // print result Using Interface

        override fun execute(sum: Int) {
            println(sum)    // Body
        }
    })


    val myLambda: (Int) -> Unit = { s: Int -> println(s)}   // Lambda Expression [ Function ] which takes int as parameter and print it
    program.addTwoNumbers(2, 7, myLambda) // lambda as parameter part 1
    program.addTwoNumbers(2, 7, {s: Int -> println(s)}) // lambda as parameter part 2
    program.addTwoNumbers(2, 7) {s: Int -> println(s)} // lambda as parameter part 3

}



class Program {

    fun addTwoNumbers(a: Int, b: Int, action: (Int) -> Unit) {  // High Level Function with Lambda as Parameter

        val sum = a + b
        action(sum)     // println(sum) as lambda print the parameter
    }

    fun addTwoNumbers(a: Int, b: Int, action: MyInterface) {    // Using Interface
        val sum = a + b
        action.execute(sum)
    }

}

interface MyInterface {
    fun execute(sum: Int)
}



