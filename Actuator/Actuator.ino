#include <Arduino.h>
#include "nw_info.h"
#include "get_post.h"
#include "activation.h"

void setup() {

  Serial.begin(115200);
  delay(1000);

  // NW init and making connections
  nw_make_connection();

}

void loop() {
  
  if (WiFi.status() == WL_CONNECTED) {
    // Debuging 용도 
    Serial.println(analogRead(A0));
    // POST로 상태 정보 전달
    send_post_request(client);
    // GET으로 모드 정보 읽어옴
    send_get_request(client);
    // Actuator 동작
    activation();
  } 
  else {
    Serial.println("WiFi disconnected. Retrying...");
    reconnect_wifi();
  }

}


