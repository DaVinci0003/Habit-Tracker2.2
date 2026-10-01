package com.habitflow.app

import android.view.*
import android.widget.*
import android.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class HabitAdapter(private val onToggle:(Habit,Int?)->Unit, private val onLong:(ViewHolder)->Unit, private val onOpen:(Habit)->Unit): RecyclerView.Adapter<HabitAdapter.ViewHolder>() {
    var items=mutableListOf<Habit>()
    fun setData(x:MutableList<Habit>){items=x;notifyDataSetChanged()}
    override fun onCreateViewHolder(p:ViewGroup,v:Int)=ViewHolder(LayoutInflater.from(p.context).inflate(com.habitflow.app.R.layout.item_habit,p,false))
    override fun getItemCount()=items.size
    override fun onBindViewHolder(h:ViewHolder,pos:Int){val item=items[pos]; h.name.text=item.name; h.meta.text=when(item.type){"minutes"->"${item.target} dk hedef • ${fmt(item.hour,item.minute)}";"reps"->"${item.target} tekrar hedef • ${fmt(item.hour,item.minute)}";else->"Her seçili gün • ${fmt(item.hour,item.minute)}"}; h.check.isChecked=item.logs.containsKey(Store.key()); h.value.visibility=if(item.type=="check")View.GONE else View.VISIBLE; h.value.text=item.logs[Store.key()]?.let{if(item.type=="minutes")"${it} dk" else "$it tekrar"} ?: "—"; h.itemView.setOnClickListener{onOpen(item)}; h.check.setOnClickListener{ if(item.type=="check") onToggle(item,null) else { val e=EditText(h.itemView.context); e.inputType=2; e.hint=if(item.type=="minutes")"Dakika" else "Tekrar"; AlertDialog.Builder(h.itemView.context).setTitle(item.name).setMessage("Bugün ne kadar yaptın?").setView(e).setPositiveButton("Kaydet"){_,_->onToggle(item,e.text.toString().toIntOrNull()?:0)}.setNegativeButton("İptal",null).show() } }; h.drag.setOnLongClickListener{onLong(h);true} }
    private fun fmt(h:Int,m:Int)="%02d:%02d".format(h,m)
    class ViewHolder(v:View):RecyclerView.ViewHolder(v){val drag:TextView=v.findViewById(R.id.drag);val name:TextView=v.findViewById(R.id.name);val meta:TextView=v.findViewById(R.id.meta);val value:TextView=v.findViewById(R.id.value);val check:CheckBox=v.findViewById(R.id.check)}
}
