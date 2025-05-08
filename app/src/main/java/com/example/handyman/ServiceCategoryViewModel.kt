package com.example.handyman

import androidx.lifecycle.ViewModel

class ServiceCategoryViewModel : ViewModel() {

    val categories = listOf(
        ServiceCategory("Plumbing", R.drawable.plumbing),
        ServiceCategory("Furniture Assembly", R.drawable.furniture),
        ServiceCategory("A/C Repairs", R.drawable.air_conditioner),
        ServiceCategory("Electrical", R.drawable.electrical),
        ServiceCategory("Carpentry", R.drawable.carpentry),
        ServiceCategory("Yard Maintenance", R.drawable.yard_maintenance),
        ServiceCategory("Appliance Installation", R.drawable.appliance),
        ServiceCategory("Cleaning", R.drawable.cleaning),
        ServiceCategory("House Moving", R.drawable.house_moving),
        ServiceCategory("Painting", R.drawable.painting),
        ServiceCategory("Floor Repairs", R.drawable.floor_repairs),
        ServiceCategory("Ceiling Repairs", R.drawable.ceiling),
        ServiceCategory("Locksmith", R.drawable.locksmith),
        ServiceCategory("Car Service", R.drawable.car_service),
        ServiceCategory("Baby Sitting", R.drawable.baby_sitting),
        ServiceCategory("Beauty Service", R.drawable.beauty_service)
    )
}