package org.firstinspires.ftc.teamcode.TeleOP;
import com.acmerobotics.dashboard.config.Config;
public class ArmConstants{
    //Lift Constants
@Config
public static class LiftConstants {
    //When button is clicked lift goes to following
    public static int Start  = 0;

    public static int Ground;

    public static int Low;

    public static int Medium;

    public static int High = -2000;
    }
    @Config
    public static class RotatorConstants {
    public static int right = 0;
    public static int FRCorner = 400;
    public static int front = 760;
    public static int FLCorner = 1130;
    public static int left = 1500;

    }
    @Config
    public static class ClawConstants{
    public static double GripLopen = 1;
    public static double GripLclose = 0.5;
    public static double GripRopen = 0;
    public static double GripRclose = 1;
    }
    public static class ClawGrip{

    }
}
