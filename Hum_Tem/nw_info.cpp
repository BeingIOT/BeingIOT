#include "nw_info.h"

// Enter your AP, Server info
// AP info init
const char* ssid = nullptr;
const char* password = nullptr;
// Server info init
const char* host = nullptr;
const uint16_t port = nullptr;
const char* url = nullptr;

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
