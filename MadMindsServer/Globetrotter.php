<?php

include_once 'db_connect.php';

class Globetrotter{

    private $db;
    private $db_table = "Globetrotter";
    private $salt_length=16;

    private $id;
    private $email;
    private $password;
    private $nome;
    private $cognome;
    private $dataNascita;
    private $cellulare;
    private $foto;



    public function __construct(){
        $this->db = new DbConnect();
    }


    private function leggiSalt($email){

        //Get a unique Salt
        $salt = "null";

        $query = "select salt from ".$this->db_table." where email = '$email'";
        $result = mysqli_query($this->db->getDb(), $query);

        if(mysqli_num_rows($result) > 0) {
            while ($row = $result->fetch_assoc()) {
                $salt = $row["salt"];
            }
            return $salt;
        }
        return $salt;
    }

    public function isCicerone($id){

        $query = "select codCicerone from Ciceroni where codCicerone = ".$id." AND attivo = true";
        $result = mysqli_query($this->db->getDb(), $query);

        if(mysqli_num_rows($result) > 0) {
            return true;
        }
        return false;
    }


    private function isGlobetrotterEsistente($email, $password){

        $salt=$this->leggiSalt($email);

        $query = "select codGlobetrotter, password from ".$this->db_table." where email = '".$email."'";
        $result = mysqli_query($this->db->getDb(), $query);

        if(mysqli_num_rows($result) > 0){
            $row = $result->fetch_assoc();
            $this->id = $row["codGlobetrotter"];
            $passwordSalt = $this->concatPasswordWithSalt($password,$salt);

            if (password_verify($passwordSalt, $row["password"])) {
                return true;
            } else {
                return false;
            }
        }

        return false;

    }

    private function isEmailEsistente($email){

        $query = "select * from ".$this->db_table." where email = '".$email."'";
        $result = mysqli_query($this->db->getDb(), $query);

        if(mysqli_num_rows($result) > 0){
            return true;
        }
        return false;
    }

    private function isEmailVadida($email){
        return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
    }

    private function getSalt($salt_length){
        return bin2hex(openssl_random_pseudo_bytes($salt_length));
    }



    /**
     * Creates password hash using the Salt and the password
     *
     * @param $password
     * @param $salt
     *
     * @return
     */
    private function concatPasswordWithSalt($password,$salt){
        $salt_length = strlen($salt);
        if($salt_length % 2 == 0){
            $mid = $salt_length / 2;
        }
        else{
            $mid = ($salt_length - 1) / 2;
        }

        return substr($salt,0,$mid - 1).$password.substr($salt,$mid,strlen($salt) - 1);
    }

    public function registraNuovoGlobetrotter($email, $password, $nome, $cognome, $dataNascita){

        $this->email = $email;
        $this->password = $password;
        $this->nome = $nome;
        $this->cognome = $cognome;
        $this->dataNascita = $dataNascita;

        $isEsistente = $this->isEmailEsistente($email);

        if($isEsistente){
            $json['error'] = true;
            $json['message'] = "Errore nella registrazione. Probabilmente l'email inserita gia esiste";
        } else{

            $isEmailVadida = $this->isEmailVadida($email);

            if($isEmailVadida) {

                //Get a unique Salt
                $salt = $this->getSalt($this->salt_length);

                //Generate a unique password Hash
                $passwordHash = password_hash($this->concatPasswordWithSalt($password,$salt),PASSWORD_DEFAULT);
                $this->password = $passwordHash;    //set password attribute as

                $query = "insert into ".$this->db_table." (email, password, salt, nome, cognome, dataNascita) values ('".$this->email."', '".$this->password."', '".$salt."', '".$this->nome."', '".$this->cognome."', '".$this->dataNascita."')";
                $insert = mysqli_query($this->db->getDb(), $query);

                if($insert == 1){

                    $json['error'] = false;
                    $json['message'] = "Globetrotter registrato con successo";

                }else{

                    $json['error'] = true;
                    $json['message'] = "Errore nella registrazione. Probabilmente l'email gia esiste";
                }

                mysqli_close($this->db->getDb());
            }
            else{
                $json['error'] = true;
                $json['message'] = "Errore nella registrazione. Indirizzo email non valido";
            }

        }

        return $json;

    }

    public function loginGlobetrotter($email, $password){
        $json = array();

        $this->email = $email;
        $this->password = $password;

        $canGlobetrotterLogin = $this->isGlobetrotterEsistente($email, $password);

        if($canGlobetrotterLogin){
            $json['error'] = false;
            $json['message'] = "Login avvenuto con successo";
            $json['id'] = $this->id;

            if($this->isCicerone($this->id)) {
                $json['cicerone'] = true;
            } else {
                $json['cicerone'] = false;
            }
        }else{
            $json['error'] = true;
            $json['message'] = "Credenziali errate";
        }

        mysqli_close($this->db->getDb());

        return $json;
    }

    private function  isGiaCicerone($codCicerone) {

        $query = "select attivo from Ciceroni where codCicerone = ".$codCicerone."";
        $result = mysqli_query($this->db->getDb(), $query);

        if(mysqli_num_rows($result) > 0){
            return true;
        } else {
            return false;
        }


    }

    public function diventaCicerone($codCicerone, $citta, $descrizione){
        $json = array();

        if($this->isGiaCicerone($codCicerone)) {

            $queryGiaCicerone = "UPDATE Ciceroni SET attivo = true WHERE codCicerone = " . $codCicerone . "";

            $insert = mysqli_query($this->db->getDb(), $queryGiaCicerone);

            if($insert == 1){
                $json['error'] = false;
                $json['message'] = "successo";
                $json['giaCicerone'] = true;

            }else{
                $json['error'] = true;
                $json['message'] = "Errore nella registrazione.";
                $json['giaCicerone'] = false;
            }

        } else {
            $query = "INSERT INTO Ciceroni (codCicerone, descrizione, citta, dataUpgrade) VALUES ('".$codCicerone."', '".$descrizione."', '".$citta."', curdate())";

            $insert = mysqli_query($this->db->getDb(), $query);

            if($insert == 1){

                $json['error'] = false;
                $json['message'] = "Cicerone registrato con successo";
                $json['giaCicerone'] = false;

            }else{

                $json['error'] = true;
                $json['message'] = "Errore nella registrazione.";
            }
        }

        mysqli_close($this->db->getDb());
        return $json;
    }


    private function caricaFoto($path) {
        if($path == "") {
            return "null";
        } else {
            $type = pathinfo($path, PATHINFO_EXTENSION);
            $data = file_get_contents($path);

            if($data == false) {    //se la foto è stata eliminata dal fule system
                return null;
            }
            $base64 = 'data:image/' . $type . ';base64,' . base64_encode($data);

            return $base64;
        }
    }


    public function visualizzaProfilo($id){

        $query = "select nome,cognome,email,cellulare,dataNascita,foto from ".$this->db_table." where codGlobetrotter = '$id'";
        $result = mysqli_query($this->db->getDb(), $query);

        if(mysqli_num_rows($result) > 0){

            $row = $result->fetch_assoc();

            $json['error'] = false;
            $json['nome'] = $row["nome"];
            $this->nome = $row["nome"];
            $json['cognome'] = $row["cognome"];
            $this->cognome = $row["cognome"];
            $json['email'] = $row["email"];
            $this->email = $row["email"];
            $json['cellulare'] = $row["cellulare"];
            $this->cellulare = $row["cellulare"];
            $json['dataNascita'] = $row["dataNascita"];
            $this->dataNascita = $row["dataNascita"];
            $json['foto'] = $row["foto"];
            $this->foto = $row["foto"];

            //$json['foto'] =$this->caricaFoto($row["foto"]);

        } else {
            $json['error'] = true;
            $json['message'] = "ID inesistente!";
        }

        mysqli_close($this->db->getDb());

        return $json;

    }

    public function visualizzaPortafoglio($id){

        $query = "select portafoglio from ".$this->db_table." where codGlobetrotter = '$id'";
        $result = mysqli_query($this->db->getDb(), $query);

        if(mysqli_num_rows($result) > 0){
            $row = $result->fetch_assoc();
            return $row["portafoglio"];
        }
        return 0;

    }

    public function modificaPasswordGlobetrotter($password, $nuovapassword, $id){

        if ($this->passwordEsistente($password,$id)){

            $salt = $this->getSalt($this->salt_length);
            $passwordHash = password_hash($this->concatPasswordWithSalt($nuovapassword,$salt),PASSWORD_DEFAULT);

            $query = "update ".$this->db_table." set password = '".$passwordHash."' , salt = '".$salt."' where codGlobetrotter = '".$id."'";
            $insert = mysqli_query($this->db->getDb(), $query);

            if($insert == 1){

                $json['error'] = false;
                $json['message'] = "Modifiche avvenute con successo";

            }else{

                $json['error'] = true;
                $json['message'] = "Errore nella modifica.";
            }

        }else {

            $json['error'] = true;
            $json['message'] = "Errore nella modifica della password. Password attuale errata!";
        }


        mysqli_close($this->db->getDb());

        return $json;

    }



    private function passwordEsistente($password, $id){


        $query = "select password,salt from ".$this->db_table." where codGlobetrotter = '$id'";
        $result = mysqli_query($this->db->getDb(), $query);

        if(mysqli_num_rows($result) > 0) {

            $row = $result->fetch_assoc();

            $salt = $row ["salt"];
            $passwordSalt = $this->concatPasswordWithSalt($password, $salt);

            if (password_verify($passwordSalt, $row["password"])) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    public function modificaProfiloGlobetrotter($nome, $cognome, $dataNascita, $cellulare, $email, $id){

        $isEmailVadida = $this->isEmailEsistente($email);

        if($isEmailVadida) {

            $query = "update ".$this->db_table." set email = '".$email."', cellulare = '".$cellulare."', nome = '".$nome."', cognome = '"
                .$cognome."', dataNascita = '".$dataNascita."' where codGlobetrotter = ".$id."";
            $insert = mysqli_query($this->db->getDb(), $query);

            if($insert == 1){

                $json['error'] = false;
                $json['message'] = "Modifiche avvenute con successo";

            }else{

                $json['error'] = true;
                $json['message'] = "Errore nella modifica.";
            }

            mysqli_close($this->db->getDb());
        }
        else{
            $json['error'] = true;
            $json['message'] = "Errore nella modifica. Indirizzo email non valido";
        }

        return $json;

    }


    public function modificaFotoGlobetrotter($path, $id){

        $query = "update ".$this->db_table." set foto = '".$path."' where codGlobetrotter = '".$id."' ";
        $insert = mysqli_query($this->db->getDb(), $query);

        if($insert == 1){

            $json['error'] = false;
            $json['message'] = "Modifiche avvenute con successo";

        }else{

            $json['error'] = true;
            $json['message'] = "Errore nella modifica.";
        }

        mysqli_close($this->db->getDb());

        return $json;
    }

    public function ricaricaPortafoglio($codGlobetrotter, $importo) {
        $saldo = $this->visualizzaPortafoglio($codGlobetrotter);
        $totale = $saldo+$importo;
        $query = "update ".$this->db_table." set portafoglio = ".$totale." where codGlobetrotter = '".$codGlobetrotter."' ";
        $update = mysqli_query($this->db->getDb(), $query);

        if($update == 1){
            return true;
        }
        return false;
    }


}
?>