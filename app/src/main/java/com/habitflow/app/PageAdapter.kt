package com.habitflow.app
import android.view.*
import androidx.recyclerview.widget.RecyclerView
class PageAdapter(private val pages:List<View>):RecyclerView.Adapter<PageAdapter.V>(){override fun onCreateViewHolder(p:ViewGroup,v:Int)=V(pages[v]);override fun getItemCount()=pages.size;override fun onBindViewHolder(h:V,p:Int){};class V(v:View):RecyclerView.ViewHolder(v)}
