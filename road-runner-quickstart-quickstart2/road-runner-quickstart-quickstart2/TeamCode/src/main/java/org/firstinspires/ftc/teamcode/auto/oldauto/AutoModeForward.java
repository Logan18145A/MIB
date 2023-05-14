package org.firstinspires.ftc.teamcode.auto.oldauto;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(name = "AutoForward")
public class AutoModeForward extends LinearOpMode {
    //private means setting value to nothing, only when setting to null
    private ElapsedTime runtime = new ElapsedTime();
    public DcMotor leftFront = null;
    public DcMotor leftBack = null;
    public DcMotor rightFront= null;
    public DcMotor rightBack  = null;

   /* public DcMotor lift = null;
    public Servo Claw = null; */



    public final static double speed = 0.6;
    public final static long time = 1800;
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

       /* lift = hardwareMap.get(DcMotor.class, "eleMotor");
        Claw = hardwareMap.get(Servo.class, "claw"); */

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);


        waitForStart();
        //forward(speed,1800);


    }

}
