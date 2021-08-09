#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include <pm2008_i2c.h>

PM2008_I2C pm2008_i2c;
#define DHTPIN 8
#define DHTTYPE DHT22

DHT dht(DHTPIN, DHTTYPE);

float hum;
float temp;
float pm;
int rain;
int tmp;
float lasthum;
float lasttemp;

void setup(){
  Serial.begin(9600);
  dht.begin();
  pm2008_i2c.begin();
}

void loop(){
  
  rain = analogRead(A0);
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
  Serial.println(rain);
  
  tmp = Serial.parseInt();
  delay(500);
}
