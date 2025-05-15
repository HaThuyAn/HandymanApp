package com.example.handyman

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class HandymanJobBoardFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HandymanJobBoardAdapter
    val handymanID = "handyman7"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_handyman_job_board, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = HandymanJobBoardAdapter(
            onViewDetails = { job ->
                // Handle "View Details" button click
                val action = HandymanJobBoardFragmentDirections.actionHandymanJobBoardFragmentToHandymanJobBoardDetailsFragment(
                        customerId = "",
                        jobId = job.jobId,
                        serviceCategory = job.jobCat,
                        problemDesc = job.jobDesc,
                        dateFrom = job.jobDateFrom,
                        dateTo = job.jobDateTo,
                        timeFrom = job.jobTimeFrom,
                        timeTo = job.jobTimeTo,
                        location = job.jobLocation,
                        salaryFrom = job.jobSalaryFrom,
                        salaryTo = job.jobSalaryTo,
                        paymentOption = job.jobPaymentOption,
                        imageUris = null,
                        assignedTo = job.assignedTo,
                        jobStatus = job.jobStatus
                    )
                findNavController().navigate(action)
            },
            onQuoteJob = { job, quoteJobBttn ->
                val jobQuotesRef = FirebaseDatabase.getInstance()
                    .getReference("DummyJob")
                    .child(job.jobId)
                    .child("quotedHandymen")

                jobQuotesRef.push()
                    .setValue(handymanID)
                    .addOnSuccessListener {
                        val handymanRef = FirebaseDatabase.getInstance()
                            .getReference("dummyHandymen")
                            .child(handymanID)

                        handymanRef.child("quotedJobs")
                            .push()
                            .setValue(job.jobId)
                            .addOnSuccessListener {
                                handymanRef.child("allJobs")
                                    .push()
                                    .setValue(job.jobId)
                                    .addOnSuccessListener {
                                        quoteJobBttn.isEnabled = false
                                        quoteJobBttn.text = "Quoted"
                                        ViewCompat.setBackgroundTintList(
                                            quoteJobBttn,
                                            ColorStateList.valueOf(Color.parseColor("#FFCCCCCC"))
                                        )
                                        Toast.makeText(
                                            context,
                                            "Job quoted successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e3 ->
                                        Toast.makeText(
                                            context,
                                            "Quoted, but failed to add to allJobs: ${e3.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                            .addOnFailureListener { e2 ->
                                Toast.makeText(
                                    context,
                                    "Quoted handyman, but failed to record in quotedJobs: ${e2.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                    .addOnFailureListener { e1 ->
                        Toast.makeText(
                            context,
                            "Failed to quote job: ${e1.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        )
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        fetchJobsFromDatabase()
    }

    private fun fetchJobsFromDatabase() {
        val jobRef = FirebaseDatabase.getInstance().getReference("DummyJob")
        jobRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val availableJobs = snapshot.children.mapNotNull { child ->
                    // 1) Deserialize the Job object
                    val job = child.getValue(Job::class.java) ?: return@mapNotNull null
                    // 2) Grab its push-key
                    val jobId = child.key ?: return@mapNotNull null
                    // 3) Read quotedHandymen list
                    val quoted = child
                        .child("quotedHandymen")
                        .children
                        .mapNotNull { it.getValue(String::class.java) }
                    // 4) Skip any job already quoted by this handyman
                    if (handymanID in quoted) return@mapNotNull null
                    // 5) Return a copy that includes its ID
                    job.copy(jobId = jobId)
                }

                // 6) Update the adapter
                adapter.submitList(availableJobs)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HandymanJobBoard", "Failed to load jobs", error.toException())
                context?.let {
                    Toast.makeText(
                        it,
                        "Error loading jobs: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}