<?php

header("Content-Type: text/html;charset=utf-8");

$datalist = [];
$servername = "localhost";
$username = "root";
$password = "hadoop";
$dbname = "result1";

// 创建连接
$link = new mysqli($servername, $username, $password, $dbname);
// 检测连接
if ($link->connect_error) {
    die("连接失败: " . $link->connect_error);
}




$link->set_charset("utf8");

//学过点数据库的应该知道这句话是啥意思把。。。。。。
$sql = "select * from result_data1";
//执行上面的语句并把结果存在res里
$res = $link->query($sql);




$new = [];
$i=0;

if ($res->num_rows > 0) {

    while($row = $res->fetch_assoc()) {
   	    //这里将从数据库拿的数据赋值给一个变量，便于我们下面将他们存列表里，这里怎么写取决于你数据形式
        $action=$row["action"];
        $count=$row["count"];
        $news[$i] = array("action"=>"$action","count"=>"$count");
        $i=$i+1;

    }
} 




$datalist = $news;

echo json_encode($datalist,JSON_UNESCAPED_UNICODE);




?>

