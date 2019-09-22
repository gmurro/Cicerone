<?php

class connectionEMail
{
    private $username = 'mminds404@gmail.com';
    private $password = 'MadMinds!98';

    public function __construct(){
    }

    public function getUsername(){
        return $this->username;
    }
    public function getPassword(){
        return $this->password;
    }

}