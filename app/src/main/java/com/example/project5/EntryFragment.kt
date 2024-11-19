package com.example.project5

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.project5.databinding.FragmentEntryBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class EntryFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var binding: FragmentEntryBinding
    private lateinit var adapter: WaterEntryAdapter
    private val waterEntries = mutableListOf<WaterEntry>()
    private var selectedDate: String = getCurrentDate()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "water_database"
        ).build()

        adapter = WaterEntryAdapter(waterEntries)
        binding.entriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.entriesRecyclerView.adapter = adapter

        db.waterEntryDao().getAllEntries().observe(viewLifecycleOwner, { entries ->
            waterEntries.clear()
            waterEntries.addAll(entries)
            adapter.notifyDataSetChanged()
        })

        binding.dateText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.submitButton.setOnClickListener {
            val volume = binding.waterIntakeInput.text.toString().toIntOrNull()
            if (volume != null) {
                val entry = WaterEntry(date = selectedDate, volume = volume)
                lifecycleScope.launch {
                    db.waterEntryDao().insert(entry)
                    binding.waterIntakeInput.text.clear()
                }
            } else {
                Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }

        binding.clearDatabaseButton.setOnClickListener {
            lifecycleScope.launch {
                db.waterEntryDao().clearAll()
                Toast.makeText(context, "Database Cleared", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate = "$year-${month + 1}-$dayOfMonth"
                binding.dateText.text = "Selected Date: $selectedDate"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return String.format("%04d-%02d-%02d", year, month, day)
    }

}
