<?php 
	require_once './transaction.php';
	require_once '../login/db_functions.php';
	$users=new User_Functions();
	$trans=new Trans_Functions();
	$surl="http://mobro.in/transaction/success.php";
	$furl="http://mobro.in/transaction/faliure.php";
	$url = 'http://mobro.in/trial.php';
	$purl="http://imojo.in/1aar0x/?intent=buy";
	$salt='f65819f6cc7e42d18811d0682a68ffbf';
   
	if (isset($_POST['uid']) && isset($_POST['amount'])&&isset($_POST['recharge']))
	{
		$uid=$_POST['uid'];
		$amount=$_POST['amount'];
		$recharge=$_POST['recharge'];
		$user=$users->getUserById($uid);
		if(!$user)
		{
			$response["error"] = "true";
		    $response["error_msg"] = "Internal error in getting user details";
		    echo json_encode($response);
			exit;
		}
                $phone_no=$user['phone_no'];
		$tranx = $trans->storeTransaction($uid,$amount,$phone_no,$recharge);
		if(!tranx)
		{
			$response["error"] = "true";
		    $response["error_msg"] = "Internal error in getting transaction details";
		    echo json_encode($response);
			exit;
		}
		if($amount > $user['wallet'])
		{
			$wallet=$user['wallet'];
			$amount=$amount-$wallet;
			$wallet=0;
			if($amount<10)
			{
				$change=10-$amount;
				$amount=10;
				$wallet+=$change;
			}
			$users->updateWallet($tranx['uid'],$wallet);
			$txid=$tranx['txid'];
			$name=$user['name'];
			$email=$user['email'];
                $sign=$amount;
                $sign=hash_hmac('sha1',$sign, $salt);
			$fields = array(
						'sign'=>urlencode($sign),
						'amount'=>urlencode($amount),
						'email'=>urlencode($email),
						'name'=> urlencode($name),
						'phone'=> urlencode($phone_no),
						'Field_46407'=>urlencode($txid),
						'Field_46408'=>urlencode($tranx['uid']),
				);

		//url-ify the data for the Request
		$fields_string='&data_readonly=data_amount&data_hidden=data_Field_46407&data_hidden=data_Field_46408';
		foreach($fields as $key=>$value) { $fields_string .= '&data_'.$key.'='.$value; }
		rtrim($fields_string, '&');
		$purl= $purl.$fields_string;
                
		
		
        
		$response['link']=$purl;
        echo json_encode($response);	
		
		}
		else{
			$wallet=$user['wallet'];
			$wallet=$wallet-$amount;
			$txid=$tranx['txid'];
			$users->updateWallet($uid,$wallet);
			$sign=hash_hmac('sha1',$txid,$tranx['uid']);
			$purl="http://mobro.in/transaction/alternate.php?txid=$txid&sign=$sign";
			$response['link']=$purl;
                        echo json_encode($response);
		}
	}
	else
	{
		$response["error"] = "true";
		$response["error_msg"] = "Required parameters (amount or phone_no) is missing!";
		echo json_encode($response);
	}
?>		