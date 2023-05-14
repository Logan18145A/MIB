package org.firstinspires.ftc.teamcode.OffseasonDevelopments;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
@TeleOp(name="IMUAuttocorrectionTEST", group="Iterative Opmode")

public class IMUAUTOCORRECTION extends OpMode {
    //Just making example opmode that we could use with powerplay robot

    ElapsedTime timeRunning = new ElapsedTime();

    double loops;

    //define state machines
    public enum LiftState {
        //empty for now
        LIFT_GRAB,
        FINISH
    }

    //define drivetrain motors
    private DcMotor front_left = null;
    private DcMotor front_right = null;
    private DcMotor back_left = null;
    private DcMotor back_right = null;
    // define autocorrection shit
    double botHeading;

    BNO055IMU imu;
    double globalAngle, power = .3, correction;

    double target = 0;
    double error;
    //I think this is the Kp you use in RR tuning
    double Kp = 0.035;

    // idt we need this but from reading thru liams code it seems kinda cool
    Gamepad currentGamepad1;
    Gamepad previousGamepad1;

    @Override
    public void init() {
        //retrive and define IMU from hardware map
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        //default but making sure
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        //without this data from imu thows exception
        imu.initialize(parameters);
        // initalize DT motors
        front_left = hardwareMap.get(DcMotor.class, "front_left");
        front_right = hardwareMap.get(DcMotor.class, "front_right");
        back_left = hardwareMap.get(DcMotor.class, "back_left");
        back_right = hardwareMap.get(DcMotor.class, "back_right");
    }

    @Override
    public void loop() {
        // Store the gamepad values from the previous loop iteration in
        // previousGamepad1/2 to be used in this loop iteration.
        // This is equivalent to doing this at the end of the previous
        // loop iteration, as it will run in the same order except for
        // the first/last iteration of the loop.
       // previousGamepad1.copy(currentGamepad1);

        // Store the gamepad values from this loop iteration in
        // currentGamepad1/2 to be used for the entirety of this loop iteration.
        // This prevents the gamepad values from changing between being
        // used and stored in previousGamepad1/2.
        //currentGamepad1.copy(gamepad1);
        botHeading = imu.getAngularOrientation().firstAngle;
        if (gamepad1.right_stick_x != 0) {
            target = imu.getAngularOrientation().firstAngle;
        }
        //Define the error and other shit
        //TODO change values to mib drivetrain
        //TODO tune your own Kp value
        double y = gamepad1.left_stick_y; // Remember, this is reversed!
        double x = -gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = -gamepad1.right_stick_x * 0.4;

        //slide_extension.setPower(gamepad2.left_stick_y);



        double denominator = Math.max(Math.abs(y) + Math.abs(x), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        error = -Math.toDegrees(botHeading) + Math.toDegrees(target);
        frontLeftPower += error * Kp;
        backLeftPower += error * Kp;
        frontRightPower += -error * Kp;
        backRightPower += -error * Kp;

        front_left.setPower(-frontLeftPower);
        back_left.setPower(-backLeftPower);
        front_right.setPower(frontRightPower);
        back_right.setPower(backRightPower);
    }
}
