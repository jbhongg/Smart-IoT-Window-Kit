import serial
import time
import RPi.GPIO as GPIO
import pymysql
import os

def main():
    try:
        port = serial.Serial("/dev/ttyACM0", baudrate=9600, timeout=None)
        port2 = serial.Serial("/dev/ttyACM1", baudrate=9600, timeout=None)
        conn = pymysql.connect(host='175.205.244.188', user='root', password='1234', db='iot', charset='utf8')
        curs = conn.cursor()

        sql = "select mode,motor_stat from window where id=\'bae';"
        curs.execute(sql)
        arr = curs.fetchone()

	tmp_userstate = arr[0]
	user_state= arr[0]
        tmp = arr[1]
        motor = arr[1]

        switch1 = 27
        switch2 = 18

        GPIO.setmode(GPIO.BCM)
        GPIO.setwarnings(False)
        GPIO.setup(switch1, GPIO.IN, GPIO.PUD_UP)
        GPIO.setup(switch2, GPIO.IN, GPIO.PUD_UP)

        while True:
            hum = float(port.readline())
	    print("Humidity in pi:" + str(format(hum, ".2f")) + "%")

	    hum2 = float(port2.readline())
	    print("Humidity in MH:" + str(format(hum2, ".2f")) + "%")

	    tem = float(port.readline())
	    print("Temparature in pi:" + str(format(tem, ".2f")) + "C")

	    tem2 = float(port2.readline())
	    print("Temparature in MH:" + str(format(tem2, ".2f")) + "C")

	    pm = float(port.readline())
	    print("PM 2.5 in pi:" + str(format(pm, ".2f")))

	    pm2 = float(port2.readline())
	    print("PM 2.5 in MH:" + str(format(pm2, ".2f")))

	    gas = float(port.readline())
	    print("Gas in pi:" + str(format(gas, ".2f")))

	    rain = float(port2.readline())
	    print("Raindrop in MH:" + str(format(rain, ".2f")))

	    if tem < 15 or tem2 < 15 or tem > 45 or tem2 > 45 or hum < 30 or hum2 < 30:
		conn.close()
		os.system("python /home/pi/Downloads/jdc.py")

	    sql = 'update window set dust_in=' + str(format(pm, ".2f")) + ',dust_out=' + str(format(pm2, ".2f")) + ',temperature_in=' + str(format(tem, ".2f")) + ',temperature_out=' + str(format(tem2, ".2f")) + ',humidity_in=' + str(format(hum,".2f")) + ',humidity_out=' + str(format(hum2,".2f")) + ',gas_stat=' + str(format(gas,".2f")) + ',is_rain=' + str(format(rain,".2f")) + ";"
	    curs.execute(sql)
	    conn.commit()

	    sql = "select mode,motor_stat from window where id=\'bae';"
	    curs.execute(sql)
	    arr = curs.fetchone()

	    tmp_userstate = user_state
	    user_state = arr[0]

            tmp = motor
	    motor = arr[1]

	    print("\n\n")
	    time.sleep(0.01)

	    if user_state == 0:
		if tmp_userstate != user_state:
		    if GPIO.input(switch1) == 0 and GPIO.input(switch2) == 0:
			port.write("1")
			tmp_userstate = user_state
		    else:
			port.write("0")
			tmp_userstate = user_state
                else:
	            if motor != tmp:
		        if motor > tmp:
		            open = motor-tmp
		            motor_value = str(open) + "8"
		            port.write(motor_value)
		        else:
		            close = tmp-motor
		            motor_value = str(close) + "9"
		            port.write(motor_value)
	            else:
	                port.write("0")
	    else:
		if tmp_userstate != user_state:
		    sql = "update window set motor_stat=0;"
		    curs.execute(sql)
		    conn.commit()
		    if motor != 0:
			close = motor
		        motor_value = str(close) + "9"
		        port.write(motor_value)
		        tmp_userstate = user_state
	        else:
	            if GPIO.input(switch1) == 0 and GPIO.input(switch2) == 0:
		        if gas > 90:
			    port.write("3")
		        else:
			    if rain <= 500:
			        if pm2 >= 36:
				    if pm > pm2:
				        port.write("5")
				    else:
				        port.write("4")
			        elif pm2 < 36 and pm >=36:
				    port.write("5")
			        else:
				    port.write("4")
			    else:
			        if pm2 >= 36:
				    if pm > pm2:
				        port.write("5")
				    else:
				        port.write("4")
			        else:
				    port.write("2")
		    elif GPIO.input(switch1) == 0 and GPIO.input(switch2) == 1:
		        if gas > 90:
		            port.write("7")
		        else:
			    if rain <= 500:
			        if pm2 >= 36:
		                    if pm > pm2:
				        port.write("3")
				    else:
				        port.write("2")
			        elif pm2 < 36 and pm >= 36:
				    port.write("3")
			        else:
				    port.write("2")
			    else:
			        if pm2 >= 36:
				    if pm > pm2:
				        port.write("3")
				    else:
				        port.write("2")
			        else:
				    port.write("6")

    except Exception as e:
	print(e)
	conn.close()
        os.system("python /home/pi/Downloads/jdc.py")

if __name__ == "__main__":
    main()
