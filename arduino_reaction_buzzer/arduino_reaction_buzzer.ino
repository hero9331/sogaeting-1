#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>

// 1. Firebase 프로젝트 설정 (본인 정보 입력 필수)
#define FIREBASE_HOST "timegame-b85bf-default-rtdb.firebaseio.com" // https:// 제외
#define FIREBASE_AUTH "oRVbiAPfUnCua1CDpgtfod8e4jnV1gCOcrA19s1a"

// 2. Wi-Fi 설정 (Temi와 동일 네트워크 사용 권장)
#define WIFI_SSID "abcdef"
#define WIFI_PASSWORD "qwerty1234"

// 3. 버튼 핀 설정 (D3 핀 사용)
#define BUTTON_PIN D3

// Firebase 객체
FirebaseData firebaseData;
FirebaseAuth auth;
FirebaseConfig config;

void setup() {
  Serial.begin(115200);
  
  // 버튼 핀 설정 (INPUT_PULLUP: 평소 HIGH, 누르면 LOW)
  pinMode(BUTTON_PIN, INPUT_PULLUP);

  // WiFi 연결
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());

  // Firebase 설정
  config.host = FIREBASE_HOST;
  config.signer.tokens.legacy_token = FIREBASE_AUTH;

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
  
  // 초기화: 버튼 눌림 상태 false로 시작
  Firebase.setBool(firebaseData, "/reactionGame/buttonPressed", false);
}

void loop() {
  // 버튼 상태 읽기 (INPUT_PULLUP이므로 누르면 LOW)
  if (digitalRead(BUTTON_PIN) == LOW) {
    Serial.println("Button Pressed!");

    // Firebase에 버튼 눌림 상태 전송 (true)
    // 경로: reactionGame/buttonPressed
    if (Firebase.setBool(firebaseData, "/reactionGame/buttonPressed", true)) {
      Serial.println("Firebase set true success");
    } else {
      Serial.print("Firebase failed: ");
      Serial.println(firebaseData.errorReason());
    }

    // 디바운싱: 버튼에서 손을 뗄 때까지 대기
    delay(50);
    while (digitalRead(BUTTON_PIN) == LOW) {
      delay(10);
    }
    
    Serial.println("Button Released");
    // 버튼을 떼면 false로 돌려놓는 로직은 앱에서 처리할 수도 있고 여기서 할 수도 있지만,
    // 보통 반응속도 게임은 '한 번 누름'이 중요하므로 앱이 처리하고 초기화하는 게 좋음.
    // 여기서는 단순히 '눌렀음' 신호만 보냅니다.
  }

  delay(10); // 루프 안정화
}
