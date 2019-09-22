<?php

include_once 'db_connect.php';
require_once 'Globetrotter.php';

class Cicerone{

    private $db;
    private $db_table = "Ciceroni";
    private $salt_length = 16;

    private $id;
    private $email;
    private $password;
    private $nome;
    private $cognome;
    private $dataNascita;
    private $cellulare;
    private $foto;

    private $descrizione;
    private $citta;
    private $dataUpgrade;


    public function __construct()
    {
        $this->db = new DbConnect();
    }


    public function isCicerone($id)
    {

        $query = "select codCicerone from Ciceroni where codCicerone = $id";
        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            return true;
        }
        return false;
    }

    private function isEmailVadida($email)
    {
        return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
    }



    public function visualizzaProfiloCompleto($id)
    {

        $query = "select nome,cognome,email,cellulare,dataNascita,foto,citta,descrizione,dataUpgrade from Globetrotter inner join  
            " . $this->db_table . " on codCicerone = codGlobetrotter where codCicerone = '$id'";
        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {

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

            $json['citta'] = $row["citta"];
            $this->citta = $row["citta"];
            $json['descrizione'] = $row["descrizione"];
            $this->descrizione = $row["descrizione"];
            $json['dataUpgrade'] = $row["dataUpgrade"];
            $this->dataUpgrade = $row["dataUpgrade"];


            //$json['foto'] =$this->caricaFoto($row["foto"]);

            //lettura delle recensioni di un cicerone
            $query = "SELECT nome, cognome, voto, descrizione FROM Recensioni r JOIN Globetrotter g 
                on r.codGlobetrotter = g.codGlobetrotter  WHERE codCicerone = $id";
            $result = mysqli_query($this->db->getDb(), $query);

            if (mysqli_num_rows($result) > 0) {

                $json['numRecensioni'] = mysqli_num_rows($result);
                $numRecensioni = 0;

                while ($row = mysqli_fetch_assoc($result)) {

                    $json['nome' . $numRecensioni] = $row['nome'];
                    $json['cognome' . $numRecensioni] = $row['cognome'];
                    $json['voto' . $numRecensioni] = $row['voto'];
                    $json['descrizione' . $numRecensioni] = $row['descrizione'];

                    $numRecensioni += 1;

                }


            } else {
                $json['numRecensioni'] = '0';
            }

        } else {
            $json['error'] = true;
            $json['message'] = "ID inesistente!";
        }

        mysqli_close($this->db->getDb());

        return $json;

    }


    public function modificaProfiloCicerone($nome, $cognome, $dataNascita, $cellulare, $email, $id, $citta, $descrizione)
    {

        $isEmailVadida = $this->isEmailVadida($email);

        if ($isEmailVadida) {

            $query = "update Globetrotter set email = '" . $email . "', cellulare = '" . $cellulare . "', nome = '" . $nome . "', cognome = '"
                . $cognome . "', dataNascita = '" . $dataNascita . "' where codGlobetrotter = " . $id . "";
            $insert = mysqli_query($this->db->getDb(), $query);

            if ($insert == 1) {

                $json['error'] = false;
                $json['message'] = "Modifiche avvenute con successo, Globetrotter";


                $query = "update " . $this->db_table . " set citta = \"" . $citta . "\", descrizione = \"" . $descrizione . "\" where codCicerone = " . $id . "";
                $insert = mysqli_query($this->db->getDb(), $query);

                if ($insert == 1) {

                    $json['error'] = false;
                    $json['message'] = "Modifiche avvenute con successo, Cicerone";

                } else {

                    $json['error'] = true;
                    $json['message'] = "Errore nella modifica. Cicerone";

                    mysqli_close($this->db->getDb());

                }

            } else {

                $json['error'] = true;
                $json['message'] = "Errore nella modifica, Globetrotter";
            }

            mysqli_close($this->db->getDb());

        } else {
            $json['error'] = true;
            $json['message'] = "Errore nella modifica. Indirizzo email non valido";
        }

        return $json;

    }

    public function eliminaProfiloCicerone($idCicerone)
    {

        $queryControllo = "SELECT titolo FROM Attivita WHERE creatore = ".$idCicerone." AND data > curdate() ";
        $result = mysqli_query($this->db->getDb(), $queryControllo);

        if (mysqli_num_rows($result) > 0) {
            $json['error'] = false;
            $json['message'] = "attivita ancora da svolgere";
            $json['attivitaDaSvolgere'] = true;
        } else {
            $query = "UPDATE Ciceroni SET attivo = false WHERE codCicerone = " . $idCicerone . "";
            $insert = mysqli_query($this->db->getDb(), $query);

            if ($insert == 1) {

                $json['error'] = false;
                $json['message'] = "non sei più Cicerone";
                $json['attivitaDaSvolgere'] = false;

            } else {

                $json['error'] = true;
                $json['message'] = "Errore nella eliminazione del Cicerone";
            }
        }

        mysqli_close($this->db->getDb());

        return $json;

    }


    public function prelevaDaPortafoglio($codCicerone, $importo) {

        $saldo = (new Globetrotter())->visualizzaPortafoglio($codCicerone);
        $totale = $saldo-$importo;
        if($totale<0) {
            return false;
        }
        $query = "update Globetrotter set portafoglio = ".$totale." where codGlobetrotter = '$codCicerone' ";
        $insert = mysqli_query($this->db->getDb(), $query);

        if($insert == 1){
            return true;
        }
        return false;
    }

    public function visualizzaGuadagno($codCicerone) {

        $query = "SELECT SUM(importo-surplus*importo/100) AS guadagno
                  FROM Prenotazioni P LEFT JOIN PrenotazioniCancellate PC ON P.codPrenotazione=PC.codPrenotazione 
                  INNER JOIN Attivita A ON P.codAttivita=A.codAttivita
                  LEFT JOIN AttivitaCancellate AC ON A.codAttivita = AC.codAttivita 
                  WHERE A.creatore='$codCicerone' AND PC.dataCancellazione IS NULL AND AC.dataCancellazione IS NULL";
        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            $row = $result->fetch_assoc();
            if($row["guadagno"]==null) {
                return 0;
            }
            return $row["guadagno"];
        } else {
            return 0;
        }
    }





}