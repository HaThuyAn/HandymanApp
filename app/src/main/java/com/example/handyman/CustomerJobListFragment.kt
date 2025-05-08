package com.example.handyman

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CustomerJobListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomerJobListAdapter
    private val customerId = "customer2"   // your real customer ID here

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customer_job_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = CustomerJobListAdapter(
            onViewDetails = { job ->
                // convert your stored String URIs back into Uri[]
                val urisArray: Array<Uri> = job.imageUris
                    .map { Uri.parse(it) }
                    .toTypedArray()

                val action =
                    CustomerJobListFragmentDirections
                        .actionCustomerJobListFragmentToCustomerJobDetailsFragment(
                            customerId   = customerId,
                            jobId        = job.jobId,
                            serviceCategory = job.jobCat,
                            problemDesc  = job.jobDesc,
                            dateFrom     = job.jobDateFrom,
                            dateTo       = job.jobDateTo,
                            timeFrom     = job.jobTimeFrom,
                            timeTo       = job.jobTimeTo,
                            location     = job.jobLocation,
                            salaryFrom   = job.jobSalaryFrom,
                            salaryTo     = job.jobSalaryTo,
                            paymentOption= job.jobPaymentOption,
                            imageUris    = urisArray,
                            assignedTo   = job.assignedTo,
                            jobStatus    = job.jobStatus
                        )
                findNavController().navigate(action)
            },
            onEdit = { job ->
                val urisArray: Array<Uri> = job.imageUris
                    .map { Uri.parse(it) }
                    .toTypedArray()

                val action =
                    CustomerJobListFragmentDirections
                        .actionCustomerJobListFragmentToJobEditFragment(
                            customerId   = customerId,
                            jobId        = job.jobId,
                            serviceCategory = job.jobCat,
                            problemDesc  = job.jobDesc,
                            dateFrom     = job.jobDateFrom,
                            dateTo       = job.jobDateTo,
                            timeFrom     = job.jobTimeFrom,
                            timeTo       = job.jobTimeTo,
                            location     = job.jobLocation,
                            salaryFrom   = job.jobSalaryFrom,
                            salaryTo     = job.jobSalaryTo,
                            paymentOption= job.jobPaymentOption,
                            imageUris    = urisArray,
                            assignedTo   = job.assignedTo,
                            jobStatus    = job.jobStatus
                        )
                findNavController().navigate(action)
            },
            onDelete = { job ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete job?")
                    .setMessage("Are you sure you want to delete this job?")
                    .setPositiveButton("Yes") { _, _ ->
                        FirebaseDatabase.getInstance()
                            .getReference("Job")
                            .child(job.jobId)
                            .removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Job deleted", Toast.LENGTH_SHORT).show()
                                val newList = adapter.currentList.filter { it.jobId != job.jobId }
                                adapter.submitList(newList)
                                // also delete its images folder
                                val storageRef = Firebase.storage
                                    .reference
                                    .child("jobImages/${job.jobId}")
                                storageRef.listAll()
                                    .addOnSuccessListener { listResult ->
                                        listResult.items.forEach { it.delete() }
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Failed to delete job: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                    .setNegativeButton("No", null)
                    .show()
            },
            onUpdate = fun(job: Job) {
                val currentStatus = job.jobStatus

                // build *only* the valid next‐step list
                val nextStatuses = when (currentStatus) {
                    "In-progress" -> arrayOf("Done")
                    "Done"        -> arrayOf()            // nothing left to do
                    else          -> arrayOf("In-progress")  // everything before In-progress
                }

                if (nextStatuses.isEmpty()) {
                    Toast.makeText(context, "No further updates available", Toast.LENGTH_SHORT).show()
                    return
                }

                // now show the dialog with only the valid choices
                var chosen = 0
                AlertDialog.Builder(requireContext())
                    .setTitle("Update status")
                    .setSingleChoiceItems(nextStatuses, 0) { _, which ->
                        chosen = which
                    }
                    .setPositiveButton("OK") { _, _ ->
                        val newStatus = nextStatuses[chosen]
                        val jobRef = FirebaseDatabase.getInstance()
                            .getReference("Job")
                            .child(job.jobId)

                        jobRef.child("jobStatusCustomer")
                            .setValue(newStatus)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Status updated to $newStatus",
                                    Toast.LENGTH_SHORT
                                ).show()

                                jobRef.child("jobStatusHandyman")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snap: DataSnapshot) {
                                            val handymanStatus = snap.getValue(String::class.java)
                                            if (handymanStatus == newStatus) {
                                                jobRef.child("jobStatus")
                                                    .setValue(newStatus)
                                                    .addOnSuccessListener {
                                                        val updatedList = if (newStatus == "In-progress" || newStatus == "Done") {
                                                            adapter.currentList.filter { it.jobId != job.jobId }
                                                        } else {
                                                            // there aren't any other cases, but just in case:
                                                            adapter.currentList
                                                        }
                                                        adapter.submitList(updatedList)
                                                    }

                                                val (custFrom, hmFrom, toList) = when (newStatus) {
                                                    "In-progress" -> Triple(
                                                        "assignedJobs",    // customer’s old list
                                                        "acceptedJobs",    // handyman’s old list
                                                        "inProgressJobs"   // new list
                                                    )
                                                    "Done" -> Triple(
                                                        "inProgressJobs",
                                                        "inProgressJobs",
                                                        "completedJobs"
                                                    )
                                                    else -> return
                                                }

                                                val custRef = FirebaseDatabase.getInstance()
                                                    .getReference("dummyCustomers")
                                                    .child(customerId)
                                                moveJobId(custRef, custFrom, toList, job.jobId)

                                                val hmRef = FirebaseDatabase.getInstance()
                                                    .getReference("dummyHandymen")
                                                    .child(job.assignedTo)
                                                moveJobId(hmRef, hmFrom, toList, job.jobId)
                                            }
                                        }
                                        override fun onCancelled(e: DatabaseError) {}
                                    })
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Failed to update status: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        recyclerView.adapter = adapter

        val spinner = view.findViewById<Spinner>(R.id.spinnerStatus)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, v: View?, pos: Int, id: Long
            ) {
                val display = parent.getItemAtPosition(pos) as String
                val key = when (display) {
                    "Assigned"      -> "assignedJobs"
                    "Not assigned"  -> "notAssignedJobs"
                    "All"           -> "allJobs"
                    "In-progress"   -> "inProgressJobs"
                    "Done"          -> "completedJobs"
                    else            -> return
                }
                fetchJobsForCategory(key)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        fetchJobsForCategory("allJobs")
        return view
    }

    private fun moveJobId(
        ref: DatabaseReference,
        fromList: String,
        toList: String,
        jobId: String
    ) {
        ref.child(fromList)
            .orderByValue()
            .equalTo(jobId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { it.ref.removeValue() }
                    ref.child(toList)
                        .push()
                        .setValue(jobId)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun fetchJobsForCategory(category: String) {
        val listRef = FirebaseDatabase.getInstance()
            .getReference("dummyCustomers")
            .child(customerId)
            .child(category)

        listRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(listSnap: DataSnapshot) {
                val jobIds = listSnap.children
                    .mapNotNull { it.getValue(String::class.java) }
                fetchJobsByIds(jobIds)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error loading $category", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchJobsByIds(jobIds: List<String>) {
        FirebaseDatabase.getInstance()
            .getReference("Job")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val jobs = snapshot.children.mapNotNull { jobSnap ->
                        val key = jobSnap.key ?: return@mapNotNull null
                        if (key !in jobIds) return@mapNotNull null
                        val parsed = jobSnap.getValue(Job::class.java) ?: return@mapNotNull null
                        parsed.copy(jobId = key)
                    }
                    adapter.submitList(jobs)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
}