package com.example.listycity

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.text.clear

class CityRepository {
    private val db = Firebase.firestore
    private val citiesRef = db.collection("cities")
    private val _cities = mutableStateListOf(
        City("Edmonton", "AB"),
        City("Vancouver", "BC"),
        City("Toronto", "ON")
    )

    val cities: List<City>
        get() = _cities

    fun addCity(city: City) {
        citiesRef.document(city.name).set(city)
    }

    fun updateCity(oldCity: City, updatedCity: City) {
        if (oldCity.name != updatedCity.name) {
            citiesRef.document(oldCity.name).delete()
        } // minor update to fix city "cloning" bug (Google Gemini, 10.09.26)
        citiesRef.document(updatedCity.name).set(updatedCity)
        // deleting the old document in Firebase and swapping it out with a new one
    }

    // deletion from collection derived from Firestore manual
    // https://firebase.google.com/docs/firestore/manage-data/delete-data#kotlin

    fun deleteCity(city: City) { // to delete the city provided in the function
        citiesRef.document(city.name).delete()
    }

    init {
        citiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            _cities.clear()
            snapshot?.documents?.forEach { document ->
                val city = document.toObject(City::class.java)
                if (city != null) {
                    _cities.add(city)
                }
            }
        }
    }
}