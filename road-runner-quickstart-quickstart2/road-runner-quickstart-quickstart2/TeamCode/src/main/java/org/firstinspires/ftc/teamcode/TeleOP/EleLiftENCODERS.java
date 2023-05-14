package org.firstinspires.ftc.teamcode.TeleOP;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class EleLiftENCODERS extends LinearOpMode  {
    public static final double Motor_Ticks = 1120;

    public static final double toHeight = 0;

    public static final double tick_Per_Inch = 0;

    private DcMotor motor;
    private static final double speed = 1; //speed of servo  (0 being nothing and 1 being the max)
    static final double     COUNTS_PER_MOTOR_REV    = 28  ;    // HD Hex Motor
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.

    static final double     Spool_Circumfrance    = 1.5 ;     // No External Gearing.

    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION)/Spool_Circumfrance;

    //claw variables

    public Servo leftClaw = null;
    private double clawOffset = 0;
    private static final double MID_SERVO   =  .5 ;
    private static final double CLAW_SPEED  = 0.01 ;                 // sets rate to move servo
    private static final double ARM_UP_POWER =  0.45 ;
    private static final double ARM_DOWN_POWER = -0.45 ;
    public EleLiftENCODERS (DcMotor motor, Servo leftClaw){
        this.motor = motor;
    }

    public void moveMotor(int distance){

        if (opModeIsActive()) {
            int newTarget = motor.getCurrentPosition() + (int) (distance * COUNTS_PER_INCH);
            motor.setTargetPosition(newTarget);

            //Move the gears
            motor.setPower(Math.abs(speed));

        }
        // Stop all motion;
        motor.setPower(0);

        // Turn off RUN_TO_POSITION
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

    @Override
    public void runOpMode() throws InterruptedException{
        int targetTick = (int) (tick_Per_Inch * toHeight);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //move claw to position
        /*if (gamepad2.a){
            motor.setPower(.6);
        }else if (gamepad2.b){
            motor.setPower(-.6);
        }else{
            motor.setPower(0);
        }*/
        //Encoder based movement
        if (gamepad2.a){
            motor.setTargetPosition(targetTick);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setPower(-.6);
        } else if (gamepad2.b) {
        }
        // Use gamepad left & right Bumpers to open and close the claw
        if (gamepad2.right_bumper) {
            clawOffset += CLAW_SPEED;
        }else if (gamepad2.left_bumper){
            clawOffset -= CLAW_SPEED;
        }else{
            //do nothing
        }

        // Move both servos to new position.  Assume servos are mirror image of each other.
        clawOffset = Range.clip(clawOffset, -.5, 5);
        leftClaw.setPosition(MID_SERVO - clawOffset);

        telemetry.addData("claw",  "Offset = %.2f", clawOffset);
        telemetry.update();
    }

    public void encoder(int turnage) {
    }
}

