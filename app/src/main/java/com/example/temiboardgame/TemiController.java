package com.example.temiboardgame;

import com.robotemi.sdk.Robot;
import java.util.List;

public class TemiController {

    // 로봇 인스턴스
    private static Robot getRobot() {
        return Robot.getInstance();
    }

    /**
     * 해당 위치(숫자)로 로봇 이동 명령
     */
    public static void moveToPosition(int positionNumber) {
        String locationName = String.valueOf(positionNumber);
        Robot robot = getRobot();

        // 위치 유효성 체크 후 이동
        if (isLocationSaved(locationName)) {
            robot.goTo(locationName);
        }
    }

    /**
     * 맵에 해당 위치가 저장되어 있는지 확인
     */
    public static boolean isLocationSaved(String locationName) {
        List<String> locations = getRobot().getLocations();
        for (String loc : locations) {
            if (loc.equals(locationName))
                return true;
        }
        return false;
    }

    public static String getLocationNameForPosition(int position) {
        return String.valueOf(position);
    }
}
