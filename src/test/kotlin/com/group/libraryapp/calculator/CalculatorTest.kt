package com.group.libraryapp.calculator

fun main(){
    val calculatorTest = CalculatorTest()
    calculatorTest.addTest()
    calculatorTest.minusTest()
    calculatorTest.multiplyTest()
    calculatorTest.divideTest()
    calculatorTest.divideExceptionTest()
}
class CalculatorTest {

    fun addTest(){
        //given
        val calculator = Calculator(5)

        //when
        calculator.add(3)

        //then
        if(calculator.number != 8){
            throw IllegalArgumentException()
        }
    }
    fun minusTest(){
        //given
        val calculator = Calculator(5)

        //when
        calculator.minus(3)

        //then
        if(calculator.number != 2){
            throw IllegalArgumentException()
        }
    }
    fun multiplyTest(){
        //given
        val calculator = Calculator(5)

        //when
        calculator.multiply(3)

        //then
        if(calculator.number != 15){
            throw IllegalArgumentException()
        }
    }
    fun divideTest(){
        //given
        val calculator = Calculator(6)

        //when
        calculator.divide(3)

        //then
        if(calculator.number != 2){
            throw IllegalArgumentException()
        }
    }
    fun divideExceptionTest(){
        //given
        val calculator = Calculator(5)

        //when
        try {
            calculator.divide(0)
        }catch (e: IllegalArgumentException){
        if (e.message != "00으로 나눌 수 없습니다"){
            throw IllegalArgumentException("메세지가 다릅니다.")
        }
            //성공
            return
        }catch (e: Exception){
            throw IllegalArgumentException()
        }
        throw IllegalArgumentException("기대하는 결과가 나오지 않았습니다.")

    }
}