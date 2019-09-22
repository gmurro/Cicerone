<?php
include_once 'db_connect.php';

class Amministrazione
{
    private $email="madminds@gmail.com";
    private $db;
    private $db_table = "Amministrazione";

    public function __construct()
    {
        $this->db = new DbConnect();
    }

    public function getEmail(){
        return $this->email;
    }

    public function isAmministrazioneEsistente($email, $password)
    {

        $query = "select * from ".$this->db_table." where email = '$email' AND password = '$password' Limit 1";
        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            mysqli_close($this->db->getDb());
            return true;
        }

        mysqli_close($this->db->getDb());
        return false;

    }

    public function visualizzaSurplus($email)
    {
        $query = " select surplus from ".$this->db_table." where email = '$email'";
        $result = mysqli_query($this->db->getDb(), $query);
        if(mysqli_num_rows($result) > 0){
            $row = $result->fetch_assoc();
            $surplus = $row["surplus"];
            return $surplus;
        }
        mysqli_close($this->db->getDb());
        return 0;
    }

    public function visualizzaSalvadanaio($email)
    {
        $query = "select salvadanaio from ".$this->db_table ." where email = '$email'";
        $result = mysqli_query($this->db->getDb(), $query);
        if(mysqli_num_rows($result) > 0){
            $row = $result->fetch_assoc();
            $salvadanaio = $row['salvadanaio'];
            return $salvadanaio;
        }
        mysqli_close($this->db->getDb());
        return 0;
    }

    public function visualizzaPortafoglio($email)
    {
        $query = "select portafoglio from ".$this->db_table ." where email = '$email'";
        $result = mysqli_query($this->db->getDb(), $query);
        if(mysqli_num_rows($result) > 0){
            $row = $result->fetch_assoc();
            $portafoglio = $row['portafoglio'];
            return $portafoglio;
        }
        return 0;
    }

    public function modificaSurplus($email,$nuovoSurplus){
        $query = "update ".$this->db_table ." set surplus = $nuovoSurplus where email = '$email'";
        $update = mysqli_query($this->db->getDb(), $query);
        if($update == 1){
            return true;
        }
        return false;
    }

    public function prelevaPortafoglio($email,$importo){
        $saldo = $this->visualizzaPortafoglio($email);
        $totale = $saldo-$importo;
        $query = "update ".$this->db_table ."set portafoglio = $totale where email = '$email'";
        $update = mysqli_query($this->db->getDb(), $query);
        if($update == 1){
            return true;
        }
        return false;
    }

    public function visualizzaCiceroniPerCitta(){
        $query = "select count(codCicerone) as nCiceroni,citta from Ciceroni where attivo = 1 group by citta order by nCiceroni desc";
        $result = mysqli_query($this->db->getDb(), $query);
        $citta = array();
        $nCiceroni = array();
        if(mysqli_num_rows($result) > 0){
            $i=0;
            while ($row = $result->fetch_assoc()) {
                $citta[$i] = $row["citta"];
                $nCiceroni[$i] = $row["nCiceroni"];
                $i++;
            }
            $array = array('citta' => $citta, 'nCiceroni' => $nCiceroni);
            return $array;
        }
        return 0;
    }

    public function visualizzaAttivitaPerCitta(){
        $query = "select count(codAttivita) as nAttivita,citta from Attivita natural left join AttivitaCancellate where dataCancellazione is null group by citta order by nAttivita desc";
        $result = mysqli_query($this->db->getDb(), $query);
        $citta2 = array();
        $nAttivita = array();
        if(mysqli_num_rows($result) > 0){
            $i=0;
            while ($row = $result->fetch_assoc()) {
                $citta2[$i] = $row["citta"];
                $nAttivita[$i] = $row["nAttivita"];
                $i++;
            }
            $array = array('citta2' => $citta2, 'nAttivita' => $nAttivita);
            return $array;
        }
        return 0;
    }
}
?>