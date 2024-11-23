#include <Arduino.h>
#include "nw_info.h"
#include "post.h"
#include <DHT.h>
#include <LiquidCrystal_I2C.h>

LiquidCrystal_I2C lcd(0x27, 16, 2);
DHT dht(6, DHT11);

void setup() {
  Serial.begin(115200);
  delay(1000);

  dht.begin();
  lcd.init();
  lcd.backlight();

  nw_make_connection();
}

void loop() {
  // 센서 데이터 읽기
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  float hic = dht.computeHeatIndex(t, h, false);

  lcd.setCursor(0, 0);
  lcd.print("Hum: ");
  lcd.setCursor(6, 0);
  lcd.print(h);
  lcd.setCursor(0, 1);
  lcd.print("Temp: ");
  lcd.setCursor(6, 1);
  lcd.print(hic);

  Serial.print("Hum: ");
  Serial.print(h);
  Serial.print(" Temp: ");
  Serial.println(hic);

  if (WiFi.status() == WL_CONNECTED) {
    send_post_request(client,h,hic);
   
  }

  else {
    Serial.println("WiFi Disconnected");
    reconnect_wifi();
  }

}
