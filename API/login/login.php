<?php
require_once './db_functions.php';
$db = new User_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['phone_no']) && isset($_POST['password'])) {
 
    // receiving the post params
    $phone_no = $_POST['phone_no'];
    $password = $_POST['password'];
 
    // get the user by email and password
    $user = $db->getUserByEmailAndPassword($phone_no, $password);
 
    if ($user != false) {
        // use is found
        $response["error"] = FALSE;
        $response["uid"] = $user["unique_id"];
        $response["name"] = $user["name"];
        $response["email"] = $user["email"];
        $response["phone_no"] = $user["phone_no"];
        $response["wallet"]=$user["wallet"];
       // $response["user"]["updated_at"] = $user["updated_at"];
        echo json_encode($response);
    } else {
        // user is not found with the credentials
        $response["error"] = TRUE;
        $response["error_msg"] = "Login credentials are wrong. Please try again!";
        echo json_encode($response);
    }
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters Phone number or password is missing!";
    echo json_encode($response);
}
?>
	