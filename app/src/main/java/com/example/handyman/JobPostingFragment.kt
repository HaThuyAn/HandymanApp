package com.example.handyman

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import java.util.Calendar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import java.io.File
import java.util.UUID

class JobPostingFragment : Fragment() {

    private lateinit var addPhoto: ImageView
    private lateinit var photoFrame: FrameLayout
    private lateinit var placeholderContainer: LinearLayout
    private lateinit var photoScroll: HorizontalScrollView
    private lateinit var attachPhotosContainer: LinearLayout
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>

    private var cameraImageUri: Uri? = null
    private val imageUris = mutableListOf<Uri>()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // Hide placeholder and show the ScrollView when a photo is selected
            photoFrame.background = null
            placeholderContainer.visibility = View.GONE
            photoScroll.visibility = View.VISIBLE

            // Inflate thumbnail layout
            val thumbnailView = layoutInflater.inflate(R.layout.photo_thumbnail, attachPhotosContainer, false)
            val ivThumbnail = thumbnailView.findViewById<ImageView>(R.id.ivThumbnail)

            Glide.with(requireContext())
                .load(uri)
                .into(ivThumbnail)

            // Delete button
            val ivDelete = thumbnailView.findViewById<ImageView>(R.id.ivDelete)
            ivDelete.setOnClickListener {
                attachPhotosContainer.removeView(thumbnailView)
                imageUris.remove(uri)  // Remove from our list too
                if (attachPhotosContainer.childCount == 0) {
                    photoFrame.setBackgroundResource(R.drawable.frame)
                    placeholderContainer.visibility = View.VISIBLE
                    photoScroll.visibility = View.GONE
                }
            }

            // Add thumbnail to container
            attachPhotosContainer.addView(thumbnailView)

            // *** Add this Uri to our list ***
            imageUris.add(uri)
        }
    }

    val customerId = "customer2"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_job_posting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                // The image was captured successfully.
                // You can add the image thumbnail just like the gallery selection.
                photoFrame.background = null
                placeholderContainer.visibility = View.GONE
                photoScroll.visibility = View.VISIBLE

                val thumbnailView = layoutInflater.inflate(R.layout.photo_thumbnail, attachPhotosContainer, false)
                val ivThumbnail = thumbnailView.findViewById<ImageView>(R.id.ivThumbnail)

                Glide.with(requireContext())
                    .load(cameraImageUri)
                    .into(ivThumbnail)

                val ivDelete = thumbnailView.findViewById<ImageView>(R.id.ivDelete)
                ivDelete.setOnClickListener {
                    attachPhotosContainer.removeView(thumbnailView)
                    imageUris.remove(cameraImageUri)  // Remove from our list too
                    if (attachPhotosContainer.childCount == 0) {
                        photoFrame.setBackgroundResource(R.drawable.frame)
                        placeholderContainer.visibility = View.VISIBLE
                        photoScroll.visibility = View.GONE
                    }
                }

                attachPhotosContainer.addView(thumbnailView)
                imageUris.add(cameraImageUri!!)
            }
        }

        val args = JobPostingFragmentArgs.fromBundle(requireArguments())
        val serviceName = args.serviceCategory

        val etDateFrom = view.findViewById<EditText>(R.id.etDateFrom)
        val etDateTo = view.findViewById<EditText>(R.id.etDateTo)
        val etTimeFrom = view.findViewById<EditText>(R.id.etTimeFrom)
        val etTimeTo = view.findViewById<EditText>(R.id.etTimeTo)
        val spinner = view.findViewById<Spinner>(R.id.spinnerLocation)
        addPhoto = view.findViewById(R.id.ivAddPhoto)
        photoFrame = view.findViewById(R.id.photoFrame)
        placeholderContainer = view.findViewById(R.id.placeholderContainer)
        photoScroll = view.findViewById(R.id.photoScroll)
        attachPhotosContainer = view.findViewById(R.id.attachPhotosContainer)

        etDateFrom.setOnClickListener {
            // Get current date as default
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Format selected date and set to EditText
                // Note: selectedMonth is zero-indexed
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                Log.d("JobPostingFragment", "Chosen date from: $formattedDate")
                etDateFrom.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        etDateTo.setOnClickListener {
            // Get current date as default
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Format selected date and set to EditText
                // Note: selectedMonth is zero-indexed
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                Log.d("JobPostingFragment", "Chosen date to: $formattedDate")
                etDateTo.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        etTimeFrom.setOnClickListener {
            // Get current time as default
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                // Format selected time and set to EditText
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                Log.d("JobPostingFragment", "Chosen time: $formattedTime")
                etTimeFrom.setText(formattedTime)
            }, hour, minute, true) // true for 24-hour format; false for 12-hour format

            timePickerDialog.show()
        }

        etTimeTo.setOnClickListener {
            // Get current time as default
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                // Format selected time and set to EditText
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                Log.d("JobPostingFragment", "Chosen time: $formattedTime")
                etTimeTo.setText(formattedTime)
            }, hour, minute, true) // true for 24-hour format; false for 12-hour format

            timePickerDialog.show()
        }

        val etFromSalary = view.findViewById<EditText>(R.id.etFromSalary)
        val etToSalary = view.findViewById<EditText>(R.id.etToSalary)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroupExample)
        val checkBox = view.findViewById<CheckBox>(R.id.checkboxExample)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            etFromSalary.isEnabled = !isChecked
            etToSalary.isEnabled = !isChecked
            for (i in 0 until radioGroup.childCount) {
                radioGroup.getChildAt(i).isEnabled = !isChecked
            }
            if (isChecked) {
                etFromSalary.text.clear()
                etToSalary.text.clear()
                radioGroup.clearCheck()
            }
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.location_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        addPhoto.setOnClickListener { pickImageLauncher.launch("image/*") }

        val ivTakePhoto = view.findViewById<ImageView>(R.id.ivTakePhoto)
        ivTakePhoto.setOnClickListener {
            cameraImageUri = createImageFileUri()
            takePictureLauncher.launch(cameraImageUri)
        }

        val confirmRequest = view.findViewById<Button>(R.id.btnConfirmRequest)
        confirmRequest.setOnClickListener {
            val etDescribeProblem = view.findViewById<EditText>(R.id.etDescribeProblem)

            if (etDescribeProblem.text.isBlank()) {
                Toast.makeText(requireContext(), "Please describe your problem", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (etDateFrom.text.isBlank()) {
                Toast.makeText(requireContext(), "Please select a start date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (etDateTo.text.isBlank()) {
                Toast.makeText(requireContext(), "Please select an end date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (etTimeFrom.text.isBlank()) {
                Toast.makeText(requireContext(), "Please select a start time", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (etTimeTo.text.isBlank()) {
                Toast.makeText(requireContext(), "Please select an end time", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (etFromSalary.text.isNotBlank() && etToSalary.text.isNotBlank()) {
                if (etFromSalary.text.toString().toInt() >= etToSalary.text.toString().toInt()) {
                    Toast.makeText(
                        requireContext(),
                        "Please select an appropriate salary range",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                if (radioGroup.checkedRadioButtonId == -1) {
                    Toast.makeText(
                        requireContext(),
                        "Please select an option for salary payment",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }
            if (radioGroup.checkedRadioButtonId != -1) {
                if (etFromSalary.text.isBlank() or etToSalary.text.isBlank()) {
                    Toast.makeText(
                        requireContext(),
                        "Please choose a salary",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }

            if (spinner.selectedItem.toString() == "Select a location") {
                Toast.makeText(requireContext(), "Please select a location", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val jobDescription = etDescribeProblem.text.toString()
            val dateFrom = etDateFrom.text.toString()
            val dateTo = etDateTo.text.toString()
            val timeFrom = etTimeFrom.text.toString()
            val timeTo = etTimeTo.text.toString()
            val location = spinner.selectedItem.toString()
            val salaryFrom = if (etFromSalary.text.isNotBlank()) {
                etFromSalary.text.toString()
            } else {
                ""
            }
            val salaryTo = if (etToSalary.text.isNotBlank()) {
                etToSalary.text.toString()
            } else {
                ""
            }
            val paymentOption = if (radioGroup.checkedRadioButtonId != -1) {
                val selectedRadio =
                    view.findViewById<android.widget.RadioButton>(radioGroup.checkedRadioButtonId)
                selectedRadio.text.toString()
            } else {
                ""
            }

            val action =
                JobPostingFragmentDirections.actionJobPostingFragmentToOrderSummaryFragment(
                    customerId = "",
                    serviceCategory = serviceName,
                    problemDesc = jobDescription,
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    timeFrom = timeFrom,
                    timeTo = timeTo,
                    location = location,
                    salaryFrom = salaryFrom,
                    salaryTo = salaryTo,
                    paymentOption = paymentOption,
                    imageUris = imageUris.toTypedArray(),
                    assignedTo = "",
                    jobStatus = ""
                )
            findNavController().navigate(action)
            }

        }

    private fun createImageFileUri(): Uri {
        val fileName = "${UUID.randomUUID()}.jpg"
        // Create a file in the cache directory
        val file = File(requireContext().cacheDir, fileName)
        // Get the content URI using FileProvider
        return FileProvider.getUriForFile(requireContext(), "com.example.handyman.fileprovider", file)
    }
}