<?php
	require_once './friends_functions.php';
	$db = new Friends_Functions;
	$response['error']="false";
	if(isset($_POST['phone_no'])&&isset($_POST['name'])&&isset($_POST['uid']))
	{
		$phone_no = $_POST['phone_no'];
		$name = $_POST['name'];
		$uid = $_POST['uid'];
		if($db->friendExisted($uid,$phone_no)==False){
		$result = $db->add_friend($uid,$name,$phone_no);
		$response['friends']=$result;
		}
		else{
			$response['error']="true";
			$response['message']="Friend with the phone number already exists";
		}
	}
	else
	{
		$response['error']="true";
		$repsonse['message']="phone_no of name not filled";
	}
	echo json_encode($response);
?>