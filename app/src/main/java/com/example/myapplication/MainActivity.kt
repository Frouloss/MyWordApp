package com.example.myapplication


import android.app.AlertDialog
import android.app.Application
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.myapplication.databinding.ActivityLearnLayoutBinding
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.view.ViewGroup
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearnLayoutBinding

    private var correctTranslation: String? = null

    private var flag = false

    private var correctAnswers = 0

    private var max = 5

    private var maxWidth = 0

    private var mistakes = 0

    fun onUserAnswered(correct: Boolean) {
        if (correct) {
            correctAnswers += 1
        }
        binding.tvProgressText.text = correctAnswers.toString()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnLayoutBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.progressContainer.post {
            maxWidth = binding.progressContainer.width
            println("maxWidth = $maxWidth")
        }


        lifecycleScope.launch {
            val randomWord = DatabaseProvider.db.wordDao().getRandomWord()
            loadNextWord()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val buttons = listOf(binding.wbWidgetButton, binding.btnSkip)

            buttons.forEach { button ->
                val params = button.layoutParams as ViewGroup.MarginLayoutParams
                params.bottomMargin = systemBarsInsets.bottom
                button.layoutParams = params
            }

            insets
        }

        val answerClickListener = { layout: LinearLayout, textView: TextView, numberView: TextView ->
            val selected = textView.text.toString()
            if (selected == correctTranslation && flag == false) {
                showResultMessage(true)
                markAnswerCorrect(layout, textView, numberView)
            } else if (flag == false){
                showResultMessage(false)
                markAnswerWrong(layout, textView, numberView)
            }
        }

        binding.layoutAnswer1.setOnClickListener {
            answerClickListener(binding.layoutAnswer1, binding.tvVariantValue1, binding.tvVariantNumber1)
        }
        binding.layoutAnswer2.setOnClickListener {
            answerClickListener(binding.layoutAnswer2, binding.tvVariantValue2, binding.tvVariantNumber2)
        }
        binding.layoutAnswer3.setOnClickListener {
            answerClickListener(binding.layoutAnswer3, binding.tvVariantValue3, binding.tvVariantNumber3)
        }
        binding.layoutAnswer4.setOnClickListener {
            answerClickListener(binding.layoutAnswer4, binding.tvVariantValue4, binding.tvVariantNumber4)
        }


        binding.wbWidgetButton.setOnClickListener {
            onUserAnswered(true)
            updateProgress()
            if (correctAnswers >= max) {
                showFinalDialog()
            }
            loadNextWord()
        }

        binding.btnSkip.setOnClickListener {
            loadNextWord()
        }

        binding.ibClose.setOnClickListener {
            closeActivity()
        }

    }

    private fun closeActivity(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun updateProgress() {
        if (maxWidth > 0) {
            val newWidth = (maxWidth * correctAnswers / max).toInt()
            val params = binding.progressFill.layoutParams
            params.width = newWidth
            binding.progressFill.layoutParams = params
        }
        binding.tvProgressText.text = correctAnswers.toString()
    }

    private fun loadNextWord() {
        lifecycleScope.launch {
            val words = DatabaseProvider.db.wordDao().getRandomWords(4)

            if (words.size < 4) {
                binding.tvQuestionWord.text = "Не хватает слов в базе"
                return@launch
            }

            val correctWord = words[0]
            val otherWords = words.subList(1, words.size)

            val allOptions = mutableListOf<String>()
            allOptions.add(correctWord.translation)
            otherWords.forEach { allOptions.add(it.translation) }

            allOptions.shuffle()

            flag = false

            binding.tvQuestionWord.text = correctWord.word

            binding.tvVariantValue1.text = allOptions[0]
            binding.tvVariantValue2.text = allOptions[1]
            binding.tvVariantValue3.text = allOptions[2]
            binding.tvVariantValue4.text = allOptions[3]

            correctTranslation = correctWord.translation

            markAnswerNeutral(binding.layoutAnswer1, binding.tvVariantValue1, binding.tvVariantNumber1)
            markAnswerNeutral(binding.layoutAnswer2, binding.tvVariantValue2, binding.tvVariantNumber2)
            markAnswerNeutral(binding.layoutAnswer3, binding.tvVariantValue3, binding.tvVariantNumber3)
            markAnswerNeutral(binding.layoutAnswer4, binding.tvVariantValue4, binding.tvVariantNumber4)
        }
    }

    private fun markAnswerCorrect(
        layoutAnswer: LinearLayout,
        tvVariantValue: TextView,
        tvVariantNumber: TextView
    ) {

        layoutAnswer.background = ContextCompat.getDrawable(
            this@MainActivity,
            R.drawable.shape_rounded_containers_correct
        )

        tvVariantNumber.setTextColor(
            ContextCompat.getColor(
                this@MainActivity,
                R.color.white
            )
        )

        tvVariantNumber.background = ContextCompat.getDrawable(
            this@MainActivity,
            R.drawable.shape_rounded_variants_correct
        )

        tvVariantValue.setTextColor(
            ContextCompat.getColor(
                this@MainActivity,
                R.color.CorrectAnswerColor
            )
        )
    }

    private fun markAnswerWrong(
        layoutAnswer: LinearLayout,
        tvVariantValue: TextView,
        tvVariantNumber: TextView
    ) {

        layoutAnswer.background = ContextCompat.getDrawable(
            this@MainActivity,
            R.drawable.shape_rounded_cointainers_wrong
        )

        tvVariantNumber.setTextColor(
            ContextCompat.getColor(
                this@MainActivity,
                R.color.white
            )
        )

        tvVariantNumber.background = ContextCompat.getDrawable(
            this@MainActivity,
            R.drawable.shape_rounded_variants_wrong
        )

        tvVariantValue.setTextColor(
            ContextCompat.getColor(
                this@MainActivity,
                R.color.WrongAnswerColor
            )

        )

    }

    private fun markAnswerNeutral(
        layoutAnswer: LinearLayout,
        tvVariantValue: TextView,
        tvVariantNumber: TextView
    ) {
        layoutAnswer.background = ContextCompat.getDrawable(
            this@MainActivity,
            R.drawable.shape_rounded_containers
        )

        tvVariantValue.setTextColor(
            ContextCompat.getColor(
                this@MainActivity,
                R.color.TextVariantsColor
            )
        )

        tvVariantNumber.apply{
            background = ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.shape_rounded_variants
            )
            setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.TextVariantsColor
                )
            )
        }

        binding.btnSkip.isVisible = true
        binding.wdAnswerwidget.isVisible = false
    }

    private fun showResultMessage(isCorrect: Boolean){
        val color: Int
        val messageText: String
        val resultIconResource: Int

        if(isCorrect){
            color = ContextCompat.getColor(this, R.color.CorrectAnswerColor)
            messageText = "Correct!"
            resultIconResource = R.drawable.ic_correct
        }else{
            mistakes += 1
            color = ContextCompat.getColor(this, R.color.WrongAnswerColor)
            messageText = "Wrong!"
            resultIconResource = R.drawable.ic_wrong
        }

        flag = true

        with(binding){
            btnSkip.isVisible = false
            wdAnswerwidget.isVisible = true
            wdAnswerwidget.setBackgroundColor(color)
            tvResultText.text = messageText
            wbWidgetButton.setTextColor(color)
            aiAnswerIcon.setImageResource(resultIconResource)
        }
    }

    private fun showFinalDialog(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_final, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val mistCounter = dialogView.findViewById<TextView>(R.id.mistCounter)
        mistCounter.text = mistakes.toString()

        dialogView.findViewById<Button>(R.id.btnRestart).setOnClickListener {
            correctAnswers = 0
            val params = binding.progressFill.layoutParams as FrameLayout.LayoutParams
            params.width = 0
            binding.progressFill.layoutParams = params
            binding.tvProgressText.text = "0"
            correctAnswers = 0
            dialog.dismiss()
            Log.d("Dialog", "Restart clicked")

            loadNextWord()
        }

        dialogView.findViewById<Button>(R.id.btnMenu).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
            Log.d("Dialog", "Menu clicked")
            dialog.dismiss()
        }

        dialog.show()
    }


}