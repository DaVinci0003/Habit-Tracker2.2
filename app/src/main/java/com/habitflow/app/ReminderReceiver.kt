package com.habitflow.app

import android.app.*
import android.content.*
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.*

class ReminderReceiver: BroadcastReceiver(){
 override fun onReceive(context:Context,intent:Intent){
  val id=intent.getLongExtra("habitId",-1); val h=Store(context).load().firstOrNull{it.id==id}?:return
  val nm=context.getSystemService(NotificationManager::class.java); if(Build.VERSION.SDK_INT>=26) nm.createNotificationChannel(NotificationChannel("habitflow","HabitFlow",NotificationManager.IMPORTANCE_DEFAULT))
  val n=NotificationCompat.Builder(context,"habitflow").setSmallIcon(android.R.drawable.ic_popup_reminder).setContentTitle("HabitFlow • ${h.name}").setContentText("${String.format("%02d:%02d",h.hour,h.minute)} zamanı — kaçırma!").setAutoCancel(true).build(); nm.notify(id.toInt(),n)
  Scheduler.scheduleNext(context,h)
 }
}
object Scheduler{
 fun scheduleNext(c:Context,h:Habit){
  if(!Store(c).notifications()) return
  val am=c.getSystemService(AlarmManager::class.java); val now=Calendar.getInstance(); var next=Calendar.getInstance(); next.set(Calendar.SECOND,0);next.set(Calendar.MILLISECOND,0);next.set(Calendar.HOUR_OF_DAY,h.hour);next.set(Calendar.MINUTE,h.minute)
  for(i in 0..7){ if(i>0) next.add(Calendar.DAY_OF_YEAR,1); val dow=next.get(Calendar.DAY_OF_WEEK); val bit=when(dow){Calendar.MONDAY->1;Calendar.TUESDAY->2;Calendar.WEDNESDAY->4;Calendar.THURSDAY->8;Calendar.FRIDAY->16;Calendar.SATURDAY->32;else->64}; if(next.after(now)&&h.daysMask and bit!=0)break }
  val intent=Intent(c,ReminderReceiver::class.java).putExtra("habitId",h.id); val pi=PendingIntent.getBroadcast(c,h.id.toInt(),intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
  if(Build.VERSION.SDK_INT>=31 && !am.canScheduleExactAlarms()) am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,next.timeInMillis,pi) else am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,next.timeInMillis,pi)
 }
 fun cancel(c:Context,h:Habit){c.getSystemService(AlarmManager::class.java).cancel(PendingIntent.getBroadcast(c,h.id.toInt(),Intent(c,ReminderReceiver::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))}
}
