<?php
	require_once './friends_functions.php';
	$db = new Friends_Functions;
	$response['error']="false";
	if(isset($_POST['phone_no'])&&isset($_POST['uid']))
	{
		$phone_no = $_POST['phone_no'];
		$uid = $_POST['uid'];
		$db->delete_friend($uid,$phone_no);
                echo json_encode($response);
	}
	else
	{
		$response['error']="true";
		$repsonse['message']="phone_no or name not filled";
	        echo json_encode($response);
        }

?>
	