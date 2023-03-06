<?php  
        $userid ='shopex';
	$key ='662214997158655';
	$phone_no =$_POST['phone_no'];
	$url ="http://joloapi.com/api/findoperator2.php?userid=$userid&key=$key&mob=$phone_no";
        $ch = curl_init();
	$timeout = 100;
	curl_setopt ($ch, CURLOPT_URL, $url);
	curl_setopt ($ch, CURLOPT_HEADER, 0);
	curl_setopt ($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt ($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
	$return = curl_exec($ch);
	$curl_error = curl_errno($ch);
	curl_close($ch);
	//dump output of api if you want during test
	$response = explode(',',$return);
	$operator['opcode'] = $response[0];
	$operator['circode'] = $response[1];
	echo json_encode($operator);
?>	