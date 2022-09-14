package com.jwhh.notekeeper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.SeekBar

@SuppressLint("AppCompatCustomView")
class ColorSlider @JvmOverloads constructor(context: Context,
                                            attrs: AttributeSet? = null,
                                            defStyleAttr: Int = R.attr.seekBarStyle,
                                            defStyleRes: Int = 0)
    : SeekBar(context, attrs, defStyleAttr, defStyleRes) {
        private var colors: ArrayList<Int> = arrayListOf(Color.RED, Color.YELLOW, Color.BLUE)

    private val w = getPixelValueFromDP(16f)
    private val h = getPixelValueFromDP(16f)
    private val halfW = if (w >= 0) w/2 else getPixelValueFromDP(1f)
    private val halfH = if (h >= 0) h/2 else getPixelValueFromDP(1f)

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorSlider)
        colors = typedArray.getTextArray(R.styleable.ColorSlider_colors)
                .map {
                    Color.parseColor(it.toString())
                } as ArrayList<Int>
        typedArray.recycle()
        max = colors.size -1
        progressBackgroundTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
        progressTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
        splitTrack = false
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom +  getPixelValueFromDP(16f).toInt())
        thumb = context.getDrawable(R.drawable.ic_color_slider_thumb)

        setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                listeners.forEach{
                    it(colors[progress])
                    print("color progess " + colors[progress].toString())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    var selectedColorValue: Int = android.R.color.transparent
        set(value) {
            var index = colors.indexOf(value)
            if(index == -1){
                progress = 0
            }
            else {
                progress = index
            }
        }

    private var listeners: ArrayList<(Int) -> Unit> = arrayListOf()
    fun addListener(function: (Int) -> Unit){
        listeners.add(function)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawTickMarks(canvas)
    }

    private fun drawTickMarks(canvas: Canvas?){
        canvas?.let {
            val count = colors.size
            val saveCount = canvas.save()
            canvas.translate(paddingLeft.toFloat(), (height/2).toFloat() + 50f)
            if(count >1){
                for(i in 0 until count) {
                    val spacing = (width - paddingLeft - paddingRight )/ (count - 1).toFloat()
                    val paint = Paint()
                    paint.color = colors[i]
                    canvas.drawRect(-halfW, -halfH,
                                    halfW, halfH, paint)
                    canvas.translate(spacing, 0f)
                }
                canvas.restoreToCount(saveCount)
            }
        }
    }

     private fun getPixelValueFromDP(value: Float): Float{
         return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                            value, context.resources.displayMetrics)
     }

    }