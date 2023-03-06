<?php
    class User_Functions{
        private $conn;

        //constructor
        function __construct(){
            require_once '/home/content/28/10504428/html/mobro.in/login/db_connect.php';
            //connect to db
            $db = new DB_CONNECT();
            $this->conn = $db->connect();
        }

        function __destruct(){
        
        }
        public function storeUser($name,$email,$password,$phone_no){
            $uuid = uniqid('',true);
            $encrypted_passwd = sha1($password);
            $stmt = $this->conn->prepare("INSERT INTO users(unique_id,name,email,passwd,phone_no) VALUES(?, ?, ?, ?, ?)");
            $stmt->bind_param("sssss",$uuid,$name,$email,$encrypted_passwd,$phone_no);
            $result = $stmt->execute();
            $stmt->close();
            if($result)
            {
                    $stmt = $this->conn->prepare("SELECT * FROM users WHERE phone_no = ? ");
                    $stmt->bind_param("s",$phone_no);
                    $stmt->execute();
                    $user = getResult($stmt);
                    return $user;
            }
            else
            {
                return false;
            }

        }
	public function isUserExisted($phone_no) {
            $stmt = $this->conn->prepare("SELECT email from users WHERE phone_no = ?"); 
        	$stmt->bind_param("s", $phone_no);
 
	        $stmt->execute();
 
 	       $stmt->store_result();
 
 	       if ($stmt->num_rows > 0) {
 	           // user exists 
 	           $stmt->close();
 	           return true;
 	       } else {
 	           // user does not exist
 	           $stmt->close();
 	           return false;
           }
    	}
	/**
     	* Get user by phone_no and password
     	*/
    	public function getUserByEmailAndPassword($phone_no,$password){
 
        $stmt = $this->conn->prepare("SELECT * FROM users WHERE phone_no = ?");
 
        $stmt->bind_param("s", $phone_no);
 
        if ($stmt->execute()) {
            $user = getResult($stmt);
			if(sha1($password) == $user['password']){return $user;}
			else{
      				return NULL;
			}
        } else {
            return NULL;
        }
    }
	public function getUserById($uid){
        $stmt = $this->conn->prepare("SELECT * from users WHERE unique_id = ?");
        $stmt->bind_param("s", $uid);
 
        if ($stmt->execute()) {
            $user = getResult($stmt);
			return $user;}

        else {
            return NULL;
        }
		
	}
        public function getUserByPhone($phone_no){
		$stmt = $this->conn->prepare("SELECT * FROM users WHERE phone_no = ?");
 
        $stmt->bind_param("s", $phone_no);
        if ($stmt->execute()) {
            
			$user = getResult($stmt);
            return $user;
			}
         else {
            return NULL;
        }
	
	}
        public function getUserByToken($token){
		$stmt = $this->conn->prepare("SELECT * FROM users WHERE reset_token = ?");
 
        $stmt->bind_param("s", $token);
        if ($stmt->execute()) {
            
			$user = getResult($stmt);
			return $user;
			}
         else {
            return NULL;
        }
	
	}
	public function updateToken($phone_no)
	{
		$token = md5(uniqid(rand(),true));
                $state = "True";
                $stmt = $this->conn->prepare("UPDATE users SET reset_token = ?, reset_state = ? WHERE phone_no = ?");
		        $stmt->bind_param("sss",$token,$state,$phone_no);
                $stmt->execute();
                $stmt->close;
                
	}
    public function updatePassword($password,$token)
	{	
		$state = "False";
		$stmt = $this->conn->prepare("UPDATE users SET reset_state = ?, passwd = ? WHERE reset_token = ?");
		$stmt->bind_param("sss",$state,$password,$token);
		$stmt->execute();
		$stmt->close;
		
	}
        public function updateWallet($uid,$amount)
	{	
		
                $stmt = $this->conn->prepare("UPDATE users SET wallet = ? WHERE unique_id = ?");
		$stmt->bind_param("ss",$amount,$uid);
		$stmt->execute();
		$stmt->close;
		
	}
	
    }
	function getResult($stmt)
	{
		    $stmt->bind_result($uid,$name,$email,$pass,$phone_no,$token,$state,$wallet);
			while($stmt->fetch());
                        $stmt->close;
			
			$user["unique_id"] = $uid;
			$user["name"] = $name;
			$user["email"] = $email;
                        $user["password"]=$pass;
			$user["phone_no"] = $phone_no;
			$user["token"]=$token;
			$user["state"]=$state;
                        $user["wallet"]=$wallet;
                        return $user;
	}
?>