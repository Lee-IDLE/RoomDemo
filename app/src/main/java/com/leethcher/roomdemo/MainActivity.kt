package com.leethcher.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.leethcher.roomdemo.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val employeeDao = (application as EmployeeApp).db.employeeDao()
        binding.btnAdd.setOnClickListener{
            addRecord(employeeDao = employeeDao)
        }
    }

    fun addRecord(employeeDao: EmployeeDao){
        val name = binding.etName.text.toString()
        val email = binding.etEmailId.text.toString()

        if(email.isNotEmpty() && email.isNotEmpty()){
            lifecycleScope.launch{
                employeeDao.insert(EmployeeEntity(name=name, email=email))
                // 여기는 코루틴의 쓰레드 이기 때문에 this로 현재 context를 넘겨줄 수 없다.
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_SHORT).show()
                binding.etName.text.clear()
                binding.etEmailId.text.clear()
            }
        }else{
            Toast.makeText(applicationContext, "Name or Email cannot be blank", Toast.LENGTH_SHORT).show()
        }
    }
}