<?php 
	require_once './db_functions.php';
	$db = new User_Functions();
	$resposns = array();
	$url="http://mobro.in/login/reset.php?key=";
        if(isset($_POST['phone_no']))
	{
		$phone_no=$_POST['phone_no'];
		$user=$db->isUserExisted($phone_no);
		$response['error']='False';
		if($user)
		{
			$db->updateToken($phone_no);
  			$user=$db->getUserByPhone($phone_no);
                        $url=$url.$user['token'];
                        
                        //send email
			$to = $user['email'];
			$subject = "Password Reset";
			$body = "A request was made to reset the password on this account.If this was a mistake,
                        just ignore this email .To reset your password, visit the 
                        following address: $url";
			mail($to, $subject, $body);
			$response['message']="Link to change password has been sent to your email id";
			echo json_encode($response);
		}
		else
		{
			$response['error']='True';
			$response['message']='No user with such credentials exists';
			echo json_encode($response);
		}
	}
	else
	{
		$response['error']='True';
		$response['message']='Please enter a mobile number';
		echo json_encode($response);
	}
?>