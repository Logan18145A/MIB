package org.firstinspires.ftc.teamcode.Subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Drivetrain extends SubsystemBase{
/**
 * Creates a new ExampleSubsystem
 */
public DcMotor front_left = null;
    public DcMotor front_right = null;
    public DcMotor back_left = null;
    public DcMotor back_right = null;

// Robot Constants
    //TODO figure out what hdcounts is and retune accordingly
    int HD_COUNTS_PER_REV = 28;
    double DRIVE_GEAR_REDUCTION = 19.2;

    double WHEEL_CIRCUMFERENCE_MM = 96 * Math.PI;
    double DRIVE_COUNTS_PER_MM = (HD_COUNTS_PER_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_MM;

    double DRIVE_COUNTS_PER_IN = DRIVE_COUNTS_PER_MM * 25.4;

    public Drivetrain(HardwareMap hardwareMap) {
        front_left = hardwareMap.get(DcMotor.class, "front_left");
        front_right = hardwareMap.get(DcMotor.class, "front_right");
        back_left = hardwareMap.get(DcMotor.class, "back_left");
        back_right = hardwareMap.get(DcMotor.class, "back_right");

        // Drive motors
        front_left.setDirection(DcMotor.Direction.REVERSE);
        back_left.setDirection(DcMotor.Direction.REVERSE);
        front_right.setDirection(DcMotor.Direction.FORWARD);
        back_right.setDirection(DcMotor.Direction.FORWARD);

        /**
         * Set drive motor powers based on Mecanum-drive-style inputs.
         *
         * @param drive the amount of forward motion
         * @param strafe the amount of sideways motion
         * @param twist the amount of rotational motion
         * @param slowMode the amount of slowing to apply
         */
    }
        public void setMecanumPower(double drive, double strafe, double twist, double slowMode)
        {
            front_left   .setPower((drive + strafe + twist) * slowMode);
            front_right  .setPower((drive - strafe - twist) * slowMode);
            back_left    .setPower((drive - strafe + twist) * slowMode);
            back_left   .setPower((drive + strafe - twist) * slowMode);
        }
        //Driving inches into counts
        public int inchesToCounts(double inches) {
            return (int) (inches * DRIVE_COUNTS_PER_IN);
        }
            @Override
        public void periodic() {
            // This method will be called once per scheduler run
        }
    }


