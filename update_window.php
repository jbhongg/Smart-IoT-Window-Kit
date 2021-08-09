<?php
    $mysql_host = 'localhost';
    $mysql_user = 'root';
    $mysql_password = '1234';
    $mysql_db = 'iot';

    $conn = @mysql_connect($mysql_host,$mysql_user,$mysql_password);
    $dbconn = mysql_select_db($mysql_db, $conn);

    $value = "'".$_POST['value']."'"; 
    $id = "'".$_POST['id']."'";
    
    mysql_query("set names utf8");

    $query = "UPDATE window SET motor_stat = $value WHERE id = $id";

    $result = mysql_query($query);

   
?>