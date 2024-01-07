package com.example.madcamp_week2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import com.example.madcamp_week2.R
import java.util.Calendar

class Register : BaseActivity() {

    private val PICK_IMAGE = 100
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupBottomNavigation(R.id.page_4)

        val concertImage = findViewById<ImageView>(R.id.concertimage)
        val concertDate = findViewById<EditText>(R.id.editdate)
        val editStartTime = findViewById<EditText>(R.id.editstarttime)
        val editEndTime = findViewById<EditText>(R.id.editendtime)
        concertImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        concertDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                R.style.CustomDatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay" // Format the selected date as needed
                    concertDate.setText(selectedDate) // Display the selected date in the EditText or desired view
                },
                year,
                month,
                day
            )
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }
        editStartTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                R.style.TimePickerTheme,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    // Handle the selected time here (start time)
                    val selectedTime = "$hourOfDay:$minute"
                    editStartTime.setText(selectedTime)
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
        editEndTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                R.style.TimePickerTheme,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    // Handle the selected time here (end time)
                    val selectedTime = "$hourOfDay:$minute"
                    editEndTime.setText(selectedTime)
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
        val spinner: Spinner = findViewById(R.id.editlocation)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.options_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_layout)
        spinner.adapter = adapter



    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data
            findViewById<ImageView>(R.id.concertimage)?.setImageURI(imageUri)
        }
    }
}