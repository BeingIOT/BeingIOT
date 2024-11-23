#include "get_post.h"
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


// 해가 진 이후, heuristic한 값으로 ON OFF 판별(공공장소이므로 암막 설치가 꺼려지고, 연구실 인원들이 주로 수업 마친 후 저녁시간 연구실에 오기때문)
bool return_state() {
  int sensorValue = analogRead(A0);
  if (sensorValue > 340) return true;
  else return false;
}

// HTTP GET Request, Response
void send_get_request(WiFiClient& client) {
  Serial.print("Connecting to ");
  Serial.println(host);

  if (!client.connect(host, port)) {
    Serial.println("Connection failed for GET Request");
    return;
  }

  // GET 요청 작성 및 전송
  client.println("GET " + String(url) + " HTTP/1.1");
  client.println("Host: " + String(host));
  client.println("Connection: close");
  client.println();

  // Response 읽고, Data section의 GotData에 Mode를 저장
  String response = readResponse(client);
  int bodyStartIndex = response.indexOf("\r\n\r\n");
  if (bodyStartIndex != -1) {
    String body = response.substring(bodyStartIndex + 4);
    Serial.println("Body:");
    Serial.println(body);
    GotData = body;
  } 
  else {
    Serial.println("No body found in response");
  }
}

// HTTP POST Request, Response
void send_post_request(WiFiClient& client) {
  String postData = "";
  Serial.print("Connecting to ");
  Serial.println(host);

  if (!client.connect(host, port)) {
    Serial.println("Connection failed for POST request");
    return;
  }

  // Server에 현재(단, 해가 진 저녁시간) 냉난방 시스템의 전원 정보를 POST하기 위한 Data를 set 
  if(return_state()){
    postData = "Air Conditioner Power is on OFF State";
  }
  else{
    postData = "Air Conditioner Power is on ON State";
  }

  // HTTP POST 요청 작성 및 전송
  client.println("POST " + String(url) + " HTTP/1.1");
  client.println("Host: " + String(host));
  client.println("Content-Type: text/plain");
  client.println("Content-Length: " + String(postData.length()));
  client.println();
  client.println(postData);

  // Server Response 읽기
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