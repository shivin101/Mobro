<?php
	$opcode = $_POST['opcode'];
	$circode = $_POST['circode'];
	$userid ='shopex';
	$key ='662214997158655';
	$url = "https://joloapi.com/api/findplan2.php?userid=$userid&key=$key&opt=$opcode&cir=$circode&typ=TUP&amt=&max=";
	$ch = curl_init();
	$timeout = 100;
	curl_setopt ($ch, CURLOPT_URL, $url);
	curl_setopt ($ch, CURLOPT_HEADER, 0);
	curl_setopt ($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt ($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
	$return = curl_exec($ch);
	$curl_error = curl_errno($ch);
	curl_close($ch);
        $response['packages']=json_decode($return);
        echo json_encode($response);
?>