fun main() {

    /* Extension functions are used to add functions to a existing class without declaring it inside the class
    *  Extension function behaves like a static method of the class */

//    extensionFunctionWithCustomClass()

    extensionFunctionWithPredefinedClass()


}


/* This function is used to demonstrate extension function with custom made class */

fun extensionFunctionWithCustomClass() {

    val student = CustomStudentClass()

    println("does student passed ${student.hasPassed(78)}")
    println("does student get scholarship ${student.isScholarExtensionFunction(78)}")

}


/* This function is used to demonstrate extension function with predefined class */

fun extensionFunctionWithPredefinedClass() {

    val string1 = "My"
    val string2 = "name"
    val string3 = "is"
    val string4 = "Sarthak"

    println(string1.concat(string2, string3, string4))

}


/* Custom made student class */

class CustomStudentClass {

    fun hasPassed(marks: Int): Boolean {
        return marks > 33
    }

}


/* Extension function made for a custom defined class */

fun CustomStudentClass.isScholarExtensionFunction(marks: Int): Boolean {
    return marks > 90
}


/* Extension function made for a predefined class */

fun String.concat(str1: String, str2: String, str3: String): String {
    return this + " " + str1 + " " + str2 + " " + str3
}
