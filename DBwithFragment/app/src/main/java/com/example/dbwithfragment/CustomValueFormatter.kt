package com.example.dbwithfragment

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

class CustomValueFormatter : IAxisValueFormatter {
    private lateinit var lineChart : LineChart

    constructor(lineChart: LineChart) {
        this.lineChart = lineChart
    }
    override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
        var data : String? = null
        var index = value.toInt()
        index -= 1

        if(index >= 0 && index < lineChart.data.getDataSetByIndex(0).entryCount)
            data = lineChart.data.getDataSetByIndex(0).getEntryForIndex(index).data.toString()

        if(index<0)
            return " ";
        return data

    }

}