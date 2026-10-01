package com.habitflow.app

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.*
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.*

class MainActivity:AppCompatActivity(){
 lateinit var store:Store; lateinit var drawer:DrawerLayout; lateinit var pager:ViewPager2; lateinit var pages:List<View>; lateinit var statsChart:ChartView; lateinit var habitChart:ChartView; var selectedHabit:Habit?=null
 val req=registerForActivityResult(ActivityResultContracts.RequestPermission()){}
 override fun onCreate(b:Bundle?){super.onCreate(b);store=Store(this);applyTheme();setContentView(R.layout.activity_main); setup()}
 private fun applyTheme(){AppCompatDelegate.setDefaultNightMode(if(store.themeDark())AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)}
 private fun setup(){
  drawer=findViewById(R.id.drawer);pager=findViewById(R.id.pager);findViewById<ImageButton>(R.id.menuBtn).setOnClickListener{drawer.openDrawer(Gravity.LEFT)}
  findViewById<TextView>(R.id.dateText).text=SimpleDateFormat("d MMMM yyyy",Locale("tr")).format(Date()); buildDays()
  pages=listOf(homePage(),statsPage(),streakPage(),settingsPage());pager.adapter=PageAdapter(pages);pager.offscreenPageLimit=3
  val nav=findViewById<NavigationView>(R.id.nav);nav.setNavigationItemSelectedListener{when(it.itemId){R.id.nav_home->go(0);R.id.nav_stats->go(1);R.id.nav_streaks->go(2);R.id.nav_add->{showHabitDialog();drawer.closeDrawer(Gravity.LEFT);return@setNavigationItemSelectedListener true};R.id.nav_settings->go(3)};drawer.closeDrawer(Gravity.LEFT);true}
  refreshAll()
 }
 fun go(i:Int){pager.currentItem=i}
 private fun buildDays(){val row=findViewById<LinearLayout>(R.id.dayRow);row.removeAllViews();val names=arrayOf("Pzt","Sal","Çar","Per","Cum","Cmt","Paz");val cal=Calendar.getInstance();val current=((cal.get(Calendar.DAY_OF_WEEK)+5)%7);for(i in 0..6){val t=TextView(this);t.text=names[i];t.textSize=14f;t.gravity=Gravity.CENTER;t.setTextColor(if(i==current)android.graphics.Color.WHITE else android.graphics.Color.rgb(49,84,59));t.setBackgroundResource(if(i==current)R.drawable.bg_selected_day else R.drawable.bg_day);t.alpha=if(i==current)1f:.9f;t.setPadding(20,12,20,12);val lp=LinearLayout.LayoutParams(-2,-2);lp.setMargins(4,0,4,12);row.addView(t,lp)}}
 private fun homePage():View{val v=layoutInflater.inflate(R.layout.page_home,null);val rv=v.findViewById<RecyclerView>(R.id.habitList);rv.layoutManager=LinearLayoutManager(this);val adapter=HabitAdapter({h,value->val key=Store.key();if(value==null)h.logs[key]=1 else h.logs[key]=value;store.save(store.load().apply{find{it.id==h.id}?.logs=h.logs});refreshAll()},{vh->vh.itemView.animate().scaleX(.98f).scaleY(.98f).setDuration(90).withEndAction{vh.itemView.animate().scaleX(1f).scaleY(1f).start()}.start()},{h->selectedHabit=h;updateHabitChart();go(1)});rv.adapter=adapter
  val touch=ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,0){override fun onMove(r:RecyclerView,vh:RecyclerView.ViewHolder,target:RecyclerView.ViewHolder):Boolean{val from=vh.bindingAdapterPosition;val to=target.bindingAdapterPosition;Collections.swap(adapter.items,from,to);adapter.notifyItemMoved(from,to);store.save(adapter.items);return true};override fun onSwiped(vh:RecyclerView.ViewHolder,d:Int){}});touch.attachToRecyclerView(rv);v.findViewById<Button>(R.id.addHabit).setOnClickListener{showHabitDialog()};v.tag=adapter;return v}
 private fun statsPage():View{val v=layoutInflater.inflate(R.layout.page_stats,null);statsChart=v.findViewById(R.id.monthChart);habitChart=v.findViewById(R.id.habitChart);return v}
 private fun streakPage():View=layoutInflater.inflate(R.layout.page_streaks,null)
 private fun settingsPage():View{val v=layoutInflater.inflate(R.layout.page_settings,null);val dark=v.findViewById<SwitchMaterial>(R.id.darkMode);dark.isChecked=store.themeDark();dark.setOnCheckedChangeListener{_,x->store.setThemeDark(x);applyTheme()};val n=v.findViewById<SwitchMaterial>(R.id.notifications);n.isChecked=store.notifications();n.setOnCheckedChangeListener{_,x->store.setNotifications(x);store.load().forEach{if(x)Scheduler.scheduleNext(this,it) else Scheduler.cancel(this,it)}};v.findViewById<Button>(R.id.requestNotifications).setOnClickListener{if(Build.VERSION.SDK_INT>=33)req.launch(Manifest.permission.POST_NOTIFICATIONS)};return v}
 private fun refreshAll(){val list=store.load();findViewById<TextView>(R.id.streakText).text="${overallStreak(list)} gün";val home=pages.firstOrNull();home?.findViewById<RecyclerView>(R.id.habitList)?.adapter?.let{(it as HabitAdapter).setData(list)};home?.findViewById<TextView>(R.id.completion)?.text="${list.count{it.logs.containsKey(Store.key())}}/${list.size} tamamlandı";updateStats(list);updateStreaks(list);list.forEach{Scheduler.scheduleNext(this,it)}}
 private fun overallStreak(list:List<Habit>):Int{var d=0;val c=Calendar.getInstance();while(true){val k=Store.key(c);if(list.isNotEmpty()&&list.all{it.logs.containsKey(k)}){d++;c.add(Calendar.DAY_OF_YEAR,-1)}else break};return d}
 private fun updateStats(list:List<Habit> = store.load()){if(!::statsChart.isInitialized)return;val vals=mutableListOf<Float>();val c=Calendar.getInstance();c.add(Calendar.DAY_OF_YEAR,-29);repeat(30){val k=Store.key(c);vals.add(if(list.isEmpty())0f else list.count{it.logs.containsKey(k)}.toFloat());c.add(Calendar.DAY_OF_YEAR,1)};statsChart.values=vals;statsChart.labels=listOf("30 gün önce","Bugün");statsChart.invalidate();updateHabitChart()}
 private fun updateHabitChart(){if(!::habitChart.isInitialized)return;val h=selectedHabit?:store.load().firstOrNull();habitChart.values=if(h==null)emptyList() else {val c=Calendar.getInstance();c.add(Calendar.DAY_OF_YEAR,-29);val a=mutableListOf<Float>();repeat(30){a.add((h.logs[Store.key(c)]?:0).toFloat());c.add(Calendar.DAY_OF_YEAR,1)};a};habitChart.labels=listOf("30 gün önce","Bugün");habitChart.invalidate();findViewById<TextView?>(R.id.habitChartTitle)?.text=h?.let{"${it.name} • geçmiş"}?:"Alışkanlık grafiği"}
 private fun updateStreaks(list:List<Habit>){if(pages.size<3)return;val box=pages[2].findViewById<LinearLayout>(R.id.streakList);box.removeAllViews();list.forEach{h->val t=TextView(this);t.text="${h.name}\n${streak(h)} gün seri";t.textSize=18f;t.setTextColor(android.graphics.Color.rgb(24,32,27));t.setPadding(18,16,18,16);t.background=getDrawable(R.drawable.bg_card);val lp=LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,0,0,10);box.addView(t,lp)}}
 private fun streak(h:Habit):Int{var n=0;val c=Calendar.getInstance();while(h.logs.containsKey(Store.key(c))){n++;c.add(Calendar.DAY_OF_YEAR,-1)};return n}
 private fun showHabitDialog(){val view=layoutInflater.inflate(R.layout.dialog_habit,null);val name=view.findViewById<EditText>(R.id.nameInput);val group=view.findViewById<RadioGroup>(R.id.typeGroup);val target=view.findViewById<EditText>(R.id.targetInput);group.setOnCheckedChangeListener{_,id->target.visibility=if(id==R.id.typeCheck)View.GONE else View.VISIBLE};var hour=20;var minute=0;val time=view.findViewById<Button>(R.id.timeButton);time.text="Bildirim: 20:00";time.setOnClickListener{TimePickerDialog(this,{_,h,m->hour=h;minute=m;time.text="Bildirim: %02d:%02d".format(h,m)},hour,minute,true).show()};val week=view.findViewById<LinearLayout>(R.id.weekRow);val days=arrayOf("P","S","Ç","P","C","C","P");val selected=BooleanArray(7){true};days.forEachIndexed{i,s->val b=CheckBox(this);b.text=s;b.isChecked=true;b.buttonTintList=ContextCompat.getColorStateList(this,R.color.sage);b.setOnCheckedChangeListener{_,x->selected[i]=x};week.addView(b)};val end=view.findViewById<SwitchMaterial>(R.id.endEnabled);val endDays=view.findViewById<EditText>(R.id.endDays);end.setOnCheckedChangeListener{_,x->endDays.visibility=if(x)View.VISIBLE else View.GONE};AlertDialog.Builder(this).setTitle("Yeni alışkanlık").setView(view).setPositiveButton("Kaydet"){_,_->val n=name.text.toString().trim();if(n.isEmpty())return@setPositiveButton;val type=when(group.checkedRadioButtonId){R.id.typeMinutes->"minutes";R.id.typeReps->"reps";else->"check"};var mask=0;selected.forEachIndexed{i,x->if(x)mask=mask or (1 shl i)};val h=Habit(System.currentTimeMillis(),n,type,target.text.toString().toIntOrNull()?:0,hour,minute,mask,if(end.isChecked)endDays.text.toString().toIntOrNull()?:0,store.load().size);val list=store.load();list.add(h);store.save(list);Scheduler.scheduleNext(this,h);refreshAll()}.setNegativeButton("İptal",null).show()}
 override fun onResume(){super.onResume();if(::pages.isInitialized)refreshAll()}
}
