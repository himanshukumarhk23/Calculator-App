package com.example.mycalculatorapp

import android.health.connect.datatypes.units.Percentage
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mycalculatorapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var inputValue1: Double? =0.0
    private var inputValue2: Double? =null
    private var currentoperator: Operator? =null
    private var result: Double? =null
    private var equation: StringBuilder = StringBuilder().append(ZERO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        setNightModeIndicator()
    }


    private fun onZeroClicked() {
        if (equation.startsWith(ZERO)) return
        OnNumberClick(ZERO)
    }




    private fun onDoubleZeroClicked() {
        if(equation.startsWith(ZERO)) return
        OnNumberClick(DOUBLE_ZERO)
    }


    private fun setListeners(){
        for(button in getNumericButton()){
            button.setOnClickListener{
                OnNumberClick(button.text.toString())
            }
            with(binding){
                button0.setOnClickListener{ onZeroClicked() }
                button00.setOnClickListener{ onDoubleZeroClicked() }
                buttonDecimalPoint.setOnClickListener{ onDecimalPointClicked() }
                buttonAddition.setOnClickListener{ onOperatorClicked(Operator.ADDITION) }
                buttonSubtraction.setOnClickListener{ onOperatorClicked(Operator.SUBTRACTION) }
                buttonMultiplication.setOnClickListener{ onOperatorClicked(Operator.MULTIPLICATION) }
                buttonDivide.setOnClickListener{ onOperatorClicked(Operator.DIVISION) }
                buttonEquals.setOnClickListener{ onEqualClicked() }
                AllClear.setOnClickListener{ onAllClearClicked() }
                buttonPlusminus.setOnClickListener{ onPlusMinusClicked() }
                buttonPercent.setOnClickListener { onPercentageClicked() }
                imageNightMode.setOnClickListener{toogleNightMode()}
            }
        }
    }

    private fun toogleNightMode(){
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        recreate()
    }


    private fun setNightModeIndicator(){
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            binding.imageNightMode.setImageResource(R.drawable.themes)
        }else{
            binding.imageNightMode.setImageResource(R.drawable.dark)
        }
    }


    private fun onPercentageClicked(){
        if(inputValue2 == null){
            val percentage = getInputValue1() / 100
            inputValue1 = percentage
            equation.clear().append(percentage)
            updateResultOnDisplay()
        }else{
            val percentageofValue1 = (getInputValue1() * getInputValue2()) / 100
            val percentageofValue2 = getInputValue2() / 100
            result = when(requireNotNull(currentoperator)){
                Operator.ADDITION -> getInputValue1() + percentageofValue2
                Operator.SUBTRACTION -> getInputValue1() - percentageofValue2
                Operator.MULTIPLICATION -> getInputValue1() * percentageofValue2
                Operator.DIVISION -> getInputValue1() / percentageofValue2
            }
            equation.clear().append(ZERO)
            updateResultOnDisplay(isPercentage = true)
            inputValue1 = result
            result = null
            inputValue2 = null
            currentoperator = null
        }
    }

    private fun onPlusMinusClicked(){
        if(equation.startsWith(MINUS)){
            equation.deleteCharAt(0)
        }else{
            equation.insert(0, MINUS)
        }
        setInput()
        updateInputOnDisplay()
    }

    private fun onAllClearClicked(){
        inputValue1 = 0.0
        inputValue2 = null
        currentoperator = null
        result = null
        equation.clear().append(ZERO)
        clearDisplay()
    }

    private fun onOperatorClicked(operator: Operator){
        onEqualClicked()
        currentoperator = operator
    }

    private fun onEqualClicked(){
        if(inputValue2 != null){
            result = calculate()
            equation.clear().append(ZERO)
            updateResultOnDisplay()
            inputValue1 = result
            result = null
            currentoperator = null
            inputValue2 = null
        }else{
            equation.clear().append(ZERO)
        }
    }


    private fun calculate():Double{
        return when(requireNotNull(currentoperator)){
            Operator.ADDITION -> getInputValue1() + getInputValue2()
            Operator.SUBTRACTION -> getInputValue1() - getInputValue2()
            Operator.MULTIPLICATION -> getInputValue1() * getInputValue2()
            Operator.DIVISION -> getInputValue1() / getInputValue2()
        }
    }


    private fun onDecimalPointClicked() {
        if(equation.contains(DECIMAL_POINT)) return
        equation.append(DECIMAL_POINT)
        setInput()
        updateInputOnDisplay()
    }


    private fun getNumericButton() = with(binding){
        arrayOf(button0,button1,button2,button3,button4,button5,button6,button7,button8,button9)
    }


    private fun OnNumberClick(numberText:String){
        if(equation.startsWith(ZERO)){
            equation.deleteCharAt(0)
        }else if(equation.startsWith("$MINUS$ZERO")){
            equation.deleteCharAt(1)
        }
        equation.append(numberText)
        setInput()
        updateInputOnDisplay()
    }


    private fun setInput() {
        if (currentoperator == null) {
            inputValue1 = equation.toString().toDouble()
        }else{
            inputValue2 = equation.toString().toDouble()
        }
    }

    private fun clearDisplay(){
        with(binding){
            textinput.text = getFormattedDisplayValue(value = getInputValue1())
            textEquation.text = null
        }
    }

    private fun updateResultOnDisplay(isPercentage: Boolean = false) {
        binding.textinput.text = getFormattedDisplayValue(result) ?: ""

        var input2Text = getFormattedDisplayValue(getInputValue2()) ?: ""
        if (isPercentage) input2Text = "$input2Text${getString(R.string.percent)}"

        binding.textEquation.text = String.format(
            "%s %s %s",
            getFormattedDisplayValue(getInputValue1()) ?: "",
            getOperatorSymbol(),
            input2Text
        )
    }





    private fun updateInputOnDisplay(){
        if(result ==null){
            binding.textEquation.text = null
        }
        binding.textinput.text = equation
    }



    private fun getInputValue1() = inputValue1 ?:0.0
    private fun getInputValue2() = inputValue2 ?:0.0

    private fun getOperatorSymbol():String {
        return when(requireNotNull(currentoperator)){
            Operator.ADDITION -> getString(R.string.addition)
            Operator.SUBTRACTION -> getString(R.string.subtraction)
            Operator.MULTIPLICATION -> getString(R.string.multiplication)
            Operator.DIVISION -> getString(R.string.division)
        }
    }


    private fun getFormattedDisplayValue(value: Double?):String? {
        val originalValue = value ?: return null
        return if (originalValue % 1 == 0.0) {
            originalValue.toInt().toString()
        } else {
            originalValue.toString()
        }
    }


    enum class Operator{
        ADDITION,SUBTRACTION,MULTIPLICATION,DIVISION
    }

    private companion object{
        const val DECIMAL_POINT = "."
        const val ZERO = "0"
        const val DOUBLE_ZERO = "00"
        const val MINUS = "-"
        const val EMPTY_STRING = ""

    }
}