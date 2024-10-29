package com.example.myassignmentw7

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var provinceSpinner: Spinner
    private lateinit var districtSpinner: Spinner
    private lateinit var wardSpinner: Spinner
    private lateinit var addressHelper: AddressHelper
    private lateinit var btnShowDatePicker: Button
    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateTextView: TextView
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo AddressHelper
        addressHelper = AddressHelper(resources)

        // Khởi tạo Spinner
        provinceSpinner = findViewById(R.id.spinnerTinh)
        districtSpinner = findViewById(R.id.spinnerHuyen)
        wardSpinner = findViewById(R.id.spinnerXa)

        // Khởi tạo nút chọn ngày sinh, CalendarView và nút Submit
        btnShowDatePicker = findViewById(R.id.btnShowDatePicker)
        calendarView = findViewById(R.id.calendarView)
        selectedDateTextView = findViewById(R.id.selectedDateTextView)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Ẩn CalendarView lúc đầu
        calendarView.visibility = View.GONE

        // Đặt dữ liệu cho Spinner tỉnh với placeholder "Chọn tỉnh/thành phố"
        val provinces = listOf("Chọn tỉnh/thành phố") + addressHelper.getProvinces()
        provinceSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Sự kiện khi chọn tỉnh
        provinceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedProvince = provinces[position]
                if (position != 0) {
                    updateDistrictSpinner(selectedProvince)
                } else {
                    districtSpinner.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, listOf("Chọn quận/huyện"))
                    wardSpinner.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, listOf("Chọn xã/phường"))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Sự kiện khi chọn huyện
        districtSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedProvince = provinceSpinner.selectedItem.toString()
                val selectedDistrict = districtSpinner.selectedItem.toString()
                if (position != 0) {
                    updateWardSpinner(selectedProvince, selectedDistrict)
                } else {
                    wardSpinner.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, listOf("Chọn xã/phường"))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Hiển thị CalendarView khi nhấn vào nút
        btnShowDatePicker.setOnClickListener {
            calendarView.visibility = if (calendarView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        // Lấy ngày khi chọn trên CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = dateFormat.format(calendar.time)

            // Cập nhật TextView với ngày đã chọn và ẩn CalendarView
            selectedDateTextView.text = "Ngày sinh: $selectedDate"
            calendarView.visibility = View.GONE
        }

        // Sự kiện cho nút Submit
        btnSubmit.setOnClickListener {
            if (isInputValid()) {
                // Xử lý khi thông tin hợp lệ (có thể thực hiện gửi dữ liệu, etc.)
                Toast.makeText(this, "Thông tin hợp lệ, tiếp tục gửi dữ liệu...", Toast.LENGTH_SHORT).show()
            } else {
                // Hiển thị thông báo lỗi
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Kiểm tra tính hợp lệ của thông tin
    private fun isInputValid(): Boolean {
        // Kiểm tra MSSV
        val mssvEditText = findViewById<EditText>(R.id.edtMSSV)
        val mssv = mssvEditText.text.toString().trim()

        // Kiểm tra Họ tên
        val hoTenEditText = findViewById<EditText>(R.id.edtHoTen)
        val hoTen = hoTenEditText.text.toString().trim()

        // Kiểm tra giới tính
        val radioGroup = findViewById<RadioGroup>(R.id.rgGioiTinh)
        val selectedGenderId = radioGroup.checkedRadioButtonId

        // Kiểm tra Email
        val emailEditText = findViewById<EditText>(R.id.edtEmail)
        val email = emailEditText.text.toString().trim()

        // Kiểm tra Số điện thoại
        val soDienThoaiEditText = findViewById<EditText>(R.id.edtSoDienThoai)
        val soDienThoai = soDienThoaiEditText.text.toString().trim()

        // Kiểm tra Ngày sinh đã được chọn
        val selectedDate = selectedDateTextView.text.toString() != "Ngày sinh: Chưa chọn"

        // Kiểm tra tất cả các trường
        return mssv.isNotEmpty() &&
                hoTen.isNotEmpty() &&
                selectedGenderId != -1 &&
                email.isNotEmpty() &&
                soDienThoai.isNotEmpty() &&
                selectedDate
    }

    // Cập nhật danh sách huyện theo tỉnh đã chọn
    private fun updateDistrictSpinner(province: String) {
        val districts = listOf("Chọn quận/huyện") + addressHelper.getDistricts(province)
        districtSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, districts).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        // Xóa danh sách xã khi chọn tỉnh mới
        wardSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Chọn xã/phường")).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    // Cập nhật danh sách xã theo tỉnh và huyện đã chọn
    private fun updateWardSpinner(province: String, district: String) {
        val wards = listOf("Chọn xã/phường") + addressHelper.getWards(province, district)
        wardSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, wards).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
}
