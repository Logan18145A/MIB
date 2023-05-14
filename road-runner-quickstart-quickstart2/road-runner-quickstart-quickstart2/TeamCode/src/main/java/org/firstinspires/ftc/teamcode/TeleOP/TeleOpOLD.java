package org.firstinspires.ftc.teamcode.TeleOP;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "ACHS TeleOp POV")
public class TeleOpOLD extends LinearOpMode {

    //DriveTrain
    private ElapsedTime runtime = new ElapsedTime();
    public DcMotor leftFront = null;
    public DcMotor leftBack = null;
    public DcMotor rightFront = null;
    public DcMotor rightBack = null;

    //Lift
    public DcMotor lift = null;
    public Servo Claw = null;
    private double clawOffset = 0;
    private static final double MID_SERVO = .5;
    private static final double CLAW_SPEED = 0.01;                 // sets rate to move servo
    private static final double ARM_UP_POWER = 0.45;
    private static final double ARM_DOWN_POWER = -0.45;

    @Override
    public void runOpMode() throws InterruptedException {
        //Drive
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        //Lift
//        lift = hardwareMap.get(DcMotor.class, "eleMotor");
//        Claw = hardwareMap.get(Servo.class, "claw");

        //Set wheels in right direction
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses PLAY)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // run until the end of the match (driver presses STOP)
            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral = gamepad1.left_stick_x;
            double yaw = gamepad1.right_stick_x;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double leftFrontPower = axial + lateral + yaw;
            double rightFrontPower = axial - lateral - yaw;
            double leftBackPower = axial - lateral + yaw;
            double rightBackPower = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));

            if (max > 1.0) {
                leftFrontPower /= max;
                rightFrontPower /= max;
                leftBackPower /= max;
                rightBackPower /= max;
            }

            // This is test code:
            //
            // Uncomment the following code to test your motor directions.
            // Each button should make the corresponding motor run FORWARD.
            //   1) First get all the motors to take to correct positions on the robot
            //      by adjusting your Robot Configuration if necessary.
            //   2) Then make sure they run in the correct direction by modifying the
            //      the setDirection() calls above.
            // Once the correct motors move in the correct direction re-comment this code.

                /*
                leftFrontPower  = gamepad1.x ? 1.0 : 0.0;  // X gamepad
                leftBackPower   = gamepad1.a ? 1.0 : 0.0;  // A gamepad
                rightFrontPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
                rightBackPower  = gamepad1.b ? 1.0 : 0.0;  // B gamepad
                */

            // Send calculated power to wheels
            leftFront.setPower(leftFrontPower);
            rightFront.setPower(rightFrontPower);
            leftBack.setPower(leftBackPower);
            rightBack.setPower(rightBackPower);

            ///Elelift

                if (gamepad2.a) {
                    lift.setPower(.7);
                } else if (gamepad2.b) {
                    lift.setPower(-.7);
                } else {
                    lift.setPower(0);
                }
            // Use gamepad left & right Bumpers to open and close the claw
            if (gamepad2.right_bumper) {
                clawOffset += CLAW_SPEED;
            } else if (gamepad2.left_bumper) {
                clawOffset -= CLAW_SPEED;
            } else {
                //do nothing
            }

            if (gamepad2.a) {
            telemetry.addData("Lift Encoder Value:", lift.getCurrentPosition());
            telemetry.update();
        }

            // Move both servos to new position.  Assume servos are mirror image of each other.
            clawOffset = Range.clip(clawOffset, -.5, 5);
            Claw.setPosition(MID_SERVO - clawOffset);


            // Show the elapsed game time and wheel power.
            /*telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("claw",  "Offset = %.2f", clawOffset);

            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            telemetry.update();*/
           /* public void loop(int) {
                if (gamepad2.a) {

                }
                telemetry.addData("Lift Encoder Value:", lift.getCurrentPosition());
                telemetry.update();
            } */
        }
    }
}