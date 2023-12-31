package com.leethcher.roomdemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.leethcher.roomdemo.databinding.ActivityMainBinding
import com.leethcher.roomdemo.databinding.DialogUpdateBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val employeeDao = (application as EmployeeApp).db.employeeDao()
        binding.btnAdd.setOnClickListener{
            addRecord(employeeDao)
        }
        lifecycleScope.launch {
            employeeDao.fetchAllEmployees().collect{
                val list = ArrayList(it)
                //setupListOfDateIntorecyclerView(list, employeeDao)
            }
        }

        lifecycleScope.launch {
            employeeDao.fetchAllEmployees().collect{
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list, employeeDao)
            }
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

    private fun setupListOfDataIntoRecyclerView(employeeList: ArrayList<EmployeeEntity>, employeeDao: EmployeeDao){
        if(employeeList.isNotEmpty()){
            // ItemAdapter의 2번째, 3번째 매개변수에 람다함수를 넣었다.
            // 전달 받는 값은 둘 다 Id로 동일하나 헷갈리지 않도록 이름은 다르게 했다.
            val itemAdapter = ItemAdapter(employeeList,
                { updateId ->
                    updateRecordDialog(updateId, employeeDao)
                },
                {deleteId ->
                    deleteRecordAlertDialog(deleteId, employeeDao)
                }
            )

            binding.rvItemsList.layoutManager = LinearLayoutManager(this)
            binding.rvItemsList.adapter = itemAdapter
            binding.rvItemsList.visibility = View.VISIBLE
            binding.tvNoRecordsAvailable.visibility = View.GONE
        }else{
            binding.rvItemsList.visibility = View.GONE
            binding.tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    private fun updateRecordDialog(id: Int, employeeDao: EmployeeDao) {
        val updateDialog = Dialog(this@MainActivity, R.style.Theme_Dialog)
        // 다른 곳을 눌러 취소할 수 없다
        updateDialog.setCancelable(false)
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect{
                binding.etName.setText(it.name)
                binding.etEmail.setText(it.email)
            }
        }

        binding.tvUpdate.setOnClickListener{
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()

            if(name.isNotEmpty() && email.isNotEmpty()){
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id, name, email))
                    Toast.makeText(applicationContext, "Record Updated.", Toast.LENGTH_LONG).show()
                    updateDialog.dismiss()
                }
            }else{
                Toast.makeText(applicationContext, "Name or Email cannot be blank", Toast.LENGTH_LONG).show()
            }
        }
        binding.tvCancel.setOnClickListener{
            updateDialog.dismiss()
        }

        updateDialog.show();
    }

    private fun deleteRecordAlertDialog(id: Int, employeeDao: EmployeeDao) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes"){dialogInterface, _ ->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(applicationContext, "Record deleted successfully.",
                    Toast.LENGTH_LONG).show()
            }
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No"){dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}