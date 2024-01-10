package com.example.madcamp_week2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import com.example.madcamp_week2.R
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.source
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Calendar

//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class Register : BaseActivity() {
    class CustomTimePickerDialog(
        context: Context,
        private val listener: TimePickerDialog.OnTimeSetListener,
        hourOfDay: Int,
        minute: Int,
        is24HourView: Boolean
    ) : TimePickerDialog(context, listener, hourOfDay, minute, is24HourView) {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val positiveButton = getButton(TimePickerDialog.BUTTON_POSITIVE)
            val negativeButton = getButton(TimePickerDialog.BUTTON_NEGATIVE)

            positiveButton?.setTextColor(context.resources.getColor(R.color.custom_button_text_color))
            negativeButton?.setTextColor(context.resources.getColor(R.color.custom_button_text_color))
        }

        override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
            super.onTimeChanged(view, hourOfDay, minute)
            // You can add additional handling here if needed
        }
    }

    private val PICK_IMAGE = 100
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupBottomNavigation(R.id.page_3)

        val concertImage = findViewById<ImageView>(R.id.concertimage)
        val editTeam = findViewById<EditText>(R.id.editteam)
        val editTitle = findViewById<EditText>(R.id.edittitle)
        val editLocation = findViewById<Spinner>(R.id.editlocation)
        val concertDate = findViewById<EditText>(R.id.editdate)
        val editStartTime = findViewById<EditText>(R.id.editstarttime)
        val editEndTime = findViewById<EditText>(R.id.editendtime)
        val concertSetList = findViewById<EditText>(R.id.editsetlist)
        val buttonRegister = findViewById<Button>(R.id.buttonregister)
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
                    val selectedDate =
                        "$selectedYear-${selectedMonth + 1}-$selectedDay" // Format the selected date as needed
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

            val timePickerDialog = CustomTimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->
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

            val timePickerDialog = CustomTimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->
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

        buttonRegister.setOnClickListener {
//            imageUri?.let {
//                val tmp: String? = convertImageToBase64(this, imageUri!!)
//                tmp?.let {
//                    Log.d("base64", tmp)
//                    // Process the base64 string if it's not null
//                } ?: run {
//                    Log.e("base64", "Conversion failed or imageUri is null")
//                    // Handle the case where conversion failed or imageUri is null
//                }
//            } ?: run {
//                Log.e("base64", "imageUri is null")
//                // Handle the case where imageUri is null
//            }
            var team = editTeam.text.toString().trim()
            var title = editTitle.text.toString().trim()
            var image_url = "tmp"
            var date = concertDate.text.toString().trim()
            var location = editLocation.selectedItem as String
            var start_time = editStartTime.text.toString().trim()
            var end_time = editEndTime.text.toString().trim()
            var setlist = concertSetList.text.toString().trim()
            val tmpstarttime = start_time.split(":")
            val tmpendtime = end_time.split(":")

            if (team.length == 0) {
                showToast("밴드명을 입력해주세요")
            } else if (title.length == 0) {
                showToast("공연명을 입력해주세요")
            } else if (imageUri==null) {
                showToast("공연 대표 사진을 골라주세요")
            } else if (date.length == 0) {
                showToast("날짜를 입력해주세요")
            } else if (location == "공연 장소를 선택하세요") {
                showToast("장소을 선택해주세요")
            } else if (start_time.length == 0) {
                showToast("공연 시작 시간을 입력해주세요")
            } else if (end_time.length == 0) {
                showToast("공연 종료 시간을 입력해주세요")
            } else if (!((tmpstarttime[0].toInt() < tmpendtime[0].toInt()) || ((tmpstarttime[0].toInt() == tmpendtime[0].toInt()) && tmpstarttime[1].toInt() < tmpendtime[1].toInt()))) {
                showToast("시작시간은 종료시간보다 빨라야합니다")
            } else if (setlist.length == 0) {
                showToast("셋리스트를 입력해주세요")
            } else {
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                var requestBody: RequestBody? = null
                inputStream?.let {
                    val mediaType = "image/*".toMediaTypeOrNull()
                    requestBody = object : RequestBody() {
                        override fun contentType(): MediaType? {
                            return mediaType
                        }

                        override fun contentLength(): Long {
                            // Return the content length of the input stream
                            // (You might not know the exact length, so returning -1 is common)
                            return -1
                        }

                        override fun writeTo(sink: BufferedSink) {
                            inputStream.use { input ->
                                sink.writeAll(input.source())
                            }
                        }
                    }
                }

                val mutipartBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("team", team)
                    .addFormDataPart("title", title)
                    .addFormDataPart("date", date)
                    .addFormDataPart("location", location)
                    .addFormDataPart("start_time", start_time)
                    .addFormDataPart("end_time", end_time)
                    .addFormDataPart("setlist", setlist)
                    .addFormDataPart("image", "image.jpg", requestBody!!)
                    .build()

                val request = Request.Builder()
                    .url("http://172.10.7.78:80/store_busking") // Replace with your server's URL
                    .post(mutipartBody)
                    .build()
                val client = OkHttpClient()
                client.newCall(request).enqueue(object : Callback {

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        // Handle the server response here if needed
                        runOnUiThread{
                            if (response.code == 200) {
                                // Response code is 200 - Start a new activity
                                val intent = Intent(this@Register, CheckRegister::class.java)
                                intent.putExtra("title", title)
                                intent.putExtra("location", location)
                                startActivity(intent)
                                // Finish the current activity if needed
//                            finish()
                            } else if (response.code ==495){
                                showToast("이미 예약된 시간입니다.")
                            }
                            else {
                                // Handle other response codes or scenarios
                                val responseBody = response.body?.string()
//                            showToast("something is wrong..")
                                // Handle the response for other cases
                            }

                        }

                        // Handle response
                    }

                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        // Handle failure
                        e.printStackTrace()
                    }
                })


            }


        }

//        fun showToast(message: String) {
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data
            findViewById<ImageView>(R.id.concertimage)?.setImageURI(imageUri)
        }
    }
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}