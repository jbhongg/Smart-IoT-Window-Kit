#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include <pm2008_i2c.h>
#include <Arduino.h>
#include "BasicStepperDriver.h"

PM2008_I2C pm2008_i2c;
#define DHTPIN 3
#define DHTTYPE DHT22
#define MOTOR_STEPS 200
#define RPM 35
#define MICROSTEPS 16
#define SLEEP 2
#define DIR 8
#define STEP 9
#define ENA 4
#define IN1 5
#define IN2 6


DHT dht(DHTPIN, DHTTYPE);
BasicStepperDriver stepper(MOTOR_STEPS, DIR, STEP, SLEEP);


float hum;
float temp;
float pm;
int gas;
int tmp;
int motor_value;
float lasthum;
float lasttemp;

void setup(){
  Serial.begin(9600);
  dht.begin();
  pm2008_i2c.begin();
  stepper.begin(RPM, MICROSTEPS);
  pinMode(ENA, OUTPUT);
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
}

void loop(){
  gas = analogRead(A0);
  hum = dht.readHumidity();
  temp = dht.readTemperature();
  uint8_t ret = pm2008_i2c.read();
  pm = pm2008_i2c.pm2p5_grimm;
  
  if(!isnan(hum))
  {
    lasthum = hum;
    Serial.println(hum);
  }
  else
  {
    Serial.println(lasthum);
  }
  
  if(!isnan(temp))
  {
    lasttemp = temp;
    Serial.println(temp);
  }
  else
  {
    Serial.println(lasttemp);
  }

  if(ret == 0)
  {
    Serial.println(pm);
  }
  Serial.println(gas);
  tmp = Serial.parseInt();

  if(tmp == 1) // window close
  {
    stepper.move(650*16);
    stepper.disable();
  }
  else if(tmp == 2) // fan off
  {
    digitalWrite(IN1, HIGH);
    digitalWrite(IN2, LOW);
    digitalWrite(ENA, 0);   
  }
  else if(tmp == 3) // fan on
  {
    digitalWrite(IN1, HIGH);
    digitalWrite(IN2, LOW);
    digitalWrite(ENA, 255);  
  }
  else if(tmp == 4) // window close and fan off
  {
    stepper.move(650*16);
    stepper.disable();
    digitalWrite(IN1, HIGH);
    digitalWrite(IN2, LOW);
    digitalWrite(ENA, 0);  
  }
  else if(tmp == 5) // window close and fan on
  {
    stepper.move(650*16);
    stepper.disable();
    digitalWrite(IN1, HIGH);
    digitalWrite(IN2, LOW);
    digitalWrite(ENA, 255);
  }
  else if(tmp == 6) // window open fan off
  {
    stepper.move(-650*16);
    stepper.disable();
    digitalWrite(IN1, HIGH);
    digitalWrite(IN2, LOW);
    digitalWrite(ENA, 0); 
  }
  else if(tmp == 7) //window open fan on
  {
    stepper.move(-650*16);
    stepper.disable();
    digitalWrite(IN1, HIGH);
    digitalWrite(IN2, LOW);
    digitalWrite(ENA, 255); 
  }
  else if(tmp%10 == 8) //control mode window open
  {
    motor_value = tmp/10;
    stepper.move(-motor_value*16);
    stepper.disable();
  }
  else if(tmp%10 == 9) //control mode window close
  {
    motor_value = tmp/10;
    stepper.move(motor_value*16);
    stepper.disable();
  }
  else // nothing
  {
    stepper.disable();
    digitalWrite(IN1, HIGH);
    digitalWrite(IN2, LOW);
    digitalWrite(ENA, 0); 

  }
  delay(500);
}

