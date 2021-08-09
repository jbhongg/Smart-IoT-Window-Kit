<?php
    $mysql_host = 'localhost';
    $mysql_user = 'root';
    $mysql_password = '1234';
    $mysql_db = 'iot';

    $conn = mysql_connect($mysql_host,$mysql_user,$mysql_password);
    $dbconn = mysql_select_db($mysql_db, $conn);

    mysql_query("set names utf8");

    $query = "select * from window";

    $result = mysql_query($query);

    $row = mysql_fetch_array($result);

    print(json_encode($row));
    mysql_close($conn);
       
    ?>