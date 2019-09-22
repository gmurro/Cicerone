<?php

include_once 'db_connect.php';

class Prenotazione
{
    private $db;
    private $db_table = "Prenotazioni";
    private $codPrenotazione;
    private $codAttivita;
    private $codGlobetrotter;
    private $nPartecipanti;
    private $importo;
    private $surplus;

    public function __construct(){
        $this->db = new DbConnect();
    }

    private function isPrenotazioneEsistente($codGlobetrotter, $codAttivita){
        $query = "select codPrenotazione from ".$this->db_table."  natural left join PrenotazioniCancellate where codGlobetrotter = $codGlobetrotter and codAttivita = $codAttivita and dataCancellazione is null";
        $result = mysqli_query($this->db->getDb(), $query);
        if(mysqli_num_rows($result) > 0){
            return true;
        }
        return false;
    }

    private function isPrenotazioneValida($codPrenotazione,$codCicerone){
        $query = "SELECT P.codPrenotazione
                  FROM Prenotazioni P LEFT JOIN PrenotazioniCancellate PC ON P.codPrenotazione=PC.codPrenotazione 
                  INNER JOIN Presenze PR ON  P.codPrenotazione = PR.codPrenotazione
                  INNER JOIN Attivita A ON P.codAttivita=A.codAttivita
                  LEFT JOIN AttivitaCancellate AC ON A.codAttivita = AC.codAttivita 
                  INNER JOIN Globetrotter C ON creatore=C.codGlobetrotter
                  WHERE P.codPrenotazione='$codPrenotazione' AND A.creatore ='$codCicerone' AND PC.dataCancellazione IS NULL AND AC.dataCancellazione IS NULL";
        $result = mysqli_query($this->db->getDb(), $query);
        if(mysqli_num_rows($result) > 0){
            return true;
        }
        return false;
    }

    public function prenotaAttivita($codGlobetrotter, $codAttivita, $nPartecipanti, $importo, $surplus, $saldoAggiornato, $creatore, $admin, $saldoAggiornatoCicerone, $saldoAggiornatoAdmin) {
        if(!$this->isPrenotazioneEsistente($codGlobetrotter, $codAttivita)) {
            mysqli_query($this->db->getDb(), "START TRANSACTION");
            $query = "insert into ".$this->db_table." (codAttivita, codGlobetrotter, nPartecipanti, importo, surplus) values ($codAttivita, $codGlobetrotter, $nPartecipanti, $importo, $surplus)";
            $insert = mysqli_query($this->db->getDb(), $query);

            $query2 = "update Globetrotter set portafoglio = $saldoAggiornato where codGlobetrotter = $codGlobetrotter";
            $update = mysqli_query($this->db->getDb(), $query2);

            $query3 = "update Globetrotter set portafoglio = $saldoAggiornatoCicerone where codGlobetrotter = $creatore";
            $update2 = mysqli_query($this->db->getDb(), $query3);

            $query4 = "update Amministrazione set portafoglio = $saldoAggiornatoAdmin where email = '".$admin."'";
            $update3 = mysqli_query($this->db->getDb(), $query4);

            if($insert == 1 && $update==1 && $update2==1 && $update3==1){
                mysqli_query($this->db->getDb(), "COMMIT");
                $json['error'] = false;
                $json['message'] = "Prenotazione effettuata con successo";

            }else{

                $json['error'] = true;
                $json['message'] = "Errore nella prenotazione";
            }
            return $json;
        } else {
            $json['error'] = true;
            $json['message'] = "Attenzione, ti sei già prenotato a questa attività!";
            return $json;
        }
    }

    public function getCodPrenotazione($codGlobetrotter, $codAttivita) {
        $query = "select codPrenotazione from ".$this->db_table." NATURAL LEFT JOIN PrenotazioniCancellate where codAttivita=$codAttivita and codGlobetrotter=$codGlobetrotter and dataCancellazione IS NULL";
        $result = mysqli_query($this->db->getDb(), $query);
        if(mysqli_num_rows($result) > 0) {
            $row = $result->fetch_assoc();
            return $row["codPrenotazione"];
        }
        return null;
    }

    public function creaPresenza ($codPrenotazione) {
        $query = "insert into Presenze (codPrenotazione) values ($codPrenotazione)";
        $insert = mysqli_query($this->db->getDb(), $query);

        if($insert == 1) {
            return true;
        }
        return false;
    }

    public function confermaPresenzaGPS ($codPrenotazione) {
        $query = "update Presenze set presenzaGps = 1 where codPrenotazione='$codPrenotazione'";
        $update = mysqli_query($this->db->getDb(), $query);

        if($update == 1) {
            return true;
        }
        return false;
    }

    public function convalidaPresenzaQRCode ($codPrenotazione, $codCicerone) {

        if(!$this->isPrenotazioneValida($codPrenotazione, $codCicerone)) {
            return false;
        }
        $query = "update Presenze set presenzaQrCode = 1 where codPrenotazione='$codPrenotazione'";
        $update = mysqli_query($this->db->getDb(), $query);

        if($update == 1) {
            return true;
        }
        return false;
    }

    public function visualizzaPrenotazioni ($codGlobetrotter) {
        $query = "SELECT P.codPrenotazione, P.codAttivita, nPartecipanti, importo, surplus, presenzaGps, presenzaQrCode, PC.dataCancellazione AS dataCancellazionePrenotazione, nome, cognome, creatore, titolo, descrizione, lingua, citta, regione, maxPartecipanti, luogo, scadenzaPrenotazione, latitAppuntamento, longAppuntamento, data, oraAppuntamento, AC.dataCancellazione AS dataCancellazioneAttivita 
                  FROM Prenotazioni P LEFT JOIN PrenotazioniCancellate PC ON P.codPrenotazione=PC.codPrenotazione 
                  INNER JOIN Presenze PR ON  P.codPrenotazione = PR.codPrenotazione
                  INNER JOIN Attivita A ON P.codAttivita=A.codAttivita
                  LEFT JOIN AttivitaCancellate AC ON A.codAttivita = AC.codAttivita 
                  INNER JOIN Globetrotter C ON creatore=C.codGlobetrotter
                  WHERE P.codGlobetrotter='$codGlobetrotter'";

        $result = mysqli_query($this->db->getDb(), $query);

        if(mysqli_num_rows($result) > 0) {
            $righe = array();
            while ($row = $result->fetch_assoc()) {
                $righe[] = $row;
            }
            mysqli_close($this->db->getDb());
            $json = array('prenotazione' => $righe);
        }
        mysqli_close($this->db->getDb());
        return $json;
    }

    public function annullaPrenotazione ($codPrenotazione, $motivo, $codGlobetrotter, $codAttivita,  $saldoAggiornatoGlobetrotter, $creatore, $admin, $saldoAggiornatoCicerone, $saldoAggiornatoAdmin) {
        $dataCancellazione = date('Y-m-d');

        mysqli_query($this->db->getDb(), "START TRANSACTION");
        $query = "INSERT INTO PrenotazioniCancellate (codPrenotazione, motivo, dataCancellazione) VALUES ('$codPrenotazione', '$motivo', '$dataCancellazione');";
        $insert = mysqli_query($this->db->getDb(), $query);

        $query2 = "update Globetrotter set portafoglio = $saldoAggiornatoGlobetrotter where codGlobetrotter = $codGlobetrotter";
        $update = mysqli_query($this->db->getDb(), $query2);

        $query3 = "update Globetrotter set portafoglio = $saldoAggiornatoCicerone where codGlobetrotter = $creatore";
        $update2 = mysqli_query($this->db->getDb(), $query3);

        $query4 = "update Amministrazione set portafoglio = $saldoAggiornatoAdmin where email = '".$admin."'";
        $update3 = mysqli_query($this->db->getDb(), $query4);

        if($insert == 1 && $update==1 && $update2==1 && $update3==1){
            mysqli_query($this->db->getDb(), "COMMIT");
            return true;
        }else{
            return false;
        }

    }


}