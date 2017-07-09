<?php
error_reporting(E_ALL);
echo "<h2>tcp/ip connection </h2>\n";
$service_port = 30023;
$address = '127.0.0.1';

$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
if ($socket === false) {
	echo "socket_create() failed: reason: " . socket_strerror(socket_last_error()) . "\n";
} else {
	echo "OK. \n";
}

echo "Attempting to connect to '$address' on port '$service_port'...";
$result = socket_connect($socket, $address, $service_port);
if($result === false) {
	echo "socket_connect() failed.\nReason: ($result) " . socket_strerror(socket_last_error($socket)) . "\n";
} else {
	echo "OK \n";
}

$in = '{last_ip=127.0.0.1, logintime=3, block_status=0, paypassword=null, tuijian_userid=1, terminal=0, type=1, up_ip=171.214.213.210, realname=, spreads_key=045082, password=7e100345eb9c6a1c192a90001016cce2, province=40, user_id=2, phone=13688045082, last_time=1457846897, reg_time=1457747459, up_time=1457844047, reg_ip=127.0.0.1, yibao=1, email=, username=zsjr_';
//$in .= "\r\n";

$out = "";
echo "sending http head request ...";
socket_write($socket, $in, strlen($in));
echo  "OK\n";

echo "Reading response:\n\n";
while ($out = socket_read($socket, 8192)) {
	$out .= $out;
}
var_dump($out);
echo "closeing socket..";
socket_close($socket);
echo "ok .\n\n";