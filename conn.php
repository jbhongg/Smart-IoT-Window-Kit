<?php
    $mysql_host = 'localhost';
    $mysql_user = 'root';
    $mysql_password = '1234';
    $mysql_db = 'iot';

    $conn = @mysql_connect($mysql_host,$mysql_user,$mysql_password);
    $dbconn = mysql_select_db($mysql_db, $conn);

    mysql_query("set names utf8");

    $query = "select * from window";

    $result = mysql_query($query);

    $row = mysql_fetch_array($result);

    $row_array = array(
    "id" => $row['id'],
    "pw"=> $row['pw'],
    'dust_in'=>$row['dust_in'],
    'dust_out'=>$row['dust_out'],
    'motor_stat'=>$row['motor_stat'],
    'temperature_in'=>$row['temperature_in'],
    'temperature_out'=>$row['temperature_out'],
    'humidity_in'=>$row['humidity_in'],
    'humidity_out'=>$row['humidity_out'],
    'fan_stat'=>$row['fan_stat'],
    'gas_stat'=>$row['gas_stat'],
    'is_rain'=>$row['is_rain'],
    'mode'=>$row['mode'],
    'wifi'=>$row['wifi']);
    
    $arr= array(
        "result" =>[$row_array]
    );
    $json_array = json_encode($arr);
    print($json_array);
    //print($row);
    //print(json_encode($row));
    mysql_close($conn);
       
    ?>