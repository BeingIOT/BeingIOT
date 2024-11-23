#ifndef POST_H
#define POST_H

#include <WiFiS3.h>
#include <Arduino.h>

String readResponse(WiFiClient& client);
void send_post_request(WiFiClient& client, float h, float hic);
void reconnect_wifi();

#endif