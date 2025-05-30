package com.example.handyman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HandymanJobListAdapter(
    private val handymanId: String,
    private val onViewDetails: (Job) -> Unit,
    private val onDelete: (Job) -> Unit,
    private val onUpdate: (Job) -> Unit,
    val onPaymentProceed: (Job) -> Unit

) : ListAdapter<Job, HandymanJobListAdapter.ViewHolder>(HandymanJobListDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.handyman_job_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jobItem = getItem(position)
        holder.bind(jobItem)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvJobTitle: TextView = itemView.findViewById(R.id.tvJobTitle)
        private val tvJobDesc: TextView = itemView.findViewById(R.id.tvJobSubtitle)
        private val tvSalary: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvAddress)
        private val detailsBttn: Button = itemView.findViewById(R.id.btnViewDetails)
        private val delete: ImageView = itemView.findViewById(R.id.ivDelete)
        private val updateBttn   = itemView.findViewById<Button>(R.id.btnUpdate)
        private val status: TextView = itemView.findViewById(R.id.tvStatus)
        val btnProceedPayment: Button = itemView.findViewById(R.id.btnProceedPayment)


        fun bind(item: Job) {
            // Bind your Job data to the views
            tvJobTitle.text = item.jobCat
            tvJobDesc.text = item.jobDesc
            if (item.jobSalaryFrom != "" && item.jobSalaryTo != "") {
                if (item.jobPaymentOption == "Per Day") {
                    tvSalary.text = "AUD ${item.jobSalaryFrom}-${item.jobSalaryTo}/day"
                } else {
                    tvSalary.text = "AUD ${item.jobSalaryFrom}-${item.jobSalaryTo}"
                }
            } else {
                tvSalary.text = "To be negotiated"
            }
            if (item.jobDateFrom == item.jobDateTo) {
                tvDate.text = "${item.jobDateFrom}"
            } else {
                tvDate.text = "${item.jobDateFrom} — ${item.jobDateTo}"
            }
            tvTime.text = "${item.jobTimeFrom} — ${item.jobTimeTo}"
            tvLocation.text = "${item.jobLocation}, Melbourne, VIC"

            val hasQuoted  = item.quotedHandymen
                .orEmpty()
                .containsValue(handymanId)
            val isAssigned = item.assignedTo == handymanId
            val hasOverall = item.jobStatus == "In-progress" || item.jobStatus == "Done"

            if (!hasQuoted && !isAssigned && !hasOverall) {
                // truly nothing to show
                status.visibility = View.GONE
            } else {
                status.visibility = View.VISIBLE

                // pick one of four status labels
                val displayStatus = when {
                    hasQuoted && !isAssigned       -> "Quoted"
                    isAssigned && !hasOverall      -> "Accepted"
                    item.jobStatus == "In-progress"-> "In-progress"
                    else                            -> "Done"
                }
                status.text = displayStatus

                // apply matching background
                when (displayStatus) {
                    "Quoted"       -> status.setBackgroundResource(R.drawable.status_not_assigned)
                    "Accepted"     -> status.setBackgroundResource(R.drawable.status_assigned)
                    "In-progress"  -> status.setBackgroundResource(R.drawable.status_in_progress)
                    "Done"         -> status.setBackgroundResource(R.drawable.status_done)
                }
            }

            detailsBttn.setOnClickListener {
                onViewDetails(item)
            }

            delete.setOnClickListener {
                onDelete(item)
            }

            if (item.assignedTo.isNullOrBlank()) {
                updateBttn.isEnabled = false
                updateBttn.alpha     = 0.5f
                updateBttn.setOnClickListener {
                    Toast.makeText(
                        itemView.context,
                        "This job has not been assigned to you yet!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else {
                if (item.jobStatus != "Done") {
                    updateBttn.isEnabled = true
                    updateBttn.alpha     = 1.0f
                    updateBttn.setOnClickListener {
                        onUpdate(item)
                    }
                } else {
                    updateBttn.setOnClickListener {
                        Toast.makeText(
                            itemView.context,
                            "Job is already done!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            btnProceedPayment.setOnClickListener {
                onPaymentProceed(item)
            }
        }
    }
}