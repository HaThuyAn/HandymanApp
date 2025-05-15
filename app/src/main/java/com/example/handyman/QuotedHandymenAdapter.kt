// QuotedHandymenAdapter.kt

package com.example.handyman

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class QuotedHandymenAdapter(
    private val handymanList: List<String>,
    private val jobId: String,
    initialAssignedId: String,
    private val customerId: String
) : RecyclerView.Adapter<QuotedHandymenAdapter.ViewHolder>() {

    private var assignedId: String = initialAssignedId

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val assignBttn: Button = itemView.findViewById(R.id.btnAssign)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quoted_handymen_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = handymanList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val handymanId = handymanList[position]
        holder.tvTitle.text = handymanId

        val isAssigned     = assignedId.isNotBlank()
        val isThisAssigned = (handymanId == assignedId)

        when {
            isThisAssigned -> {
                holder.assignBttn.text = "Assigned"
                holder.assignBttn.isEnabled = false
                ViewCompat.setBackgroundTintList(
                    holder.assignBttn,
                    ColorStateList.valueOf(Color.parseColor("#50C878"))
                )
            }
            isAssigned -> {
                holder.assignBttn.text = "Assign"
                holder.assignBttn.isEnabled = false
                ViewCompat.setBackgroundTintList(
                    holder.assignBttn,
                    ColorStateList.valueOf(Color.parseColor("#FFCCCCCC"))
                )
            }
            else -> {
                holder.assignBttn.text = "Assign"
                holder.assignBttn.isEnabled = true
                ViewCompat.setBackgroundTintList(
                    holder.assignBttn,
                    ColorStateList.valueOf(Color.parseColor("#ff33b5e5"))
                )
            }
        }

        holder.assignBttn.setOnClickListener {
            val jobRef = FirebaseDatabase.getInstance()
                .getReference("DummyJob")
                .child(jobId)

            jobRef.child("assignedTo")
                .setValue(handymanId)
                .addOnSuccessListener {
                    assignedId = handymanId
                    notifyDataSetChanged()

                    val handymanRef = FirebaseDatabase.getInstance()
                        .getReference("dummyHandymen")
                        .child(handymanId)
                    handymanRef.child("acceptedJobs")
                        .push()
                        .setValue(jobId)
                        .addOnFailureListener { e2 ->
                            Toast.makeText(
                                holder.itemView.context,
                                "Assigned, but failed in acceptedJobs: ${e2.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    // remove from quotedJobs
                    handymanRef.child("quotedJobs")
                        .orderByValue().equalTo(jobId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach { it.ref.removeValue() }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })

                    // move in customer’s lists: notAssignedJobs → assignedJobs
                    val custRef = FirebaseDatabase.getInstance()
                        .getReference("dummyCustomers")
                        .child(customerId)

                    custRef.child("notAssignedJobs")
                        .orderByValue().equalTo(jobId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(ns: DataSnapshot) {
                                ns.children.forEach { it.ref.removeValue() }
                                // now push into assignedJobs
                                custRef.child("assignedJobs")
                                    .push()
                                    .setValue(jobId)
                                    .addOnFailureListener { e4 ->
                                        Toast.makeText(
                                            holder.itemView.context,
                                            "Assigned, but failed to record in customer’s assignedJobs: ${e4.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })

                    // feedback
                    Toast.makeText(
                        holder.itemView.context,
                        "Assigned to $handymanId",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e1 ->
                    Toast.makeText(
                        holder.itemView.context,
                        "Failed to assign: ${e1.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}
