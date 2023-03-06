<?php
 
/**
 * A class file to connect to database
 */
class DB_CONNECT {
    private $con; 
    // constructor
    function __construct() {
        // connecting to database
        $this->connect();
    }
 
    // destructor
    function __destruct() {
        // closing db connection
        $this->close();
    }
 
    /**
     * Function to connect with database
     */
    function connect() {
        // import database connection variables
        require_once  './db_config.php';
 
        // Connecting to mysql database
        $this->con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD,DB_DATABASE); 
        if (mysqli_connect_errno())
        {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }
 
        // Selecing database
       // $db = mysql_select_db(DB_DATABASE) or die(mysql_error()) or die(mysql_error());
 
        // returing connection cursor
        return $this->con;
    }
 
    /**
     * Function to close db connection
     */
    function close() {
        // closing db connection
        //mysqli_close($this->con);
    }
 
}
 
?>
