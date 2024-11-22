// 1. 필요한 패키지 불러오기
const express = require('express');
const bodyParser = require('body-parser');
const fs = require('fs');

// 2. Express 애플리케이션 생성
const app = express();
const port = 8889;

// 로그 파일에 접속 정보를 기록하는 함수
function logRequest(req) {
    const timestamp = new Date().toISOString();
    const logMessage = `${timestamp} - Method: ${req.method}, URL: ${req.url}, IP: ${req.ip}\n`;

    // 로그 파일에 기록
    fs.appendFile('access_arduino.log', logMessage, (err) => {
        if (err) {
            console.error('Error writing to log file:', err);
        }
    });
}

// 모든 요청에 대해 로그를 기록하는 미들웨어
app.use((req, res, next) => {
    logRequest(req);
    next();
});

// 3. body-parser를 사용하여 문자열 데이터를 처리
app.use(bodyParser.text({ type: 'text/plain' }));

// 4. 센서 데이터 저장소
let temphumi = '';
// 현재 작동 값
let status = '';
// android_express에서 전달된 post 값을 servobit에 저장
let servo_bit = '';

// 5. POST 요청 처리 - 두 개의 아두이노 보드가 센서 값을 문자열로 전송
// temphumi 값 post
app.post('/send_temphumi', (req, res) => {
    console.log("Connect send_temphumi");

    const received = req.body;
    // 문자열 분할
    console.log(received);
    let str = received.split(' ');

    temphumi = extractNumbers(str);
    console.log(`TempHumi:${JSON.stringify(temphumi)}`);

    if (received) {
        res.status(200).send('Data received');
        console.log('Data sends Sucessfully');
    }else {
        res.status(400).send('No data received');
    }
});

//lcd 값 post
app.post('/send_lcd', async (req, res) => {
    console.log("send_lcd (POST) Connect");
    const received = req.body;

    // 문자열 분할
    let str = received.split(' ');
    console.log(received);
    status = extractWords(str);
    console.log(`Status:${JSON.stringify(status)}`);

    if (received) {
        await res.status(200).send('Data received');
        console.log('send_lcd (POST) Data sends Sucessfully');
    } else {
        await res.status(400).send('No data received');
    }
});

// lcd 보드로 작동 모드 전달
app.get('/send_lcd', (req, res) => {
    console.log("send_lcd (GET) Connection");
    //let send_temphumi = JSON.stringify(temphumi);
    
    // 안드로이드에서 버튼 눌렀을 때 만 전송하도록
    console.log(servo_bit);
    res.status(200).send(servo_bit);// 저장된 모든 센서 데이터를 JSON으로 반환
    if(servo_bit === '0 PUSH'){
        servo_bit = 'O NO';
    }
    console.log("send_lcd send Data");
});

// android에서 servobit 받음 -> 서보모터가 달린 보드에서 get 하면 전달 해야 함
app.post('/send_arduino_servobit', (req, res) => {
    console.log("send_arduino_servobit Connection");
    servo_bit = req.body;
    console.log("send_arduino_servobit", servo_bit); // 저장된 모든 센서 데이터를 JSON으로 반환
});

// temphumi, status를 android_express에 전달함
// test 완료
app.get('/receive_android', (req, res) => {
    // temphumi와 status를 하나의 객체로 묶어서 전송
    //console.log("receive_android Connection");
    temphumi[Object.keys(status)[0]] = Object.values(status)[0];
    //console.log(temphumi);
    /*
    let data = '';
    console.log(temp, humi);
    data = {temphumi, status};*/

    res.status(200).send(temphumi);
    console.log("receive_android Data is sucessfully Sent");
});

/*
// a
app.get('/received_servoBit', async (req, res) => {
    console.log("received_servoBit Connection");
    if(servo_bit == ''){
        res.status(200).send('Servo Motor bit is not defined');
    }else{
        res.status(200).send(servo_bit);
    }
    console.log("received_servoBit Data is sucessfully Sent");
});
*/
// 7. 서버 시작
app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});

// 숫자값만 추출하는 함수
function extractNumbers(data) {
    
    const numbers = data
        .map(item => item.replace(/[^0-9.]/g, '')) // 숫자와 소수점만 남기기
        .filter(item => item !== '')                // 빈 문자열 제거
        .map(Number);                               // 문자열을 숫자로 변환

    // json 변환
    sensor = {
        "Temp":  numbers[1],
        "Humi":  numbers[0]
    }
    return sensor;
}

// "power"와 "on/off"를 찾는 함수
function extractWords(data) {
    let result = '';
    data.forEach(item => {
        const lowerCaseItem = item.toLowerCase(); // 소문자로 변환하여 비교
        if (lowerCaseItem === 'on'){
            result= {"Status": "Green"};
        }else if(lowerCaseItem === 'off') {
            result= {"Status": "Red"};
        }
    });
    return result;
}
