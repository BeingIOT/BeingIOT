// 1. 필요한 패키지 불러오기
const express = require('express');
const http = require('http');
const bodyParser = require('body-parser');
const fs = require('fs');

// 2. Express 애플리케이션 생성
const app = express();
const port = 8890;

// 3. body-parser를 사용하여 문자열 데이터를 처리
app.use(bodyParser.json());

let power = ''; // post 통해 입력받은 on/off
let mode = ''; // post 통해 입력받은 mode 상태
let bit = ''; // 서보모터 on, off 작동 여부
let bit_Backup = ''; // 백업용
// 로그 파일에 접속 정보를 기록하는 함수
function logRequest(req) {
    const timestamp = new Date().toISOString();
    const logMessage = `${timestamp} - Method: ${req.method}, URL: ${req.url}, IP: ${req.ip}\n`;

    // 로그 파일에 기록
    fs.appendFile('access_and.log', logMessage, (err) => {
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

app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});

// 리모콘에서 power 값 post
app.post('/send_power', (req, res) => {
    console.log("send_power Connection");
    let ending = 'Data is received successfully';
    const received = req.body;
    console.log("Client send", received);
    //sendBitToArduino(received);
    if (received) {
        mode = Object.values(received)[0];
        console.log("Mode", mode);

        if (mode == 'Auto'){
            bit = '1';
            console.log("Mode, ON/OFF", mode, bit);
        }else if(mode =='On'){
            bit = '0 PUSH'
            console.log("Mode, ON/OFF", mode, bit);
        }else if(mode =='Off'){
            bit = '0 NO'
            console.log("Mode, ON/OFF", mode, bit);
        }else{
            console.log("Wrong Data Type");
            res.status(200).send("Mode is not avaliable to use Power!");
            return;
        }
        console.log("Mode, ON/OFF", mode, bit);
        sendBitToArduino(bit);
        mode = "OFF";
        console.log("Mode, ON/OFF", mode, bit);
        res.status(200).send(ending);
    }else {
        res.status(400).send('No data received');
    }
});

let temphumi = '';
// get 통해 and에게 아두이노 센서값 전달
app.get('/received_temphumi', async (req, res) => {
    console.log("received_temphumi Connection");
    try {
        temphumi = await get_temphumi();
        console.log(temphumi);
        let data = temphumi;
        console.log(data);
        //data.temphumi[Object.keys(data.status)[0]] = Object.values(data.status)[0];
        //data = data.temphumi;
        console.log('temphumi', data);
        res.status(200).send(data);
        console.log('type', typeof(data));
    } catch (error) {
        console.error('Error:', error);
        res.status(500).send('Failed to retrieve data');
    }
});

// received temphumi 실행할 함수
function get_temphumi() {
    return new Promise((resolve, reject) => {
        const options = {
            hostname: 'greyk.iptime.org',
            port: 8889,
            path: '/receive_android',
            method: 'GET',
            timeout: 1000 // 5초 타임아웃 설정
        };

        let data = '';
        const req = http.request(options, (res) => {
            res.on('data', (chunk) => {
                data += chunk;
            });
            res.on('end', () => {
                resolve(data);
            });
        });
 
        req.on('error', (e) => {
            console.error(`Problem with request: ${e.message}`);
            reject(e);
        });

        req.on('timeout', () => {
            console.error('Request timed out');
            req.abort(); // 타임아웃 시 요청 중단
            reject(new Error('Request timed out'));
        });

        req.end();
    });
}

// 리모콘에서 받은 servo_bit를 아두이노에게 전달
function sendBitToArduino(and_bit) {
    return new Promise((resolve, reject) => {
    const options = {
        hostname: 'greyk.iptime.org',
        port: 8889,
        path: '/send_arduino_servobit',
        method: 'POST',
        headers: {
            'Content-Type': 'text/plain',
            'Content-Length': Buffer.byteLength(and_bit),
            timeout: 1000 // 5초 타임아웃 설정
        }
    };

    const req = http.request(options, (res) => {
        let str = '';
        res.on('data', (chunk) => {
            str += chunk;
        });
        res.on('end', () => {
            console.log(`"${str}" is sent from arduino_express.js`);
        });
    });

    req.on('error', (err) => {
        console.error('HTTP request error:', err);
    });

    req.write(and_bit);
    console.log("Servo Bit is sent successfully");
    req.end();
});
}
