#include "post.h"
#include "nw_info.h"

// Response시간에 의해 다음 요청이 버려지는 것을 막기 위해 지연시간을 추가
String readResponse(WiFiClient& client) {
  String response = "";
  unsigned long timeout = millis();
  while (client.connected() && (millis() - timeout < 5500)) { // 5.5초 타임아웃
    if (client.available()) {
      char c = client.read();
      response += c;
      timeout = millis(); // 데이터 수신 시 타임아웃 갱신
    }
  }
  return response;
}

// HTTP POST Request, Response
void send_post_request(WiFiClient& client, float h, float hic) {
  String postData = "";
  Serial.print("Connecting to ");
  Serial.println(host);

  if (!client.connect(host, port)) {
    Serial.println("Connection failed for POST request");
    return;
  }

  // POST 데이터 준비
  postData = "Humidity : " + String(h) + " Temperature : " + String(hic);

  // HTTP POST 요청 작성
  client.println("POST " + String(url) + " HTTP/1.1");
  client.println("Host: " + String(host));
  client.println("Content-Type: text/plain");
  client.println("Content-Length: " + String(postData.length()));
  client.println(); // 헤더와 바디 사이의 빈 줄
  client.println(postData); // POST 데이터 전송

  // 서버 응답 읽기
  String response = readResponse(client);
  Serial.println("POST response:");
  Serial.println(response);
 
}

// Wi-Fi 재연결
void reconnect_wifi() {
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nReconnected to WiFi!");
}