package com.example.imagerecognition

data class User(
    val firstName: String = "john",
    val lastName: String = "doe",
    val age:Int = 18,
    val address: String = "USA, New York",
    val sex: String = "Male",
    val profilePicture: String = ""
) {
    // Additional methods or properties can be added here
}