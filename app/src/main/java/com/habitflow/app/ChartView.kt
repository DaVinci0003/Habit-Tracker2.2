package com.habitflow.app

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ChartView @JvmOverloads constructor(c: Context,a: AttributeSet?=null): View(c,a) {
    var values: List<Float> = emptyList(); var labels: List<String> = emptyList(); var mode="line"
    private val p=Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas: Canvas) { super.onDraw(canvas); if(values.isEmpty()) return
        val w=width.toFloat(); val h=height.toFloat(); val left=42f; val right=w-18; val top=24f; val bottom=h-32
        p.style=Paint.Style.STROKE; p.strokeWidth=1f; p.color=Color.rgb(220,225,220); for(i in 0..4){val y=top+(bottom-top)*i/4f; canvas.drawLine(left,y,right,y,p)}
        val max=(values.maxOrNull()?:1f).coerceAtLeast(1f); p.color=Color.rgb(78,125,91); p.strokeWidth=5f; p.style=Paint.Style.STROKE
        val path=Path(); values.forEachIndexed{ i,v -> val x=left+(right-left)*(if(values.size==1)0f else i.toFloat()/(values.size-1)); val y=bottom-(bottom-top)*(v/max); if(i==0)path.moveTo(x,y) else path.lineTo(x,y)}; canvas.drawPath(path,p)
        p.style=Paint.Style.FILL; p.color=Color.rgb(49,84,59); values.forEachIndexed{i,v-> val x=left+(right-left)*(if(values.size==1)0f else i.toFloat()/(values.size-1)); val y=bottom-(bottom-top)*(v/max); canvas.drawCircle(x,y,4f,p)}
        p.color=Color.DKGRAY; p.textSize=24f; canvas.drawText(max.toInt().toString(),6f,top+8,p); canvas.drawText("0",20f,bottom+6,p)
        p.textSize=20f; if(labels.isNotEmpty()){canvas.drawText(labels.first(),left,bottom+24,p); canvas.drawText(labels.last(),right-55,bottom+24,p)}
    }
}
