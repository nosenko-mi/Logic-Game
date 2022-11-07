package com.ltl.mpmp_lab3.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.ltl.mpmp_lab3.R
import com.ltl.mpmp_lab3.databinding.FragmentResultBinding
import com.ltl.mpmp_lab3.utility.EmailPreferenceHandler
import com.ltl.mpmp_lab3.utility.MailSender

class ResultFragment : Fragment() {

    private val args: ResultFragmentArgs by navArgs()
    private lateinit var userDisplayName: String
    private lateinit var userEmail: String
    private var userScore: Int = 0
    private lateinit var mailSender: MailSender

    private var _binding: FragmentResultBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val view = binding.root

        userDisplayName = args.userDisplayName
        userEmail = args.userEmail
        userScore = args.userScore

        renderUi()
        sendMessage()
/*        mainSender uses activity context so it should be initialized inside onCreateView
        if (savedInstanceState != null) {
            mailSender = savedInstanceState.getSerializable("mailSender") as MailSender
        } else{
            mailSender = MailSender(context)
        }*/
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener{
            goToGame();
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
        binding.displayNameTextView.text =
            String.format(getString(R.string.username_gained), userDisplayName)

        binding.pointsTextView.text =
            resources.getQuantityString(R.plurals.point_plurals, userScore, userScore)
    }

    fun sendMessage(){
        mailSender = MailSender.getInstance(context)

        val isEmailOn = EmailPreferenceHandler.get(context)
        if (isEmailOn) {
            mailSender.sendInNotSent(userEmail, userDisplayName, userScore)
        }
    }

    private fun goToGame() {
        val action = ResultFragmentDirections.backToGameFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }


}