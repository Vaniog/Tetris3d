    package com.example.tetris3d

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import com.example.customopengl.Space
import com.example.tetris3d.CustomOpenGL.ScoreBoard
import kotlin.math.*


class FieldSpace(val field: Field, val context: Context, val scoreBoard: ScoreBoard) : Space() {
    private fun cube(){
        square(-1, 1, 1, 1, 1, 1, 1, 1, -1, -1, 1, -1)
        square(1, 1, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1)

        square(-1, -1, 1, -1, -1, -1, 1, -1, -1, 1, -1, 1)

        square(-1, 1, -1, 1, 1, -1, 1, -1, -1, -1, -1, -1)
        square(-1, 1, 1, -1, 1, -1, -1, -1, -1, -1, -1, 1)
        square(1, 1, -1, 1, 1, 1, 1, -1, 1, 1, -1, -1)
    }


    var fieldRotationY = PI / 4.0 + 0.001
    var fieldRotationX = 0.02
    private var lastUpdateTime = time

    override fun onFrame() {    

        stopped = true
        if (!field.gameEnded) {
            if (time - lastUpdateTime > 1.0 / scoreBoard.speed) {
                val action = field.doStep(false)
                if (action == 0)
                    field.clearField()
                if (action == 2)
                    scoreBoard.scored()
                lastUpdateTime = time
            }
        }
        else{
            if (time - lastUpdateTime > 0.2) {
                if (field.doStep(false) == 0) {
                    onLose()
                }
                lastUpdateTime = time
            }
        }


        translate(0.0, 0.0, -8.0)
        rotateZ(PI)
        rotateX(-PI/4.0)
        rotateX(fieldRotationX)
        rotateY(fieldRotationY)
        zoom(0.15, 0.15,  0.15)
        translate(-field.width.toDouble() + 1, 17.0, -field.width.toDouble() + 1)
        color3d(29, 51, 74)
        lineColor3d(58, 102, 148)
        for (i in (0..3)){
            square(field.width * 2 - 1, -field.height * 2 + 1, -1,
                field.width * 2 - 1, -(field.height - field.playHeight - 1) * 2 + 1, -1,
                -1, -(field.height - field.playHeight - 1) * 2 + 1, -1,
                -1, -field.height * 2 + 1, -1)
            translate(field.width * 2.0 - 2, 0.0, 0.0)
            rotateY(PI/2.0)
        }

        color3d(40, 68, 100)
        for (i in (0..3)){
            square(field.width * 2 - 1, -(field.height - field.playHeight - 1) * 2 + 1, -1,
                field.width * 2 - 1, 1, -1,
                -1, 1, -1, -1,
                -(field.height - field.playHeight - 1) * 2 + 1, -1)
            translate(field.width * 2.0 - 2, 0.0, 0.0)
            rotateY(PI/2.0)
        }

        square(-1, -field.height * 2 + 1, -1,
            - 1, -field.height * 2 + 1, field.width * 2 - 1,
        field.width * 2 - 1, -field.height * 2 + 1, field.width * 2 - 1,
        field.width * 2 - 1, -field.height * 2 + 1, -1,
        )

        lineColor3d(190, 190, 190)
        for (x in field.field.indices)
            for (y in field.field[0].indices)
                for (z in field.field[0][0].indices){
                    val color1 = field.field[x][y][z]
                    if (color1 == Color.TRANSPARENT)
                        continue
                    pushMatrix()
                    translate(x.toDouble() * 2, -y.toDouble() * 2, z.toDouble() * 2)
                    color3d(color1)
                    cube()
                    popMatrix()
                }

        val shape1 = field.curFigure.shape
        val coords1 = field.curFigure.coordinates
        val center = field.curFigure.center
        field.curFigure.updateTime(deltaTime)

        pushMatrix()
        translate(-field.curFigure.translations.x * 2.0, -field.curFigure.translations.y * 2.0, -field.curFigure.translations.z * 2.0)
        translate(center.x.toDouble() + coords1.x * 2.0 - 1,
            -center.y.toDouble() - coords1.y * 2.0 + 1,
            center.z.toDouble() + coords1.z * 2.0 - 1)
        rotateX(-field.curFigure.rotations.x)
        rotateY(-field.curFigure.rotations.y)
        rotateZ(-field.curFigure.rotations.z)
        translate(-center.x.toDouble() - coords1.x * 2.0 + 1,
            center.y.toDouble() + coords1.y * 2.0 - 1,
            -center.z.toDouble() - coords1.z * 2.0 + 1)

        val color1 = field.curFigure.color;
        color3d(color1)
        for (x in shape1.indices)
            for (y in shape1[0].indices)
                for (z in shape1[0][0].indices)
                {
                    if (shape1[x][y][z] == Color.TRANSPARENT)
                        continue
                    pushMatrix()
                    translate(x.toDouble() * 2.0 + coords1.x * 2.0, -y.toDouble() * 2.0 - coords1.y * 2.0, z.toDouble() * 2.0 + coords1.z * 2.0)
                    cube()
                    popMatrix()
                }
        popMatrix()

        color3d(Color.TRANSPARENT)
        translate(0.0, -field.curFigure.translations.y * 2.0, -field.curFigure.translations.z * 2.0)
        lineColor3d(Color.rgb(160, 160, 170))

        for (x in shape1.indices)
            for (y in shape1[0].indices)
                for (z in shape1[0][0].indices)
                {
                    if (shape1[x][y][z] == Color.TRANSPARENT)
                        continue

                    pushMatrix()
                    translate(-0.99, -y.toDouble() * 2.0 - coords1.y * 2.0, z.toDouble() * 2.0 + coords1.z * 2.0)
                    square(0, 1, 1, 0, -1, 1, 0, -1, -1, 0, 1, -1)
                    translate(field.width * 2.0 - 0.01, 0.0, 0.0)
                    square(0, -1, -1, 0, -1, 1, 0, 1, 1, 0, 1, -1)
                    popMatrix()
                }

        translate(-field.curFigure.translations.x * 2.0, 0.0, field.curFigure.translations.z * 2.0)

        for (x in shape1.indices)
            for (y in shape1[0].indices)
                for (z in shape1[0][0].indices)
                {
                    if (shape1[x][y][z] == Color.TRANSPARENT)
                        continue
                    pushMatrix()
                    translate(x.toDouble() * 2.0 + coords1.x * 2.0, -y.toDouble() * 2.0 - coords1.y * 2.0, -0.99)
                    square(-1, -1, 0, 1, -1, 0, 1, 1, 0, -1, 1, 0)
                    translate(0.0, 0.0, field.width * 2.0 - 0.01)
                    square(1, 1, 0, 1, -1, 0, -1, -1, 0, -1, 1, 0)
                    popMatrix()
                }


        stopped = false
    }

    var joystickTouched = false
    private var mouseStartX = 0.0f
    private var mouseStartY = 0.0f
    private var lastMouseX = 0.0f
    private var lastMouseY = 0.0f
    private var mouseX = 0f
    private var mouseY = 0f
    val ROTATING_MODE = 0
    val LOOK_MODE = 1
    private var lastTouchTime = 0.0
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


    var touchMode = ROTATING_MODE

    fun onLose(){
        val sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val lastHigh = sharedPreferences.getInt("HIGH_SCORE", 0)
        if (lastHigh < scoreBoard.score)
            sharedPreferences.edit().putInt("HIGH_SCORE", scoreBoard.score).apply()
        field.clearField()
        scoreBoard.clear()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTouchEvent(event: MotionEvent?, width : Int, height : Int) : Boolean {
        if (event == null)
            return true
        if (event.action == MotionEvent.ACTION_MOVE) {
            mouseX = event.x
            mouseY = event.y
            if (touchMode == LOOK_MODE){
                fieldRotationY += -(mouseX - lastMouseX) / width.toDouble() * PI * 2.0
                fieldRotationX += -(mouseY - lastMouseY) / height.toDouble() * PI * 1.4
                fieldRotationX = min(PI / 4.7, fieldRotationX)
                fieldRotationX = max(-PI / 4, fieldRotationX)
            }

            lastMouseX = mouseX
            lastMouseY = mouseY
            return true
        }

        if (event.action == MotionEvent.ACTION_DOWN){
            /*if (time - lastTouchTime <= 0.2 && mouseStartY >= height / 2f && !joystickTouched && (abs(mouseStartX - event.x) < height / 30.0) && abs(mouseStartY - event.y) < height / 30.0) {
                touchMode = LOOK_MODE
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            }*/
            mouseStartX = event.x
            mouseStartY = event.y
            lastMouseX = mouseStartX
            lastMouseY = mouseStartY
            return true
        }

        if (event.action == MotionEvent.ACTION_UP){
            stopped = true
            if (touchMode == ROTATING_MODE &&
                abs(event.y - mouseStartY) > abs(event.x - mouseStartX)
                && abs(event.y - mouseStartY) > height / 20.0)
            {
                if (mouseStartX >= width / 2) {
                    val s = sign(cos(fieldRotationY)).toInt()
                    if (tan(fieldRotationY) >= 0) {
                        if (event.y - mouseStartY > 0)
                            field.rotateFigure('z', 1 * s, 1)
                        else if (event.y - mouseStartY < 0)
                            field.rotateFigure('z', -1 * s, 1)
                    } else {
                        if (event.y - mouseStartY > 0)
                            field.rotateFigure('x', 1 * s, -1)
                        else if (event.y - mouseStartY < 0)
                            field.rotateFigure('x', -1 * s, -1)
                    }
                } else {
                    val s = sign(cos(fieldRotationY)).toInt()
                    if (tan(fieldRotationY) >= 0) {
                        if (event.y - mouseStartY > 0)
                            field.rotateFigure('x', 1 * s, -1)
                        else if (event.y - mouseStartY < 0)
                            field.rotateFigure('x', -1 * s, -1)
                    } else {
                        if (event.y - mouseStartY > 0)
                            field.rotateFigure('z', -1 * s, 1)
                        else if (event.y - mouseStartY < 0)
                            field.rotateFigure('z', 1 * s,  1)
                    }
                }
            }
            else if (touchMode == ROTATING_MODE && abs(event.x - mouseStartX) > height / 20.0){
                val  s = sign(event.x - mouseStartX).toInt()
                field.rotateFigure('y', -1 * s, 1)
            }
            stopped = false
        }

        if (touchMode == ROTATING_MODE)
            lastTouchTime = time
        else
            lastTouchTime = 0.0
        joystickTouched = false
        return true
    }
}