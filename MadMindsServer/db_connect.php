<?php
define('DB_USER', "root"); // db user
define('DB_PASSWORD', "MadMindsDB!98"); // db password (mention your db password here)
define('DB_NAME', "cicerone"); // database name
define('DB_HOST', "localhost"); // db server

class DbConnect{

    private $connect;

    public function __construct(){

        $this->connect = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
        //$this->connect->set_charset("utf8");
        if (mysqli_connect_errno($this->connect)){
            echo "Unable to connect to MySQL Database: " . mysqli_connect_error();
        }
    }

    public function getDb(){
        return $this->connect;
    }
}
?>