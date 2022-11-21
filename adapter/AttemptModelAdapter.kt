package com.ltl.mpmp_lab3.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.ltl.mpmp_lab3.R
import com.ltl.mpmp_lab3.attempt.AttemptModel
import com.ltl.mpmp_lab3.databinding.AttemptListItemBinding
import java.text.SimpleDateFormat
import java.util.*

class AttemptModelAdapter(context: Context, resource: Int, private val items: MutableList<AttemptModel>) :
    ArrayAdapter<AttemptModel>(context, resource, items) {


    override fun getView(position: Int, view: View?, parent: ViewGroup): View {

        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.attempt_list_item, null, true)

        val dateText = rowView.findViewById(R.id.dateTextView) as TextView
        val scoreText = rowView.findViewById(R.id.scoreTextView) as TextView
        val difficultyText = rowView.findViewById(R.id.difficultyTextView) as TextView

        val t = items[position].createdAt
        val milliseconds = t.seconds * 1000 + t.nanoseconds / 1000000
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(milliseconds)
        val date = sdf.format(netDate).toString()

        dateText.text = date
//        dateText.text = items.value?.get(position)?.createdAt.toString()
        scoreText.text = items[position].score.toString()
        difficultyText.text = items[position].difficulty

        return rowView
    }
}