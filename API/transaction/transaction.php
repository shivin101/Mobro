<?php 

$posted = array();
if(!empty($_POST)) {
    //print_r($_POST);
  foreach($_POST as $key => $value) {    
    $posted[$key] = $value; 
	
  }
}
class Trans_Functions{
        private $conn;

        //constructor
        function __construct(){
            require_once '../login/db_connect.php';
            //connect to db
            $db = new DB_CONNECT();
            $this->conn = $db->connect();
        }

        function __destruct(){
        
        }
        public function storeTransaction($uid,$amount,$phone_no,$recharge){
            $txid = uniqid('',true);
			$status='in_process';
			$date = date('Y-m-d H:i:s');
			$stmt = $this->conn->prepare("INSERT INTO transaction(txid,phone_no,amount,status,date,uid,recharge) VALUES(?, ?, ?, ?, ?, ?, ?)");
            $stmt->bind_param("sssssss",$txid,$phone_no,$amount,$status,$date,$uid,$recharge);
            $result = $stmt->execute();
            $stmt->close();
            if($result)
            {
                    $stmt = $this->conn->prepare("SELECT * FROM transaction WHERE txid = ? ");
                    $stmt->bind_param("s",$txid);
                    $stmt->execute();
                    $stmt->bind_result($txid,$phone_no,$amount,$status,$date,$uid,$recharge);
                    while($stmt->fetch());
                    $stmt->close;
					$trans["txid"] = $txid;
					$trans["phone_no"] = $phone_no;
                                        $trans["amount"] = $amount;
					$trans["status"]= $status;
					$trans["date"]= $date;
					$trans["uid"]=$uid;
					$trans["recharge"]=$recharge;
                                        return $trans;
            }
            else
            {
                return false;
            }

        }
		public function getTransaction($txid)
		{
			$stmt = $this->conn->prepare("SELECT * FROM transaction WHERE txid = ?");
 
			$stmt->bind_param("s", $txid);
 
        if ($stmt->execute()) {
            $stmt->bind_result($txid,$phone_no,$amount,$status,$date,$uid,$recharge);
                while($stmt->fetch());
            $stmt->close;
			$trans["txid"] = $txid;
			$trans["phone_no"] = $phone_no;
            $trans["amount"] = $amount;
			$trans["status"]= $status;
			$trans["date"]= $date;
			$trans["uid"]=$uid;
                        $trans["recharge"]=$recharge;
			return $trans;
			
		}
		else{return false;}
		}
		public function updateTransaction($txid,$status)
		{
			$stmt = $this->conn->prepare("UPDATE transaction SET status = ? WHERE txid = ?");
			$stmt->bind_param("ss",$status,$txid);
			if ($stmt->execute()) {
                             $stmt->close;
		}
		else{
			return false;
		}
		}
		
}
?>