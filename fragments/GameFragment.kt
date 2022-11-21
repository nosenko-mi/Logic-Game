package com.ltl.mpmp_lab3.fragments

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ltl.mpmp_lab3.R
import com.ltl.mpmp_lab3.attempt.AttemptModel
import com.ltl.mpmp_lab3.attempt.AttemptModelRepository
import com.ltl.mpmp_lab3.attempt.AttemptsViewModel
import com.ltl.mpmp_lab3.constants.AnswerOption
import com.ltl.mpmp_lab3.constants.Duration
import com.ltl.mpmp_lab3.databinding.FragmentGameBinding
import com.ltl.mpmp_lab3.user.User
import com.ltl.mpmp_lab3.user.UserViewModel
import com.ltl.mpmp_lab3.utility.EmailPreferenceHandler
import com.ltl.mpmp_lab3.utility.GameMaster
import com.ltl.mpmp_lab3.utility.PenaltyHandler
import java.io.FileNotFoundException


class GameFragment : Fragment() {

    private lateinit var difficulty: String

    //    private var _binding: FragmentGameBinding? = null
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
    private val TAG: String = "mainFragment"


    private lateinit var binding: FragmentGameBinding
    private var isAttached: Boolean = false

    var accountGoogle: GoogleSignInAccount? = null
    var gso: GoogleSignInOptions? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
    private var accountFirebase: FirebaseUser? = null
    private lateinit var currentUser: User

    private var timer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0L
    private var mEndTime: Long? = 0L
    private var points: Int = 0
    private var isStared: Boolean = false
    private var penalty: Int = 1
    private lateinit var gm: GameMaster

    private var checkedMenuItemId = 0
    var optionsMenu:Menu? = null

    private lateinit var colorNames: Array<String>
    private lateinit var colors: IntArray
    private val colorsMap = HashMap<String, Int>()

    private val userViewModel: UserViewModel by navGraphViewModels(R.id.my_nav)
    private val attemptsViewModel: AttemptsViewModel by navGraphViewModels(R.id.my_nav)
    private val attemptRepository: AttemptModelRepository = AttemptModelRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gm = GameMaster(requireActivity().applicationContext)
        initColorsMap()

        difficulty = getString(R.string.game_difficulty_normal)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity!!.finish()
            }
        })
    }

    private fun initColorsMap(){
        colorNames = resources.getStringArray(R.array.color_names_array)
        colors = resources.getIntArray(R.array.game_colors_array)
        require(colorNames.size == colors.size) { "The number of keys doesn't match the number of values." }
        for (i in colorNames.indices) {
            colorsMap[colorNames[i]] = colors[i]
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        _binding = FragmentGameBinding.inflate(inflater, container, false)
//        val view = binding.root

        binding = FragmentGameBinding.inflate(inflater, container, false)
        val view = binding.root

        shuffle()

        initCurrentUser()

        userViewModel.getName().observe(viewLifecycleOwner) {
            binding.toolbar.title = it.toString()
        }

        userViewModel.getRecord().observe(viewLifecycleOwner) {
            binding.recordTextView.text = it.toString()
        }

        return view
    }

    private fun initCurrentUser(){
        //        accounts:
        mAuth = FirebaseAuth.getInstance()
        accountFirebase = mAuth!!.currentUser

        accountGoogle = GoogleSignIn.getLastSignedInAccount(requireContext())
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso!!)

        if (accountFirebase != null) {
            currentUser = getCurrentUser(accountFirebase!!)
            Log.d(TAG, "accountFirebase : ok")
        }
        if (accountGoogle != null) {
            currentUser = getCurrentUser(accountGoogle!!)
            Log.d(TAG, "accountGoogle : ok")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.yesButton.setOnClickListener {
            handleClick(
                AnswerOption.YES
            )
        }

        binding.noButton.setOnClickListener {
            handleClick(
                AnswerOption.NO
            )
        }

        binding.startButton.setOnClickListener{
            try {
                if (!isStared) {
                    startGame(Duration.TEST_GAME_MILLIS.duration + Duration.ANIMATION_MILLIS.duration)
                } else {
                    finishGameSequence()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

        binding.toolbar.inflateMenu(R.menu.action_bar_menu)
        optionsMenu = binding.toolbar.menu

//        empty user:
//        binding.toolbar.title = userViewModel.currentUser.value!!.displayname
//        binding.toolbar.title = currentUser.displayName
        binding.toolbar.menu.findItem(R.id.email_settings).isChecked =
            EmailPreferenceHandler.get(context)

        binding.toolbar.setOnMenuItemClickListener{
            when (it.itemId) {
                R.id.game_easy_settings, R.id.game_normal_settings, R.id.game_hard_settings -> {
                    setPenalty(it)
                    updateMenus(it)
                    true
                }
                R.id.email_settings -> {
                    it.isChecked = !it.isChecked
                    EmailPreferenceHandler.put(context, it.isChecked)
                    true
                }
                R.id.exit_settings -> {
                    signOut()
                    goToLogin()
                    true
                }
                else ->
                    super.onOptionsItemSelected(it)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (isStared) {
            timeLeftInMillis = mEndTime!! - System.currentTimeMillis()
        }
        try {
            outState.putLong("points", gm.points)
            outState.putLong("millisLeft", timeLeftInMillis)
            outState.putBoolean("isStarted", isStared)
            outState.putLong("endTime", mEndTime!!)
            outState.putSerializable("gm", gm)
        } catch (e: RuntimeException){
            Log.e(TAG, e.toString())
        }


        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            gm = savedInstanceState.getSerializable("gm") as GameMaster
            timeLeftInMillis = savedInstanceState.getLong("millisLeft")
            isStared = savedInstanceState.getBoolean("isStarted")
            mEndTime = savedInstanceState.getLong("endTime")
        }

        if (isStared) {
            binding.pointsTextView.text =
                String.format(getString(R.string.current_points_text), gm.points)
            binding.yesButton.isVisible = true
            binding.noButton.isVisible = true

            startTimer(timeLeftInMillis)

            binding.startButton.text = getString(R.string.stop_button_text)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "isAttached = true")
        isAttached = true
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "isAttached = false")
        isAttached = false
    }

    private fun startGame(timeMillis: Long) {
        Log.d(TAG, "game started")

        isStared = true
        gm.start()
        points = 0
        mEndTime = System.currentTimeMillis() + timeMillis

        startTimer(timeMillis)

        binding.pointsTextView.text = String.format(getString(R.string.current_points_text), points)
        startOpeningAnimations()
    }

    private fun  finishGame() {
        Log.d(TAG, "game finished")
        Log.d(TAG, "finishGame timer: " + timer.toString())

        timer!!.cancel()
        Log.d(TAG, "finishGame timer: " + timer.toString())

        isStared = false
    }

    private fun renderFinishedUi(){
        if (!isAttached) {
            Log.d(TAG, "renderFinishedUi() fragment is not attached")
            return
        }

        Log.d(TAG, "renderFinishedUi() start")
        binding.timerTextView.setText(R.string.finished_text)
        binding.startButton.text = getString(R.string.start_button_text)
        startEndingAnimations()
        Log.d(TAG, "renderFinishedUi() finished")
    }

    private fun finishGameSequence(){
        setNewRecord(gm.points)
        writeAttempt()
        renderFinishedUi()
        finishGame()
        goToResults(currentUser)
    }

    private fun writeAttempt() {
        if (!userViewModel.hasValidUser()) return
        attemptsViewModel.saveCurrentAttempt(
            AttemptModel(
                userViewModel.getEmail().value.toString(),
                gm.points,
                difficulty
            )
        )
//        attemptRepository.saveAttempt(
//            AttemptModel(
//                userViewModel.getEmail().value.toString(),
//                gm.points
//            ))

//        tests only
        attemptRepository.findAttemptsByEmailLimited(userViewModel.getEmail().value.toString(), 3)
    }

    private fun setNewRecord(points: Long) {
        if (userViewModel.hasValidUser() && userViewModel.getRecord().value!! < points){
            Log.d(TAG, "setting new record")
            userViewModel.updateRecord(points)
        }
    }

    private fun startTimer(timeMillis: Long) {
        timer = object : CountDownTimer(timeMillis, 1) {
            override fun onTick(l: Long) {
                timeLeftInMillis = l
                when {
                    l > 60000 -> {
                        binding.timerTextView.text = "01:00"
                    }
                    l > 10000 -> {
                        binding.timerTextView.text = String.format("00:%d", l / 1000)
                    }
                    else -> {
                        binding.timerTextView.text = String.format("00:0%d", l / 1000)
                    }
                }
            }

            override fun onFinish() {
                finishGameSequence()
            }
        }.start()
        Log.d(TAG, "timer: " + timer.toString())
    }

    private fun handleClick(answerOption: AnswerOption) {
        gm.checkAnswer(
            answerOption,
            binding.leftTextView.text,
            binding.rightTextView.currentTextColor
        )
        binding.pointsTextView.text =
            String.format(getString(R.string.current_points_text), gm.points)

        shuffle()
    }

    fun checkAnswer(answer: AnswerOption) {
        val expectedColor: Int = colorsMap[binding.leftTextView.text]!!
        if (expectedColor == binding.rightTextView.currentTextColor && answer == AnswerOption.YES) {
            points++
        } else if (expectedColor != binding.rightTextView.currentTextColor && answer == AnswerOption.NO) {
            points++

        } else {
            points -= penalty
            if (points < 0) points = 0
        }
        binding.pointsTextView.text =
            String.format(getString(R.string.current_points_text), points)
    }

    private fun shuffle() {
/*        var randomTextIndex: Int = generator.nextInt(colors.size)
        var randomColorIndex: Int = generator.nextInt(colorNames.size)
        binding.leftTextView.text = colorNames[randomTextIndex]
        binding.leftTextView.setTextColor(colors[randomColorIndex])
        randomTextIndex = generator.nextInt(colors.size)
        randomColorIndex = generator.nextInt(colorNames.size)
        binding.rightTextView.text = colorNames[randomTextIndex]
        binding.rightTextView.setTextColor(colors[randomColorIndex])*/

        val indices: IntArray = gm.shuffle()

        binding.leftTextView.text = colorNames[indices[0]]
        binding.leftTextView.setTextColor(colors[indices[1]])
        binding.rightTextView.text = colorNames[indices[2]]
        binding.rightTextView.setTextColor(colors[indices[3]])
    }

    private fun setPenalty(item: MenuItem) {
        if (isStared) return
        penalty = PenaltyHandler.getPenalty(item)
        gm.penalty = penalty
        difficulty = item.title.toString()
    }

    private fun updateMenus(item: MenuItem) {
        if (isStared) return
        binding.toolbar.menu.findItem(item.itemId).isChecked = true
        checkedMenuItemId = item.itemId
    }

    private fun startOpeningAnimations() {
        val moveUp = AnimationUtils.loadAnimation(context, R.anim.move_upwards_disappear)
        val moveDown =
            AnimationUtils.loadAnimation(context, R.anim.move_downwards_disappear)
        binding.rulesTextView.startAnimation(moveUp)
        binding.startButton.text = getString(R.string.stop_button_text)
        binding.difficultyTextView.startAnimation(moveDown)
        val reverseMoveDown =
            AnimationUtils.loadAnimation(context, R.anim.reverse_move_downwards_disappear)
        binding.yesButton.startAnimation(reverseMoveDown)
        binding.noButton.startAnimation(reverseMoveDown)
    }

    private fun startEndingAnimations() {
        val reverseMoveUp =
            AnimationUtils.loadAnimation(context, R.anim.reverse_move_upwards_disappear)
        val reverseMoveDown =
            AnimationUtils.loadAnimation(context, R.anim.reverse_move_downwards_disappear)
        binding.rulesTextView.startAnimation(reverseMoveUp)
        binding.startButton.text = getString(R.string.start_button_text)
        binding.difficultyTextView.startAnimation(reverseMoveDown)
        val moveDown =
            AnimationUtils.loadAnimation(context, R.anim.move_downwards_disappear)
        binding.yesButton.startAnimation(moveDown)
        binding.noButton.startAnimation(moveDown)
    }

    fun goToResults(user: User) {
        val action = GameFragmentDirections.goToResultFragment(user.displayName, user.email, gm.points)
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    fun goToLogin() {

        Navigation.findNavController(requireView()).navigate(R.id.goToLoginFragment)
    }

    private fun signOut() {
        if (isStared) {
            renderFinishedUi()
            finishGame()
        }
        mAuth!!.signOut()
        Log.d(TAG, "accountFirebase : signOut")
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener {
                Log.d(TAG, "accountGoogle : signOut")
            }
    }

    private fun getCurrentUser(fAccount: FirebaseUser): User {
        return User(fAccount.displayName, fAccount.email)
    }

    private fun getCurrentUser(gAccount: GoogleSignInAccount): User {
        return User(gAccount.displayName, gAccount.email)
    }




}