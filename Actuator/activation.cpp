#include "activation.h"
#include "nw_info.h"
#include "get_post.h"
#define SEASON 1 // 1 : winter , 0 : summer

//Servo Pin 6
Servo myServo6;

// Servo 동작 정의
void moving_servo() {
  myServo6.attach(6);
  myServo6.write(0);
  delay(700);
  myServo6.write(150);
  delay(400);
  myServo6.detach();
}

// Servo 동작
void activation() {
  //winter
  if(SEASON){
    // GotData: 0(리모컨 모드) PUSH(application에서 전원 버튼 터치)
    if (GotData == "0 PUSH") {
      send_post_request(client); // Pre-state POST
      moving_servo();
      send_post_request(client); // Post-state POST
    } 
    // GotData: 0(리모컨 모드) NO(application에서 전원을 터치하지 않고 리모컨 모드인 상태)
    else if (GotData == "0 NO") {
      send_post_request(client);
    } 
    // GotData : 1(자동 모드), return_state(): OFF상태면 true를 return
    else if (return_state() && GotData == "1") {
      moving_servo();
      delay(60000); // 난방은 약 1분간의 대기시간을 필요로 함.
    }
  }
    //summer
  else{
    // GotData : 0(리모컨 모드) PUSH(application에서 전원 버튼 터치)
    if (GotData == "0 PUSH") {
      send_post_request(client); // Pre-state POST
      moving_servo();
      send_post_request(client); // Post-state POST
    } 
    // GotData : 0(리모컨 모드) NO(application에서 전원을 터치하지 않고 리모컨 모드인 상태)
    else if (GotData == "0 NO") {
      send_post_request(client);
    } 
    // GotData : 1(자동모드), return_state(): OFF상태면 true를 return
    else if (return_state() && GotData == "1") {
      moving_servo();
      delay(360000); // 냉방은 약 6분간의 대기시간을 필요로 함.
    }
  }

}

