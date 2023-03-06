<?php 
	$opcode['22']="VF";
	$opcode['28']="AT";
	$opcode['3']="BS";
	$opcode['8']="IDX";
	$opcode['17']="TD";
	$opcode['13']="RG";
	$opcode['19']="UN";
        $mode=1;
	$userid ='shopex';
	$key ='662214997158655';
	$operator=$opcode[$_GET['opcode']];
	$service=$_GET['phone_no'];
	$amount=$_GET['amount'];
	$orderid=$_GET['txnid'];
	$url="http://joloapi.com/api/recharge2.php?mode=$mode&userid=$userid&key=$key&operator=$operator&service=$service&amount=$amount&orderid=$orderid";
        $ch = curl_init();
	curl_setopt ($ch, CURLOPT_URL, $url);
	curl_setopt ($ch, CURLOPT_HEADER, 0);
	curl_setopt ($ch, CURLOPT_RETURNTRANSFER, 1);
	$return = curl_exec($ch);
	$curl_error = curl_errno($ch);
	curl_close($ch);
        header('Content-type: application/json');
        echo json_encode($return);

?>