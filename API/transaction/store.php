<?php
 
require_once './transaction.php';
$db = new Trans_Functions();
 
// json response array
$response = array("error" => "false");
 
if (isset($_POST['phone_no']) && isset($_POST['uid']) && isset($_POST['amount'])){
 
    // receiving the post params
    $amount=$_POST["amount"];
	$uid=$_POST["uid"];
	$phone_no=$_POST["phone_no"];
	$trans = $db->storeTransaction($uid,$amount,$phone_no);
        if ($trans) {
            // user stored successfully
            $response["error"] = "false";
            $response["txid"]=$trans["txid"];
			$response["uid"] = $trans["uid"];
            $response["phone_no"] = $trans["phone_no"];
			$response["amount"]=$trans["amount"];
			$response["status"]=$trans["status"];
			$response["date"]=$trans["date"];
		   // $response["user"]["updated_at"] = $user["updated_at"];
            echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = "false";
            $response["error_msg"] = "Unknown error occurred in registration!";
            echo json_encode($response);
			}
}
else {
$response["error"] = "true";
$response["error_msg"] = "Required parameters (amount or phone_no) is missing!";
echo json_encode($response);
	
}
?>