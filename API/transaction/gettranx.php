<?php
	require_once './transaction.php';
	$db = new Trans_Functions();
 
// json response array
$response = array("error" => "false");
 
if (isset($_POST['txid'])){
	$txid=$_POST['txid'];
	$result=$db->getTransaction($txid);
	if ($result) {
            // user stored successfully
            $response["error"] = "false";
            $response["txid"]=$result["txid"];
			$response["uid"] = $result["uid"];
            $response["phone_no"] = $result["phone_no"];
			$response["amount"]=$result["amount"];
			$response["status"]=$result["status"];
			$response["date"]=$result["date"];
		   // $response["user"]["updated_at"] = $user["updated_at"];
            echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = "false";
            $response["error_msg"] = "Unknown error occurred in registration!";
            echo json_encode($response);
			}
}
else{
	$response["error"] = "true";
	$response["error_msg"] = "Error while processing parameters not set";
	echo json_encode($response);
}

?>