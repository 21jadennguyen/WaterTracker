package com.example.project5

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.project5.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var lineChart: LineChart
    private lateinit var totalTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Room Database
        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "water_database"
        ).build()

        lineChart = view.findViewById(R.id.lineChart)
        totalTextView = view.findViewById(R.id.totalTextView)

        // Display the total water intake for the current day in the TextView
        displayTotalWaterIntake()

        // Display the line chart with total water intake per day
        displayLineChart()
    }

    // Display the total water intake per day in TextView
    private fun displayTotalWaterIntake() {
        lifecycleScope.launch {
            // Fetch total water intake per day
            val dailyIntakes = db.waterEntryDao().getTotalWaterIntakePerDay()

            if (dailyIntakes.isNotEmpty()) {
                // Calculate the total intake for the last day
                val lastDayTotal = dailyIntakes.last().totalVolume
                val totalText = "Total Intake (Last Day): $lastDayTotal ml"
                totalTextView.text = totalText
            } else {
                totalTextView.text = "Total Intake: 0 ml"
            }
        }
    }

    // Display the line chart with total water intake per day
    private fun displayLineChart() {
        lifecycleScope.launch {
            // Fetch total water intake per day
            val dailyIntakes = db.waterEntryDao().getTotalWaterIntakePerDay()

            if (dailyIntakes.isEmpty()) {
                Log.e("DashboardFragment", "No entries found in database")
                return@launch
            }

            // Prepare data for the line chart
            val dailyTotalIntake = mutableListOf<Entry>()
            val dateList = mutableListOf<String>()

            // Add the data points for each day
            dailyIntakes.forEachIndexed { index, dailyIntake ->
                dailyTotalIntake.add(Entry(index.toFloat(), dailyIntake.totalVolume.toFloat()))
                dateList.add(dailyIntake.date)
            }

            // Create LineDataSet and set the data for the chart
            val dataSet = LineDataSet(dailyTotalIntake, "Total Water Intake")
            dataSet.color = Color.GREEN
            dataSet.valueTextColor = Color.BLACK
            dataSet.valueTextSize = 10f
            dataSet.setDrawFilled(true)
            dataSet.fillColor = Color.GREEN

            // Create LineData and set it to the chart
            val lineData = LineData(dataSet)
            lineChart.data = lineData
            lineChart.invalidate() // Refresh the chart

            lineChart.description.text = "Water Intake Per Day"
            lineChart.description.textColor = Color.BLACK
            lineChart.description.textSize = 14f


            // Configure the x-axis to show the date labels
            val xAxis = lineChart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(dateList)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f // Show a label for each point
            lineChart.xAxis.setLabelCount(dailyTotalIntake.size, true)
        }
    }
}


