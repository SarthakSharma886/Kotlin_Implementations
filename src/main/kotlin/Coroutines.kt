import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
import kotlin.concurrent.thread as thread

fun main() {

//    parallelComputingUsingSimpleThread()

//    parallelComputingUsingCoroutine()

//    parallelComputingUsingCoroutineWithCanceling()

//    parallelComputingUsingCoroutineWithTimeout()

//    coroutineExecution()

//    coroutineLazyExecution()

}


/* This function is use for parallel tasking using simple thread
 * Application will not finish before all thread finished their work*/

fun parallelComputingUsingSimpleThread() {

    println()
    println("Simple thread parallel computing function body started")

    println("Main Work starts: ${Thread.currentThread().name}") // work on main thread

    thread { // create a background thread

        println("Fake Work starts: ${Thread.currentThread().name}") // work on background thread
        Thread.sleep(1000) // pretend doing some work
        println("Fake Work finished: ${Thread.currentThread().name}")

    }

    println("Main Work finished: ${Thread.currentThread().name}")

    println("Simple thread parallel computing function body finished")
    println()

}


/* This function is use for parallel tasking using coroutine
 * Application will not wait for coroutine for complete their work and get exited as main thread finish its work by default */

fun parallelComputingUsingCoroutine() = runBlocking { // Creates a coroutine that blocks the current thread ( in this case main thread ) used for using delay function

    println()
    println("Coroutine parallel task function body started")

    println("Main Work starts: ${Thread.currentThread().name}") // work on main thread


    /* launch is used to fire and forget coroutine. It is like starting a new thread. If the code inside the launch
     terminates with exception, then it is treated like uncaught exception in a thread -- usually printed to stderr in
     backend JVM applications and crashes Android applications. join is used to wait for completion of the launched
     coroutine and it does not propagate its exception. However, a crashed child coroutine cancels its parent with the
     corresponding exception, too.

     async is used to start a coroutine that computes some result. The result is represented by an instance of Deferred
     and you must use await on it. An uncaught exception inside the async code is stored inside the resulting Deferred
     and is not delivered anywhere else, it will get silently dropped unless processed. You MUST NOT forget about the
     coroutine you’ve started with async.*/


//    GlobalScope is used for launch coroutine at application level ( Discouraged. Use only when needed )

//    GlobalScope.launch { //Thread: T1 // create a coroutine that runs on a background thread (not main thread) and not block the current thread

//    launch launch coroutine at immediate parent level coroutine runBlocking block (not used outside the runBlocking block)thread and returns object of job by which we can control launch coroutine
//    val job = launch {  // launch builder will start a new coroutine that is “fire and forget” — that means it won’t return the result to the caller.


//    async launch coroutine at immediate parent level coroutine runBlocking block (not used outside the runBlocking block)thread and returns object of Deferred ( sub class of job which returns a value ) by which we can control launch coroutine.
    val deferredJob: Deferred<Int> = async {


        println("Fake Work starts: ${Thread.currentThread().name}") // Thread: T1
//        Thread.sleep(1000)  // not recommended as it blocks thread (T1) and block all coroutine on that thread (T1) ( violate coroutine work principle )

//        delay is a suspended function and can be called from a coroutine or a suspended function only
        delay(1000) // suspended function it doesn't block the background thread (T1) only stop current coroutine so other coroutine can continue their work.

//        After using delay or any suspend function coroutine background thread(T1) can be change acc. to the background thread available
        println("Fake Work finished: ${Thread.currentThread().name}") // Either T1 or some other thread.

        15   // return a value via deffred object
    }

    println("Main Work finished: ${Thread.currentThread().name}")

    /*
     Block the main thread till work of coroutine is completed
     */

//    Thread.sleep(2000) // wait for coroutine to finish ( practically not a right way to wait )

//    mySuspendDelayFunc(2000) // wait for coroutine to finish ( practically not a right way to wait )

//    job.join()  // wait for coroutine to finish ( right way to wait in case of a job object returned by the launch )

//    deferredJob.join() // wait for coroutine to finish ( right way to wait in case of a Deferred object returned by the async )

    val num = deferredJob.await() // wait for coroutine to finish ( same as join but use returned value from Deferred object )
    println("Deferred job returned value: ${num}")



    println("Coroutine parallel task function body finished")
    println()


}


/* This function is use for parallel tasking using coroutine and demonstrate canceling of coroutines */

fun parallelComputingUsingCoroutineWithCanceling() = runBlocking {


    /*
    For cancel a coroutine coroutine should be cooperative
      Two ways to make coroutine cooperative
    1. Periodically invoke a suspending function that checks for cancellation.
       - Only those suspending function that belongs to kotlinx.coroutines package will make coroutine cooperative.
       - delay(), yield(), withContext(), withTimeout() etc. are the suspending functions that belongs to
         kotlinx.coroutines package.

    2. Explicitly check for the cancellation status within the coroutine
       - CoroutineScope.isActive boolean flag
     */




    println()
    println("Coroutine parallel task function with cancellation body started")

    println("Main Work starts: ${Thread.currentThread().name}")


    val job = launch /* (Dispatchers.Default) used for use isActive flag */ {

        println("Fake Work starts: ${Thread.currentThread().name}")

        try {
            for (i in 0..500) {

                /* if(!isActive){ // need to add Dispatchers.Default for use this flag.
//                break // breaks from the for loop.
                return@launch // returns at the level of coroutine means truly cancel the coroutine.
            } */

                println(i)
//            Thread.sleep(50) // coroutine will not gonna check for cancellation that's why by using this coroutine is non cooperative.
                delay(50) // By using delay or yield this coroutine check for cancellation these make this coroutine
//            yield() // cooperative and at the time of cancellation it throws cancellationException.

            }
        } catch (ex: CancellationException) {
            println(ex.message)
            println("........ Coroutine is cancelled on ${Thread.currentThread().name} thread .........")
            return@launch
        } finally {
            try {
                println("now coroutine is finished in finally")
                println("isActive flag value before withContext(NonCancellable) ${isActive}") // isActive is false as coroutine is cancelled
//                mySuspendDelayFunc(200) // can't call suspend function within kotlinx.coroutines package after exception as coroutine is canceled so it will gonna throw again cancellation exception
                withContext(NonCancellable) {
                    mySuspendDelayFunc(200) // for calling suspend function within kotlinx.coroutines package we can use with context with non cancellable companion object.
                    println("isActive flag value inside withContext(NonCancellable) ${isActive}") /* isActive is true as withContext(NonCancellable)
                                                                                                   * made A non-cancelable job that is always [active] this code will not be cancelled.*/
                }
                println("now coroutine is finished after suspend function within kotlinx.coroutines package")
            } catch (ex: CancellationException) {
                println(ex.message)
                println(" cancel exception again ")
            }
        }


        println("Fake Work finished: ${Thread.currentThread().name}")

    }
    println("Main Work finished: ${Thread.currentThread().name}")

    delay(200)
//    job.cancelAndJoin() // use to cancel the job or if not get cancel it will join
    job.cancel(CancellationException("job is cancelled using custom exception")) // can make a exception with custom message
    job.join()



    println("Coroutine parallel task function with cancellation body finished")
    println()

}


/* This function is use for parallel tasking using coroutine using Timeouts */

fun parallelComputingUsingCoroutineWithTimeout() = runBlocking {

    println()
    println("Coroutine parallel task function with timeout body started")

    println("Main Work starts: ${Thread.currentThread().name}")


    try {
        withTimeout(200) { // using withTimeout when coroutine take more time then timeMillis it throws TimeoutCancellationException

            println("Fake Work starts in withTimeout: ${Thread.currentThread().name}")

            for (i in 0..500) {
                println(i)
                delay(50) // By using delay or yield this coroutine check for cancellation these make this coroutine
            }


            println("Fake Work finished in withTimeout: ${Thread.currentThread().name}")


        }
    } catch (ex: TimeoutCancellationException) {
        println("Exception caught in withTimeout coroutine is ${ex.message}")
    }


    val timeOutInt = withTimeoutOrNull(20000) { // using withTimeoutOrNull when coroutine take more time then timeMillis it returns null else return value

        println("Fake Work starts in withTimeoutOrNull: ${Thread.currentThread().name}")

        var returnInt = 0
        for (i in 0..50) {
            println(i)
            returnInt = i
            delay(50) // By using delay or yield this coroutine check for cancellation these make this coroutine
        }

        println("Fake Work finished in withTimeoutOrNull: ${Thread.currentThread().name}")

        returnInt

    }


    println("timeOutOrNull return value is ${timeOutInt}") // value of timeOutInt is null if coroutine take more time then timeMillis else returned value

    println("Main Work finished: ${Thread.currentThread().name}")



    println("Coroutine parallel task function with timeout body finished")
    println()


}


/* This function is use for demonstrate execution inside a coroutine */

fun coroutineExecution() = runBlocking {

    /* By default all execution inside a coroutineScope is sequential */

    println()
    println("Coroutine Execution function started")

    println("Main Work starts: ${Thread.currentThread().name}")


    var time = measureTimeMillis {
        val messageOne = printFirstFunc()
        val messageTwo = printSecondFunc()
        println("The entire message with sequential manner is ${messageOne} ${messageTwo}")
    }

    println("Time taken in sequential manner ${time} ms")


    println()


    /* To make execution concurrent we use async */


    time = measureTimeMillis {
        val messageOne = async { printFirstFunc() }
        val messageTwo = async { printSecondFunc() }
        println("The entire message with concurrent manner is ${messageOne.await()} ${messageTwo.await()}")
    }

    println("Time taken in concurrent manner ${time} ms")



    println("Main Work finished: ${Thread.currentThread().name}")

    println("Coroutine Execution function finished")
    println()


}


/* This function is use for demonstrate lazy execution of a coroutine */

fun coroutineLazyExecution() = runBlocking {

    /* When we declare a function using async by default both printFirstFunction and printSecondFunction execute actively
    *  but when we use (start = CoroutineStart.LAZY) both function will execute when we use messageOne and messageTwo */

    println()
    println("Coroutine lazy Execution function started")

    println("Main Work starts: ${Thread.currentThread().name}")


    val time = measureTimeMillis {
        val messageOne = async(start = CoroutineStart.LAZY) { printFirstFunc() }
        val messageTwo = async(start = CoroutineStart.LAZY) { printSecondFunc() }
        println("The entire message is ${messageOne.await()} ${messageTwo.await()}")
    }

    println("Time taken in lazy sequential manner ${time} ms")





    println("Main Work finished: ${Thread.currentThread().name}")



    println("Coroutine lazy Execution function finished")
    println()


}


/* Custom made Suspend function can only be called by a coroutine or inside any suspend function */

suspend fun mySuspendDelayFunc(millis: Long) {
//    delay is a suspended function and can be called from a suspended function
    delay(millis)
}


suspend fun printFirstFunc(): String {
    delay(1000)
    println("First printing function")
    return "Hello"
}


suspend fun printSecondFunc(): String {
    delay(1000)
    println("Second printing Function")
    return "World"
}