package com.ltl.mpmp_lab3.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.ltl.mpmp_lab3.R
import com.ltl.mpmp_lab3.adapter.AttemptModelAdapter
import com.ltl.mpmp_lab3.attempt.AttemptModel
import com.ltl.mpmp_lab3.attempt.AttemptsViewModel
import com.ltl.mpmp_lab3.databinding.FragmentResultBinding
import com.ltl.mpmp_lab3.user.UserModel
import com.ltl.mpmp_lab3.user.UserViewModel
import com.ltl.mpmp_lab3.utility.EmailPreferenceHandler
import com.ltl.mpmp_lab3.utility.MailSender
import java.text.SimpleDateFormat
import java.util.*

class ResultFragment : Fragment() {

    private val TAG = "resultFragment"
    private val args: ResultFragmentArgs by navArgs()
    private lateinit var userDisplayName: String
    private lateinit var userEmail: String
    private var userScore: Long = 0
    private lateinit var mailSender: MailSender

    private lateinit var adapter: AttemptModelAdapter

    private var _binding: FragmentResultBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.my_nav)
    private val attemptsViewModel: AttemptsViewModel by navGraphViewModels(R.id.my_nav)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val view = binding.root

        userDisplayName = args.userDisplayName
        userEmail = args.userEmail
        userScore = args.userScore

        renderUi()
        sendMessage()

//        TODO: https://developer.android.com/codelabs/advanced-kotlin-coroutines#0
//        crashes the app
//        val adapter = AttemptModelAdapter(requireContext(), R.layout.attempt_list_item, attemptsViewModel.getAttempts())
//        attemptsViewModel.getAttempts().observe(viewLifecycleOwner){
//            Log.d(TAG, it.size.toString())
//            binding.tempTextView.text = "true"
//            binding.scoreListView.adapter = adapter
//        }


        attemptsViewModel.getAttempts().observe(viewLifecycleOwner){
            Log.d(TAG, it.size.toString())
            if (it.size > 0){
                binding.tempTextView.text = "Статистика:"
                adapter = AttemptModelAdapter(requireContext(), R.layout.attempt_list_item, it)
                binding.scoreListView.adapter = adapter
//                createScoreTable(it)
            }
        }

        userViewModel.getName().observe(viewLifecycleOwner) {
            binding.displayNameTextView.text =
                String.format(getString(R.string.username_gained), it.toString())
        }

        userViewModel.getRecord().observe(viewLifecycleOwner) {
            binding.recordTextView.text =
                String.format(getString(R.string.current_record_text), it)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attemptsViewModel.getAttemptsLimited(userEmail, 6)


        binding.backButton.setOnClickListener{
            goToGame()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("mailSender", mailSender)
    }

    fun renderUi(){
//        binding.displayNameTextView.text =
//            String.format(getString(R.string.username_gained), userDisplayName)

        binding.pointsTextView.text =
            resources.getQuantityString(R.plurals.point_plurals, userScore.toInt(), userScore)
    }

//    fun createScoreTable(attempts: MutableList<AttemptModel>){
//        for (a in attempts){
//            val row = TableRow(context)
//            val lp = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
//            row.layoutParams = lp;
//
//            val milliseconds = a.createdAt.seconds * 1000 + a.createdAt.nanoseconds / 1000000
//            val sdf = SimpleDateFormat("MM/dd/yyyy")
//            val netDate = Date(milliseconds)
//            val date = sdf.format(netDate).toString()
//            Log.d(TAG, date)
//
//            val textDate = TextView(context)
//            textDate.text = date
//
//            val textScore = TextView(context)
//            textScore.text = a.score.toString()
//
//            val textDifficulty = TextView(context)
//            textDifficulty.text = a.difficulty
//
//            row.addView(textDate)
//            row.addView(textScore)
//            row.addView(textDifficulty)
//
//            binding.scoresTableLayout?.addView(row)
//        }
//    }

    fun sendMessage(){
        mailSender = MailSender.getInstance()

        val isEmailOn = EmailPreferenceHandler.get(context)
        if (isEmailOn) {
            mailSender.sendInNotSent(context, userEmail, userDisplayName, userScore.toInt())
        }
    }

    private fun goToGame() {


        val userModel = UserModel(userEmail, userDisplayName, userScore)
        val action = ResultFragmentDirections.backToGameFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }


}