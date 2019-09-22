<?php

include_once 'db_connect.php';

class Recensione
{

    private $db;
    private $db_table = "Ciceroni";
    private $salt_length = 16;

    private $idCicerone;
    private $idRecensione;
    private $idGlobetrotter;

    private $descrizione;
    private $voto;


    public function __construct()
    {
        $this->db = new DbConnect();
    }


    public function aggiungiRecensione($idCicerone, $idGlobetrotter, $voto, $descrizione)
    {

        $query = "INSERT INTO Recensioni (codGlobetrotter, codCicerone, voto, descrizione) VALUES (" . $idGlobetrotter . "," . $idCicerone . "," . $voto . ",\"" . $descrizione . "\")";
        $insert = mysqli_query($this->db->getDb(), $query);

        if ($insert == 1) {

            $json['error'] = false;
            $json['message'] = "Recensione aggiunta con successo";

        } else {

            $json['error'] = true;
            $json['message'] = "Errore nell'inserimento della Recensione";
        }

        mysqli_close($this->db->getDb());

        return $json;


    }



    public function visualizzaRecensioniCicerone($id)
    {

        $query = "SELECT nome, cognome, voto, descrizione FROM Recensioni r JOIN Globetrotter g 
                on r.codGlobetrotter = g.codGlobetrotter  WHERE codCicerone = '$id'";
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

        return $json;

    }


    public function visualizzaRecensioniGlobetrotter($id)
    {

        $query = "SELECT g.nome, g.cognome, voto, r.descrizione FROM Recensioni r JOIN Ciceroni c 
                on c.codCicerone = r.codCicerone  
                JOIN Globetrotter g on r.codCicerone = g.codGlobetrotter WHERE r.codGlobetrotter = '$id'";
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

        return $json;

    }




}