package com.example.temiboardgame;

import android.util.Log;
// import com.robotemi.sdk.Robot; // Temi SDK import

public class TemiController {

    // 보드 칸(1~12) → Temi 위치 이름 매핑
    public static String getLocationNameForPosition(int position) {
        switch (position) {
            case 1:
                return "start";
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case 8:
                return "8";
            case 9:
                return "9";
            case 10:
                return "10";
            case 11:
                return "11";
            case 12:
                return "12";

            default:
                return null;
        }
    }

    public static void moveToPosition(int position) {
        String locationName = getLocationNameForPosition(position);
        if (locationName == null) {
            Log.w("TemiController", "알 수 없는 칸: " + position);
            return;
        }

        Log.d("TemiController", "테미 이동: " + locationName);

        // Robot robot = Robot.getInstance();
        // if (robot != null) {
        // robot.goTo(locationName); // Temi에 저장한 위치 이름과 정확히 같아야 함
        // }
        Log.d("TemiController", "[Emulator Mode] Robot.goTo skipped: " + locationName);
    }
}
