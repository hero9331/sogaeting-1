#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

// 1. Firebase 설정 (본인 프로젝트 정보 입력)
// 1. Firebase 설정 (본인 프로젝트 정보 입력)
#define FIREBASE_HOST "temiboardgame-60750-default-rtdb.firebaseio.com" // https:// 제외
#define FIREBASE_AUTH "jSZOxOqn4B5Ndd1gt3zz1fdym8RySs2pZu2sYJtD" // 비밀번호
// #define FIREBASE_FINGERPRINT "06:E4:89:69:52:DC:4A:88:49:E4:9A:19:66:79:97:BD:4A:0A:7C:3F"

// 2. Wi-Fi 설정 (Temi와 동일한 핫스팟 사용 권장)
#define WIFI_SSID "abcdef"
#define WIFI_PASSWORD "qwerty1234"

// 3. 스위치 핀 설정
#define SWITCH_PIN D8

void setup() {
  Serial.begin(115200);
  
  // 스위치 핀을 INPUT_PULLUP으로 설정 (스위치 안 누르면 HIGH, 누르면 LOW)
  pinMode(SWITCH_PIN, INPUT_PULLUP); 

  // 와이파이 연결
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());

  // Firebase 시작
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  
  // 초기 상태: 정지 상태(0)로 설정
  Firebase.setInt("gameState", 1);
}

void loop() {
  // 스위치가 눌렸는지 확인 (LOW 신호)
  if (digitalRead(SWITCH_PIN) == LOW) {
    // 스위치가 눌리면 Firebase의 gameState를 0(정지)으로 변경
    Firebase.setInt("gameState", 0);
    
    Serial.println("Button Pressed! Game Stop Sent.");
    
    // 디바운싱 (중복 입력 방지) 및 과도한 통신 방지를 위한 딜레이
    delay(500); 
  }
  
  // 오류 처리
  if (Firebase.failed()) {
    Serial.print("setting /gameState failed:");
    Serial.println(Firebase.error());
    return;
  }
}