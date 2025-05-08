package com.example.handyman

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class HandymanJobBoardAdapter(
    private val onViewDetails: (Job) -> Unit,
    private val onQuoteJob: (Job, Button) -> Unit
) : ListAdapter<Job, HandymanJobBoardAdapter.ViewHolder>(HandymanJobBoardDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.handyman_job_board_item, parent, false)
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
        private val quoteJobBttn: Button = itemView.findViewById(R.id.btnQuote)
        private val detailsBttn: Button = itemView.findViewById(R.id.btnViewDetails)

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

            quoteJobBttn.setOnClickListener {
                onQuoteJob(item, quoteJobBttn)
            }

            detailsBttn.setOnClickListener {
                onViewDetails(item)
            }
        }
    }
}