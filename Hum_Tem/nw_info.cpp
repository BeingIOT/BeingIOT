#include "nw_info.h"

const char* ssid = "415호 와이파이";
const char* password = "mmlab415";

const char* host = "59.13.210.250";
const uint16_t port = 8889;
const char* url = "/send_temphumi";

// for mode
String GotData = "";
WiFiClient client;

// NW connection
void nw_make_connection() {
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nConnected to WiFi!");
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());
}
