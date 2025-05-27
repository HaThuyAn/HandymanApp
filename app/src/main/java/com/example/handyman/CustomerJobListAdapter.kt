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

class CustomerJobListAdapter(
    private val onViewDetails: (Job) -> Unit,
    private val onEdit: (Job) -> Unit,
    private val onDelete: (Job) -> Unit,
    private val onUpdate: (Job) -> Unit,
    var hideStatus: Boolean = false
) : ListAdapter<Job, CustomerJobListAdapter.ViewHolder>(CustomerJobListDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.customer_job_list_item, parent, false)
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
        private val edit: ImageView = itemView.findViewById(R.id.ivEdit)
        private val delete: ImageView = itemView.findViewById(R.id.ivDelete)
        private val updateBttn: Button = itemView.findViewById(R.id.btnUpdate)
        private val status: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(item: Job) {
            // Bind your Job data to the views
            tvJobTitle.text = item.jobCat
            tvJobDesc.text = item.jobDesc
            if (item.jobSalaryFrom.isNotBlank() && item.jobSalaryTo.isNotBlank()) {
                tvSalary.text = if (item.jobPaymentOption == "Per Day")
                    "AUD ${item.jobSalaryFrom}-${item.jobSalaryTo}/day"
                else
                    "AUD ${item.jobSalaryFrom}-${item.jobSalaryTo}"
            } else {
                tvSalary.text = "To be negotiated"
            }

            tvDate.text = if (item.jobDateFrom == item.jobDateTo)
                item.jobDateFrom
            else
                "${item.jobDateFrom} — ${item.jobDateTo}"

            tvTime.text = "${item.jobTimeFrom} — ${item.jobTimeTo}"
            tvLocation.text = "${item.jobLocation}, Melbourne, VIC"

            // Determine status
            val displayStatus = when {
                item.assignedTo.isBlank() -> "Not assigned"
                item.jobStatusCustomer.isNullOrBlank() || item.jobStatusHandyman.isNullOrBlank() ->
                    "Assigned"
                else -> item.jobStatusCustomer!!
            }
//            status.text = displayStatus
//
//            when (displayStatus) {
//                "Not assigned" -> status.setBackgroundResource(R.drawable.status_not_assigned)
//                "Assigned"     -> status.setBackgroundResource(R.drawable.status_assigned)
//                "In-progress"  -> status.setBackgroundResource(R.drawable.status_in_progress)
//                "Done"         -> status.setBackgroundResource(R.drawable.status_done)
//            }

            if (hideStatus) {
                status.visibility = View.GONE
                status.text = ""
            } else {
                status.visibility = View.VISIBLE
                status.text = displayStatus

                when (displayStatus) {
                    "Not assigned" -> status.setBackgroundResource(R.drawable.status_not_assigned)
                    "Assigned"     -> status.setBackgroundResource(R.drawable.status_assigned)
                    "In-progress"  -> status.setBackgroundResource(R.drawable.status_in_progress)
                    "Done"         -> status.setBackgroundResource(R.drawable.status_done)
                }
            }

            detailsBttn.setOnClickListener { onViewDetails(item) }
            edit.setOnClickListener    { onEdit(item) }
            delete.setOnClickListener  { onDelete(item) }

            // Disable update until someone is assigned
            if (item.assignedTo.isBlank()) {
                updateBttn.isEnabled = false
                updateBttn.alpha     = 0.5f
                updateBttn.setOnClickListener {
                    Toast.makeText(
                        itemView.context,
                        "Please assign a handyman before updating status!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                if (item.jobStatus != "Done") {
                    updateBttn.isEnabled = true
                    updateBttn.alpha     = 1.0f
                    updateBttn.setOnClickListener { onUpdate(item) }
                } else {
                    updateBttn.isEnabled = false
                    updateBttn.alpha     = 0.5f
                    updateBttn.setOnClickListener {
                        Toast.makeText(
                            itemView.context,
                            "Job is already done!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}