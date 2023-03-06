<?php
class Friends_Functions{
        private $conn;

        //constructor
        function __construct(){
            require_once './db_connect.php';
            //connect to db
            $db = new DB_CONNECT();
            $this->conn = $db->connect();
        }

        function __destruct(){
        
        }
	public function add_friend($uid,$name,$phone_no)
	{
		$stmt = $this->conn->prepare("INSERT INTO friends(uid,name,phone_no) VALUES(?, ?, ?)");
        $stmt->bind_param("sss",$uid,$name,$phone_no);
        $result = $stmt->execute();
        $stmt->close();
		if($result)
		{
		    return getfriends($uid,$this->conn);
		}
	}
    public function get_friends($uid)
    {
        return getfriends($uid,$this->conn);
    }
    public function delete_friend($uid,$phone)
    {
    		$stmt=$this->conn->prepare("DELETE from friends where uid = ? and phone_no = ?");
            $stmt->bind_param("ss",$uid,$phone);
            $stmt->execute();
            $stmt->close();
    }
    public function friendExisted($uid,$phone_no) {
            $stmt = $this->conn->prepare("SELECT * from friends WHERE phone_no = ? and uid = ?"); 
        	$stmt->bind_param("ss",$phone_no,$uid);
 
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
}
function getfriends($uid,$conn)
{
			$stmt=$conn->prepare("SELECT name,phone_no FROM friends WHERE uid = ?");
			$stmt->bind_param("s",$uid);
			$stmt->execute();
			$stmt->bind_result($name,$phone);
			for($response = array();$stmt->fetch();)
            {
                $result['name'] = $name;
                $result['phone']=  $phone;
                $response[]=$result;
            }
			return $response;
}
?>	