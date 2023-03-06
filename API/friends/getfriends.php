<?php
	require_once './friends_functions.php';
	$db = new Friends_Functions;
	$response['error']="false";
	if(isset($_POST['uid']))
	{
		$phone_no = $_POST['phone_no'];
		$name = $_POST['name'];
		$uid = $_POST['uid'];
		$result = $db->get_friends($uid,$name,$phone_no);
		$response['friends']=$result;
		echo json_encode($response);
	}
	else
	{
		$response['error']="true";
		$repsonse['message']="phone_no of name not filled";
                echo json_encode($response);
	}
?>