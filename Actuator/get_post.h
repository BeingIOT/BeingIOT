#ifndef GET_POST_H
#define GET_POST_H

#include <WiFiS3.h>
#include <Arduino.h>

// GET, POST, reconnect(서버 끊김 방지), state(현재 ON/OFF상태)
String readResponse(WiFiClient& client);
void send_get_request(WiFiClient& client);
void send_post_request(WiFiClient& client);
void reconnect_wifi();
bool return_state();

#endif 