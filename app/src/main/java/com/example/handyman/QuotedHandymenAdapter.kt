// QuotedHandymenAdapter.kt

package com.example.handyman

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.handyman.chatbox.ChatClientActivity
import com.example.handyman.utils.SessionManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class QuotedHandymenAdapter(
    private val handymanList: List<String>,
    private val jobId: String,
    initialAssignedId: String,
    private val customerId: String,
    private val context: Context
) : RecyclerView.Adapter<QuotedHandymenAdapter.ViewHolder>() {

    private var assignedId: String = initialAssignedId

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val assignBtn: Button = itemView.findViewById(R.id.btnAssign)
        val messageBtn: Button = itemView.findViewById(R.id.btnMessage)
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

        val isAssigned = assignedId.isNotBlank()
        val isThisAssigned = (handymanId == assignedId)

        when {
            isThisAssigned -> {
                holder.assignBtn.text = "Assigned"
                holder.assignBtn.isEnabled = false
                ViewCompat.setBackgroundTintList(
                    holder.assignBtn,
                    ColorStateList.valueOf(Color.parseColor("#50C878"))
                )
            }

            isAssigned -> {
                holder.assignBtn.text = "Assign"
                holder.assignBtn.isEnabled = false
                ViewCompat.setBackgroundTintList(
                    holder.assignBtn,
                    ColorStateList.valueOf(Color.parseColor("#FFCCCCCC"))
                )
            }

            else -> {
                holder.assignBtn.text = "Assign"
                holder.assignBtn.isEnabled = true
                ViewCompat.setBackgroundTintList(
                    holder.assignBtn,
                    ColorStateList.valueOf(Color.parseColor("#ff33b5e5"))
                )
            }
        }

        holder.assignBtn.setOnClickListener {
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

        // Message button to open chat in job detail of client
        holder.messageBtn.setOnClickListener {

            // Fetch document from Firestore that contains chatroom of job
            val chatRef = FirebaseFirestore.getInstance().collection("chats").document(jobId)
            chatRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Load memberInfos array from document
                    val memberInfos: List<Map<String, String>> =
                        documentSnapshot.get("memberInfos") as List<Map<String, String>>

                    // Loop through all maps in memberInfos to check if this chatroom belongs to current user
                    for ((index, member) in memberInfos.withIndex()) {
                        if (member["uid"] == SessionManager.currentUserID) {
                            // If current user is in the second (last) map of array
                            // open ChatClientActivity with information of the other user
                            // (whose information should be stored in first map of array)
                            if (index == memberInfos.size - 1) {
                                val intent = Intent(context, ChatClientActivity::class.java).apply {
                                    putExtra("chatID", jobId)
                                    putExtra("uid", memberInfos[0]["uid"])
                                    putExtra("username", memberInfos[0]["username"])
                                }
                                context.startActivity(intent)
                            } else {
                                // Else if current user is in the first map of array
                                // open ChatClientActivity with information of the other user
                                // (whose information should be stored in second (last) map of array)
                                val intent = Intent(context, ChatClientActivity::class.java).apply {
                                    putExtra("chatID", jobId)
                                    putExtra("uid", memberInfos[1]["uid"])
                                    putExtra("username", memberInfos[1]["username"])
                                }
                                context.startActivity(intent)
                            }
                        }
                    }
                } else {
                    Log.e(
                        "PrototypeIssue",
                        "Due to limitations during development of this prototype\n" +
                                "The chatroom for this job is *not* automatically created on the Firestore\n" +
                                "The chatroom must be manually added by creating a new document with id the same as this job id: $jobId \n" +
                                "Add into the document a String field named 'chatID' whose value is the same as the document id\n" +
                                "Then add user infos by including an array that contains two maps, each map containing a 'uid' key and a 'username' key\n" +
                                "The 'uid' values must match the users that are part of the job (1 customer and 1 handyman)\n" +
                                "The chat feature does not currently support more than two members per chatroom"
                    )
                    Toast.makeText(context, "Please read the log with tag 'PrototypeIssue'", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
