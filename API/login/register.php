<?php
 
require_once './db_functions.php';
$db = new User_Functions();
 
// json response array
$response = array("error" => "false");
if (isset($_POST['name']) && isset($_POST['email']) && isset($_POST['password'])&& isset($_POST['phone_no']) ){
 
    // receiving the post params
    $name = $_POST['name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $phone_no = $_POST['phone_no'];
    // check if user is already existed with the same phone_no
    if ($db->isUserExisted($phone_no)) {
        // user already existed
        $response["error"] = "true";
        $response["error_msg"] = "User already existed with " . $phone_no;
        echo json_encode($response);
    } else {
        // create a new user
        $user = $db->storeUser($name, $email, $password,$phone_no);
        if ($user) {
            // user stored successfully
            $response["error"] = "false";
            $response["uid"] = $user["unique_id"];
            $response["name"] = $user["name"];
            $response["email"] = $user["email"];
            $response["phone_no"] = $user["phone_no"];
            $response["wallet"]=$user["wallet"];
           // $response["user"]["updated_at"] = $user["updated_at"];
            echo json_encode($response);
        } else {
            // user failed to store
            $response["error"] = "false";
            $response["error_msg"] = "Unknown error occurred in registration!";
            echo json_encode($response);
        }
    }
} else {
    $response["error"] = "true";
    $response["error_msg"] = "Required parameters (name, email,password or phone_no) is missing!";
    echo json_encode($response);
}
?>
	