package com.leethcher.roomdemo

import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.leethcher.roomdemo.databinding.ItemsRowBinding

class ItemAdapter(private val items: ArrayList<EmployeeEntity>,
                  private val updateListener: (id: Int) -> Unit,
                  private val deleteListener: (id: Int) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val llMain = binding.llMain
        val tvName = binding.tvName
        val tvEmail = binding.tvEmail
        val ivEdit = binding.ivEdit
        val ivDelete = binding.ivDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemsRowBinding.inflate(LayoutInflater.from(parent.context)
            , parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        // EmployeeEntity
        val item = items[position]

        holder.tvName.text = item.name
        holder.tvEmail.text = item.email

        if(position % 2 == 0){
            holder.llMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.lightGray))
        }else{
            holder.llMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }

        // 각 아이템들의 업데이트, 삭제 버튼을 누르면 아래 클릭 리스너들이 동작하고,
        // 위 ItemAdapter의 매개변수 updateListener와 deleteListener에게 item.의 id를 전달한다.
        // 이 때 invoke메서드는 함수 또는 람다함수에게 매개변수를 전달하고, 함수 또는 람다 함수의 반환값을 가져온다.
        holder.ivEdit.setOnClickListener{
            updateListener.invoke(item.id)
        }

        holder.ivDelete.setOnClickListener{
            deleteListener.invoke(item.id)
        }
    }
}