package com.example.handyman

import android.net.Uri

data class Job(
    val jobId: String = "",
    val customerId: String = "",
    val jobCat: String = "",
    val jobDesc: String = "",
    val jobDateFrom: String = "",
    val jobDateTo: String = "",
    val jobTimeFrom: String = "",
    val jobTimeTo: String = "",
    val jobLocation: String = "",
    val jobSalaryFrom: String = "",
    val jobSalaryTo: String = "",
    val jobPaymentOption: String = "",
    val paymentStatus: String = "",
    val imageUris: List<String> = emptyList(),
    val assignedTo: String = "",
    val jobStatus: String = "",
    val jobStatusCustomer : String? = null,
    val jobStatusHandyman : String? = null,
    val quotedHandymen: Map<String, String>? = null,
)
