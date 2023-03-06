<?php
        $out['error']="false";
        require_once './transaction.php';
        require_once '../login/db_functions.php';
        $db = new Trans_Functions();
	$user = new User_Functions;
        if(isset($_GET['payment_id'])){
	$url="https://www.instamojo.com/api/1.1/payments/";
	$id=$_GET['payment_id'];
	$id = urlencode($id);
	$url=$url.$id.'/';
	$ch=curl_init();
	curl_setopt ($ch, CURLOPT_URL, $url);
	curl_setopt($ch, CURLOPT_HTTPHEADER, array(
			'X-Api-Key: 45b2c5388f95a96bc863c67eaeb46087',
			'X-Auth-Token: 5751cc20ed08902ab9ac9a41f50cb8a7'
			));
	curl_setopt ($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt ($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
	$return = curl_exec($ch);
	$curl_error = curl_errno($ch);
	curl_close($ch);
	$response=json_decode($return,true);
        $txid=$response['payment']["custom_fields"]["Field_46407"]["value"];
        if($response['payment']['status']=="Credit"){
        $trans=$db->getTransaction($txid);
        $uid=$trans['uid'];
        $customer = $user->getUserById($uid);
        $wallet=$customer['wallet'];
        $recharges=json_decode($trans['recharge'],true);
	if($trans['status']=="in_process"){
          foreach($recharges as $recharge)
          {
	        
                        $amount=$recharge['amount'];
			$phone_no=$recharge['phone_no'];
			$userid ='shopex';
			$key ='662214997158655';
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
			$result = explode(',',$return);
			$opcode = $result[0];
			
                        $ch = curl_init();
                        $url="http://www.mobro.in/transaction/payment.php?opcode=$opcode&amount=$amount&phone_no=$phone_no&txnid=$txid";
                        curl_setopt ($ch, CURLOPT_URL, $url);
			curl_setopt ($ch, CURLOPT_HEADER, 0);
                        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
                        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, TRUE);
                        $output = curl_exec ($ch);
			curl_close ($ch);
                        $output=explode(",", $output);
                        $count = count($output);
                        if($count==2)
                        {
                           $out['payment'][]="failed";
                           $wallet=$wallet+$amount;
                           $user->updateWallet($uid,$wallet);
                           echo $output[1];
                           $details.="Phone :$phone_no - Amount:$amount - Status:failed \r\n";
                         }
                         else {
                            $out['payment'][]="success";
                            $details.="Phone :$phone_no - Amount:$amount - Status:success \r\n";
                         }
                }
               $db->updateTransaction($txid,"Completed");
               sendMail($customer,True,$txid,$details);
          }
          else
         { 
             $out['error']="true";
             $out['message']="Payment already complete"; 
           }    
	}
        else
        {    
             $db->updateTransaction($txid,"Failed");
             $out['error']="true";
             $out['message']= "Not a valid payment";
          }
        }
	else
	{
	     
             $out['error']="true";
             $out['message']= "Not a valid payment";
	}
      echo json_encode($out);
        function sendMail($user,$status,$txid,$details)
	{
		$to=$user['email'];
		$subject="Mobro Mobile Recharge";
		if($status==True)
		{
			$body="Your Mobile was succcessfullt recharged for the specified ammount: order id $txid\r\n,
			$details
			Advertisement
			
			http:://advertisement.in
			";
		}
		else
		{
			$body="The transaction failed and the amount has been credited into your account: order id $txid
			
			Advertisement
			
			http:://advertisement.in
			";
		}
               mail($to, $subject, $body)
	}
?>				