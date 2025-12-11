package com.example.temiboardgame;

import android.util.Log;
// import com.robotemi.sdk.Robot; // Temi SDK import

public class TemiController {

    // 보드 칸(1~12) → Temi 위치 이름 매핑
    public static String getLocationNameForPosition(int position) {
        switch (position) {
            case 1:
                return "home base"; // 시작점은 보통 home base
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

        Log.d("TemiController", "테미 이동 시도: " + locationName);

        /*
         * try {
         * Robot robot = Robot.getInstance();
         * if (robot != null) {
         * // 저장된 위치 목록에 있는지 확인 후 이동 (안전장치)
         * if (robot.getLocations().contains(locationName)) {
         * robot.goTo(locationName);
         * } else {
         * Log.e("TemiController", "저장되지 않은 위치: " + locationName);
         * // 테스트용: 위치가 없으면 그냥 로그만 찍고 넘어감
         * }
         * }
         * } catch (Exception e) {
         * Log.e("TemiController", "Robot SDK Error", e);
         * }
         */
        Log.d("TemiController", "[Emulator Mode] Robot.goTo skipped: " + locationName);
    }
}
