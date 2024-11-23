#ifndef NW_INFO_H
#define NW_INFO_H

#include <WiFiS3.h>

// Access Point 정보
extern const char* ssid;
extern const char* password;
// Server 정보
extern const char* host;
extern const uint16_t port;
extern const char* url;

extern String GotData; // Server Response Data
extern WiFiClient client;

void nw_make_connection();

#endif 