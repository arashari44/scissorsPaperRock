package com.example.scissorspaperrock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.scissorspaperrock.ui.theme.ScissorsPaperRockTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

enum class ChangeDirectionMode{
    VERTICALLY,
    HORIZONTALLY,
    COMPLETELY
}

var deviceWidth = 0.0
var deviceLength = 0.0

open class Thing(private var x: Double, private var y: Double, private var direction: Double, private var speed: Double){
    fun updateDirection(mode: ChangeDirectionMode){
        when (mode){
            ChangeDirectionMode.HORIZONTALLY -> {
                this.direction = 360 - this.direction
            }
            ChangeDirectionMode.VERTICALLY -> {
                if (this.direction <= 180){
                    this.direction = 180 - this.direction
                }
                else{
                    this.direction = 540 - this.direction
                }
            }
            ChangeDirectionMode.COMPLETELY -> {
                if (this.direction <= 180){
                    this.direction = 180 + this.direction
                }
                else{
                    this.direction = this.direction - 180
                }
            }
        }
    }
    fun updateLocation(){
        this.x = this.x + this.speed * cos(Math.toRadians(this.direction))
        this.y = this.y - this.speed * sin(Math.toRadians(this.direction))
        if ((this.x <= 0 && this.y <= 0) || (this.x <= 0 && this.y >= deviceLength) || (this.x >= deviceWidth && this.y <= 0) || (this.x >= deviceWidth && this.y >= deviceLength)){
            this.updateDirection(ChangeDirectionMode.COMPLETELY)
        }
        else if (this.x > deviceWidth || this.x < 0){
            this.updateDirection(ChangeDirectionMode.VERTICALLY)
        }
        else if (this.y > deviceLength || this.y < 0){
            this.updateDirection(ChangeDirectionMode.HORIZONTALLY)
        }
    }
    fun getX(): Double{
        return this.x
    }
    fun getY(): Double{
        return this.y
    }
    fun setX(newX: Double){
        this.x = newX
    }
    fun setY(newY: Double){
        this.y = newY
    }
    fun setSpeed(newSpeed: Double){
        this.speed = newSpeed
    }
}

class Scissors(private var lentgh: Double, private var width: Double, private var degree: Double, private var velocity: Double): Thing(lentgh, width, degree, velocity){

}

class Paper(private var lentgh: Double, private var width: Double, private var degree: Double, private var velocity: Double): Thing(lentgh, width, degree, velocity){

}

class Rock(private var lentgh: Double, private var width: Double, private var degree: Double, private var velocity: Double): Thing(lentgh, width, degree, velocity){

}

class MainActivity : ComponentActivity() {
    companion object {
        val scissors = mutableListOf<Scissors>()
        val rocks = mutableListOf<Rock>()
        val papers = mutableListOf<Paper>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val density: Float = resources.displayMetrics.density
        deviceWidth = (resources.configuration.screenWidthDp / density).toDouble()
        deviceLength = (resources.configuration.screenHeightDp / density).toDouble()
        var x: Double
        var y: Double
        var direction: Double
        var speed: Double
        repeat(5){
            repeat(3) {
                x = Random.nextDouble(0.0, deviceWidth)
                y = Random.nextDouble(0.0, deviceLength)
                direction = Random.nextDouble(0.0, 360.0)
                speed = Random.nextDouble(0.0, 5.0)
                when (it){
                    0 -> scissors.add(Scissors(x, y, direction, speed))
                    1 -> papers.add(Paper(x, y, direction, speed))
                    2 -> rocks.add(Rock(x, y, direction, speed))
                }
            }
        }
        setContent {
            ScissorsPaperRockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScissorsPaperRock()
                }
            }
        }
    }
}

@Composable
fun ScissorsPaperRock(modifier: Modifier = Modifier) {
    val rocks = MainActivity.rocks
    val scissors = MainActivity.scissors
    val papers = MainActivity.papers
    var progress by remember { mutableStateOf(false) }
    val rockImage = painterResource(R.drawable.rock)
    val paperImage = painterResource(R.drawable.paper)
    val scissorsImage = painterResource(R.drawable.scissors)
    for (rock in rocks) {
        Image(
            painter = rockImage,
            contentDescription = null,
            modifier = Modifier
                .requiredHeight(40.dp)
                .requiredWidth(40.dp)
                .absoluteOffset(
                    (2 * rock.getX() - deviceWidth).dp,
                    (2 * rock.getY() - deviceLength).dp
                )
        )
    }
    for (scissor in scissors) {
        Image(
            painter = scissorsImage,
            contentDescription = null,
            modifier = Modifier
                .requiredHeight(40.dp)
                .requiredWidth(40.dp)
                .absoluteOffset(
                    (2 * scissor.getX() - deviceWidth).dp,
                    (2 * scissor.getY() - deviceLength).dp
                )
        )
    }
    for (paper in papers) {
        Image(
            painter = paperImage,
            contentDescription = null,
            modifier = Modifier
                .requiredHeight(40.dp)
                .requiredWidth(40.dp)
                .absoluteOffset(
                    (2 * paper.getX() - deviceWidth).dp,
                    (2 * paper.getY() - deviceLength).dp
                )
        )
    }
    LaunchedEffect(progress) {
        CoroutineScope(Dispatchers.Default).launch {
            delay(10)
            for (rock in rocks){
                rock.updateLocation()
            }
            for (scissor in scissors){
                scissor.updateLocation()
            }
            for (paper in papers){
                paper.updateLocation()
            }
            progress = !progress
            for( i in 0..rocks.size - 1 ){
                for( j in 0..i - 1 ){
                    if (sqrt((rocks[i].getX() - rocks[j].getX()).pow(2) + (rocks[i].getY() - rocks[j].getY()).pow(2)) < 10){
                        if (Math.abs(rocks[i].getX() - rocks[j].getX()) > Math.abs(rocks[i].getY() - rocks[j].getY()) + 3){
                            rocks[i].updateDirection(ChangeDirectionMode.VERTICALLY)
                            rocks[j].updateDirection(ChangeDirectionMode.VERTICALLY)
                        }
                        else if (Math.abs(rocks[i].getX() - rocks[j].getX()) + 3< Math.abs(rocks[i].getY() - rocks[j].getY())){
                            rocks[i].updateDirection(ChangeDirectionMode.HORIZONTALLY)
                            rocks[j].updateDirection(ChangeDirectionMode.HORIZONTALLY)
                        }
                        else{
                            rocks[i].updateDirection(ChangeDirectionMode.COMPLETELY)
                            rocks[j].updateDirection(ChangeDirectionMode.COMPLETELY)
                        }
                    }
                }
            }
            for( i in 0..scissors.size - 1 ){
                for( j in 0..i - 1 ){
                    if (sqrt((scissors[i].getX() - scissors[j].getX()).pow(2) + (scissors[i].getY() - scissors[j].getY()).pow(2)) < 10){
                        if (Math.abs(scissors[i].getX() - scissors[j].getX()) > Math.abs(scissors[i].getY() - scissors[j].getY()) + 3){
                            scissors[i].updateDirection(ChangeDirectionMode.VERTICALLY)
                            scissors[j].updateDirection(ChangeDirectionMode.VERTICALLY)
                        }
                        else if (Math.abs(scissors[i].getX() - scissors[j].getX()) + 3< Math.abs(scissors[i].getY() - scissors[j].getY())){
                            scissors[i].updateDirection(ChangeDirectionMode.HORIZONTALLY)
                            scissors[j].updateDirection(ChangeDirectionMode.HORIZONTALLY)
                        }
                        else{
                            scissors[i].updateDirection(ChangeDirectionMode.COMPLETELY)
                            scissors[j].updateDirection(ChangeDirectionMode.COMPLETELY)
                        }
                    }
                }
            }
            for( i in 0..papers.size - 1 ){
                for( j in 0..i - 1 ){
                    if (sqrt((papers[i].getX() - papers[j].getX()).pow(2) + (papers[i].getY() - papers[j].getY()).pow(2)) < 10){
                        if (Math.abs(papers[i].getX() - papers[j].getX()) > Math.abs(papers[i].getY() - papers[j].getY()) + 3){
                            papers[i].updateDirection(ChangeDirectionMode.VERTICALLY)
                            papers[j].updateDirection(ChangeDirectionMode.VERTICALLY)
                        }
                        else if (Math.abs(papers[i].getX() - papers[j].getX()) + 3 < Math.abs(papers[i].getY() - papers[j].getY())){
                            papers[i].updateDirection(ChangeDirectionMode.HORIZONTALLY)
                            papers[j].updateDirection(ChangeDirectionMode.HORIZONTALLY)
                        }
                        else{
                            papers[i].updateDirection(ChangeDirectionMode.COMPLETELY)
                            papers[j].updateDirection(ChangeDirectionMode.COMPLETELY)
                        }
                    }
                }
            }
            for( i in 0..rocks.size - 1 ){
                for( j in 0..scissors.size - 1 ){
                    if (sqrt((rocks[i].getX() - scissors[j].getX()).pow(2) + (rocks[i].getY() - scissors[j].getY()).pow(2)) < 10){
                        if (Math.abs(rocks[i].getX() - scissors[j].getX()) > Math.abs(rocks[i].getY() - scissors[j].getY()) + 3){
                            rocks[i].updateDirection(ChangeDirectionMode.VERTICALLY)
                        }
                        else if (Math.abs(rocks[i].getX() - scissors[j].getX()) + 3< Math.abs(rocks[i].getY() - scissors[j].getY())){
                            rocks[i].updateDirection(ChangeDirectionMode.HORIZONTALLY)
                        }
                        else{
                            rocks[i].updateDirection(ChangeDirectionMode.COMPLETELY)
                        }
                        scissors[j].setX(10 * deviceWidth)
                        scissors[j].setY(10 * deviceLength)
                        scissors[j].setSpeed(0.0)
                    }
                }
            }
            for( i in 0..papers.size - 1 ){
                for( j in 0..rocks.size - 1 ){
                    if (sqrt((papers[i].getX() - rocks[j].getX()).pow(2) + (papers[i].getY() - rocks[j].getY()).pow(2)) < 10){
                        if (Math.abs(papers[i].getX() - rocks[j].getX()) > Math.abs(papers[i].getY() - rocks[j].getY()) + 3){
                            papers[i].updateDirection(ChangeDirectionMode.VERTICALLY)
                        }
                        else if (Math.abs(papers[i].getX() - rocks[j].getX()) + 3< Math.abs(papers[i].getY() - rocks[j].getY())){
                            papers[i].updateDirection(ChangeDirectionMode.HORIZONTALLY)
                        }
                        else{
                            papers[i].updateDirection(ChangeDirectionMode.COMPLETELY)
                        }
                        rocks[j].setX(10 * deviceWidth)
                        rocks[j].setY(10 * deviceLength)
                        rocks[j].setSpeed(0.0)
                    }
                }
            }
            for( i in 0..scissors.size - 1 ){
                for( j in 0..papers.size - 1 ){
                    if (sqrt((scissors[i].getX() - papers[j].getX()).pow(2) + (scissors[i].getY() - papers[j].getY()).pow(2)) < 10){
                        if (Math.abs(scissors[i].getX() - papers[j].getX()) > Math.abs(scissors[i].getY() - papers[j].getY()) + 3){
                            scissors[i].updateDirection(ChangeDirectionMode.VERTICALLY)
                        }
                        else if (Math.abs(scissors[i].getX() - papers[j].getX()) + 3< Math.abs(scissors[i].getY() - papers[j].getY())){
                            scissors[i].updateDirection(ChangeDirectionMode.HORIZONTALLY)
                        }
                        else{
                            scissors[i].updateDirection(ChangeDirectionMode.COMPLETELY)
                        }
                        papers[j].setX(10 * deviceWidth)
                        papers[j].setY(10 * deviceLength)
                        papers[j].setSpeed(0.0)
                    }
                }
            }
        }
    }
}