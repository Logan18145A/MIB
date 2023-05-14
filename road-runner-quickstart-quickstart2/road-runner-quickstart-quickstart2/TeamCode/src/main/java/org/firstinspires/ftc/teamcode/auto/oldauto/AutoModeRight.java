package org.firstinspires.ftc.teamcode.auto.oldauto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "AutoModeRight")
public class AutoModeRight extends LinearOpMode {
    //private means setting value to nothing, only when setting to null
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFront = null;
    private DcMotor leftBack = null;
    private DcMotor rightFront= null;
    private DcMotor rightBack  = null;
    public DcMotor lift = null;
    public Servo Claw = null;


    public final static double speed = 0.6;
    public final static long time = 2000;
    public void left(double power,long time) {
        leftFront.setPower(-power);
        rightFront.setPower(power);
        leftBack.setPower(power);
        rightBack.setPower(-power);
        sleep(time);
    }

    public void right(double power,long time) {
        leftFront.setPower(power);
        rightFront.setPower(-power);
        leftBack.setPower(-power);
        rightBack.setPower(power);
        sleep(time);
    }

    public void stopMove() {
        forward(0,0);
    }

    public void forward(double power,long time) {
        leftFront.setPower(power);
        rightFront.setPower(power);
        leftBack.setPower(power);
        rightBack.setPower(power);
        sleep(time);
    }
    @Override
    public void runOpMode() throws InterruptedException {
        leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        lift = hardwareMap.get(DcMotor.class, "eleMotor");
        Claw = hardwareMap.get(Servo.class, "claw");


        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);


        waitForStart();
        forward(speed,time);
        right(speed,time);
        stopMove();

    }
}