<?php

include_once 'db_connect.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

// Load Composer's autoloader
require 'vendor/autoload.php';
require 'vendor/connectionEMail.php';

class Attivita
{

    private $db;
    private $db_table = "Attivita";

    private $creatore;
    private $codAttivita;
    private $titolo;
    private $descrizione;
    private $lingua;
    private $citta;
    private $regione;
    private $maxPartecipanti;
    private $luogo;
    private $scadenzaPrenotazione;
    private $data;
    private $oraAppuntamento;
    private $latitAppuntamento;
    private $longAppuntamento;

    public function __construct()
    {
        $this->db = new DbConnect();
    }

    public function getMaxPartecipanti()
    {
        return $this->maxPartecipanti;
    }

    public function getCreatore()
    {
        return $this->creatore;
    }

    public function homePage()
    {
        $query = "SELECT codAttivita, nome, cognome, creatore, titolo, descrizione, lingua, citta, regione, maxPartecipanti, luogo, scadenzaPrenotazione, latitAppuntamento, longAppuntamento, data, oraAppuntamento 
                  FROM Attivita NATURAL LEFT JOIN AttivitaCancellate INNER JOIN Globetrotter ON creatore=codGlobetrotter 
                  WHERE curdate() <= scadenzaPrenotazione AND dataCancellazione IS NULL";

        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            $righe = array();
            while ($row = $result->fetch_assoc()) {
                $righe[] = $row;
            }

            mysqli_close($this->db->getDb());
            $json = array('attivita' => $righe);
            mysqli_close($this->db->getDb());
            return $json;
        }
        mysqli_close($this->db->getDb());
    }

    public function ricercaAttivita($citta, $nPartecipanti, $dataMin, $dataMax)
    {
        $query = "";
        if ($citta == " ") {
            $query = "SELECT codAttivita, nome, cognome, creatore, titolo, descrizione, lingua, citta, regione, maxPartecipanti, luogo, scadenzaPrenotazione, latitAppuntamento, longAppuntamento, data, oraAppuntamento 
                  FROM Attivita NATURAL LEFT JOIN AttivitaCancellate INNER JOIN Globetrotter ON creatore=codGlobetrotter 
                  WHERE curdate() <= scadenzaPrenotazione AND dataCancellazione IS NULL AND maxPartecipanti>=$nPartecipanti AND '$dataMin'<=data AND '$dataMax'>=data";

        } else {
            $query = "SELECT codAttivita, nome, cognome, creatore, titolo, descrizione, lingua, citta, regione, maxPartecipanti, luogo, scadenzaPrenotazione, latitAppuntamento, longAppuntamento, data, oraAppuntamento 
                  FROM Attivita NATURAL LEFT JOIN AttivitaCancellate INNER JOIN Globetrotter ON creatore=codGlobetrotter 
                  WHERE curdate() <= scadenzaPrenotazione AND dataCancellazione IS NULL AND citta = '$citta' AND maxPartecipanti>=$nPartecipanti AND '$dataMin'<=data AND '$dataMax'>=data";

        }

        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            $righe = array();
            while ($row = $result->fetch_assoc()) {
                $righe[] = $row;
            }

            mysqli_close($this->db->getDb());
            $json = array('attivita' => $righe);
            mysqli_close($this->db->getDb());
            return $json;
        }
        mysqli_close($this->db->getDb());
    }

    public function getFasciaPrezzo($codAttivita, $nPartecipanti)
    {

        $query = "select prezzo from FascePrezzo where codAttivita = $codAttivita and minPartecipanti <= $nPartecipanti and maxPartecipanti >= $nPartecipanti";
        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            $row = $result->fetch_assoc();
            $prezzo = $row["prezzo"];
            return $prezzo;
        }
        return null;
    }

    public function getFasciePrezzi($codAttivita)
    {

        $query = "select minPartecipanti, maxPartecipanti, prezzo from FascePrezzo where codAttivita = $codAttivita ";
        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            $righe = array();
            while ($row = $result->fetch_assoc()) {
                /*
                $righe['minPartecipanti'] = $row["minPartecipanti"];
                $righe['maxPartecipanti'] = $row["maxPartecipanti"];
                $righe['prezzo'] = $row["prezzo"];
                */
                $righe[] = $row;
            }
            $json = array('prezzi' => $righe);
            $json['error'] = false;
            return $json;
        } else {
            $json['error'] = true;
            $json['message'] = "Errore nella visualizzazione della fasce di prezzo";
            return $json;
        }

    }

    public function contaPrenotati($codAttivita)
    {

        $query = "SELECT SUM(nPartecipanti) AS prenotati FROM Prenotazioni NATURAL LEFT JOIN PrenotazioniCancellate WHERE codAttivita = '$codAttivita' AND dataCancellazione IS NULL";
        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            $row = $result->fetch_assoc();
            $prenotati = $row["prenotati"];
            if ($prenotati == null) {
                $prenotati = 0;
            }
            return $prenotati;
        }
        return null;
    }


    public function visualizzaAttivita($codAttivita)
    {
        $query = "SELECT creatore, codAttivita, titolo, descrizione, lingua, citta, regione, maxPartecipanti, luogo, scadenzaPrenotazione, latitAppuntamento, longAppuntamento, data, oraAppuntamento FROM " . $this->db_table . " WHERE codAttivita = " . $codAttivita . "";
        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            $row = $result->fetch_assoc();

            $json['creatore'] = $row["creatore"];
            $this->creatore = $row["creatore"];
            $json['codAttivita'] = $row["codAttivita"];
            $this->codAttivita = $row["codAttivita"];
            $json['titolo'] = $row["titolo"];
            $this->titolo = $row["titolo"];
            $json['descrizione'] = $row["descrizione"];
            $this->descrizione = $row["descrizione"];
            $json['lingua'] = $row["lingua"];
            $this->lingua = $row["lingua"];
            $json['citta'] = $row["citta"];
            $this->citta = $row["citta"];
            $json['regione'] = $row["regione"];
            $this->regione = $row["regione"];
            $json['maxPartecipanti'] = $row["maxPartecipanti"];
            $this->maxPartecipanti = $row["maxPartecipanti"];
            $json['luogo'] = $row["luogo"];
            $this->luogo = $row["luogo"];
            $json['scadenzaPrenotazione'] = $row["scadenzaPrenotazione"];
            $this->scadenzaPrenotazione = $row["scadenzaPrenotazione"];
            $json['data'] = $row["data"];
            $this->data = $row["data"];
            $json['oraAppuntamento'] = $row["oraAppuntamento"];
            $this->oraAppuntamento = $row["oraAppuntamento"];
            $json['latitAppuntamento'] = $row["latitAppuntamento"];
            $this->latitAppuntamento = $row["latitAppuntamento"];
            $json['longAppuntamento'] = $row["longAppuntamento"];
            $this->longAppuntamento = $row["longAppuntamento"];
            return $json;
        } else {
            $json['error'] = "Attività inesistente";
            return $json;
        }
    }


    public function inserisciAttivita($creatore1, $titolo1, $descrizione1, $latiApp1, $longApp1, $oraApp1, $lingua1, $citta1, $regione1, $maxPartecipanti1, $luogo1, $scadenzaPrenotazione1, $data1)
    {

        $query = "INSERT INTO `Attivita`(`creatore`, `titolo`, `descrizione`, `latitAppuntamento`, `longAppuntamento`, `oraAppuntamento`, `lingua`, `citta`, `regione`, `maxPartecipanti`, `luogo`, `scadenzaPrenotazione`, `data`) VALUES (" . $creatore1 . ", \"" . $titolo1 . "\", \"" . $descrizione1 . "\", " . $latiApp1 . ", " . $longApp1 . ", \"" . $oraApp1 . "\", \"" . $lingua1 . "\", \"" . $citta1 . "\", \"" . $regione1 . "\", " . $maxPartecipanti1 . ", \"" . $luogo1 . "\", \"" . $scadenzaPrenotazione1 . "\", \"" . $data1 . "\")";

        $insert = mysqli_query($this->db->getDb(), $query);


        if ($insert == 1) {
            $json['error'] = false;
            $json['message'] = 'Attivita created';

            $query2 = "SELECT codAttivita FROM Attivita WHERE luogo=\"" . $luogo1 . "\"";
            $result = mysqli_query($this->db->getDb(), $query2);

            if (mysqli_num_rows($result) > 0) {
                $row = $result->fetch_assoc();
                $json['codAttivita'] = $row["codAttivita"];
            } else {
                $json['codAttivita'] = 'Cod attivita non trovato';
            }

        } else {
            $json['error'] = true;
            $json['message'] = 'Attivita not created';
        }
        mysqli_close($this->db->getDb());
        return $json;
    }

    public function inserisciFascePrezzo($codAttivita, $min, $max, $prezzo)
    {
        $query = "INSERT INTO `FascePrezzo` (`codAttivita`, `minPartecipanti`, `maxPartecipanti`, `prezzo`) VALUES
                    (" . $codAttivita . ", " . $min . ", " . $max . ", '" . $prezzo . "')";

        $insert = mysqli_query($this->db->getDb(), $query);

        if ($insert == 1) {
            $json['error'] = false;
            $json['message'] = 'Fascia inserita';
        } else {
            $json['error'] = true;
            $json['message'] = 'Fascia inserita';
        }

        mysqli_close($this->db->getDb());
        return $json;
    }


    public function modificaAttivita($codAttivita, $descrizione1, $latApp, $longApp, $orarioApp)
    {
        $query = "UPDATE Attivita SET descrizione = \"" . $descrizione1 . "\", latitAppuntamento = " . $latApp . ", longAppuntamento = " . $longApp . ", oraAppuntamento = \"" . $orarioApp . "\" WHERE codAttivita = " . $codAttivita . "";

        $insert = mysqli_query($this->db->getDb(), $query);

        if ($insert == 1) {
            $json['error'] = false;
            $json['message'] = 'Attivita modificata';
            //inviaNotifiche();
        } else {
            $json['error'] = true;
            $json['message'] = 'Errore modifica attività';
        }


        return $json;

    }

    public function attivitaPubblicate($codCicerone)
    {
        $query = "SELECT codAttivita, nome, cognome, creatore, titolo, descrizione, lingua, citta, regione, maxPartecipanti, luogo, scadenzaPrenotazione, latitAppuntamento, longAppuntamento, data, oraAppuntamento, dataCancellazione
                  FROM Attivita NATURAL LEFT JOIN AttivitaCancellate INNER JOIN Globetrotter ON creatore=codGlobetrotter 
                  WHERE   creatore=" . $codCicerone . "";

        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            $righe = array();
            while ($row = $result->fetch_assoc()) {
                $righe[] = $row;
            }

            $json = array('attivita' => $righe);
            mysqli_close($this->db->getDb());
            return $json;
        }
        mysqli_close($this->db->getDb());
    }

    public function listaPrenotati($codAttivita)
    {
        $query = "SELECT P.codPrenotazione, P.codGlobetrotter, PC.dataCancellazione AS dataCancellazionePrenotazione, nome, cognome, foto, P.nPartecipanti AS nPartecipanti
                  FROM Prenotazioni P LEFT JOIN PrenotazioniCancellate PC ON P.codPrenotazione=PC.codPrenotazione 
                  INNER JOIN Attivita A ON P.codAttivita=A.codAttivita
                  INNER JOIN Globetrotter G ON P.codGlobetrotter=G.codGlobetrotter
                  WHERE A.codAttivita='$codAttivita'";

        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            $righe = array();
            while ($row = $result->fetch_assoc()) {
                $righe[] = $row;
            }

            $json = array('prenotati' => $righe);
            mysqli_close($this->db->getDb());
            return $json;
        }
        mysqli_close($this->db->getDb());
    }

    public function inviaNotifiche($codAttivita, $subject, $body1, $body2)
    {
        //SELECT email, nome, cognome FROM Prenotazioni NATURAL JOIN Globetrotter WHERE codAttivita=48

        $query = "SELECT email, nome, cognome, titolo FROM Prenotazioni NATURAL JOIN Globetrotter NATURAL JOIN Attivita WHERE codAttivita='$codAttivita' AND codPrenotazione NOT IN (SELECT codPrenotazione FROM PrenotazioniCancellate)";

        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            //$righe = array();
            //echo "ciao1";
            while ($row = mysqli_fetch_array($result)) {
                //$righe[] = $row;
                //echo "ciao";
                //echo "".$row["email"]."".$row["nome"]."".$row["cognome"]."";

                try {

                    $conn = new connectionEMail();

                    //Server settings
                    $mail = new PHPMailer(true);
                    //$mail->SMTPDebug = 2;                                       // Enable verbose debug output
                    $mail->isSMTP();                                            // Set mailer to use SMTP
                    $mail->Host = 'smtp.gmail.com';  // Specify main and backup SMTP servers
                    $mail->SMTPAuth = true;                                   // Enable SMTP authentication
                    $mail->Username = $conn->getUsername();                     // SMTP username
                    $mail->Password = $conn->getPassword();                               // SMTP password
                    $mail->SMTPSecure = 'tls';                                  // Enable TLS encryption, `ssl` also accepted
                    $mail->Port = 587;                                    // TCP port to connect to

                    //Recipients
                    $mail->setFrom('mminds404@gmail.com', 'MadMinds Team');

                    // Content
                    $mail->isHTML(true);                                  // Set email format to HTML
                    $mail->Subject = $subject;
                    $mail->Body = "Gentile <b>".$row["nome"]." ".$row["cognome"].$body1.$row["titolo"].$body2;
                    $mail->AltBody = 'This is the body in plain text for non-HTML mail clients';
                    $mail->addAddress($row["email"]);

                    $mail->send();
                    //echo 'Message has been sent';
                } catch (Exception $e) {
                    //echo "Message could not be sent. Mailer Error: {$mail->ErrorInfo}";
                }

            }

            //$json = array('mailList' => $righe);
            //return $json;


            mysqli_close($this->db->getDb());
        }
        mysqli_close($this->db->getDb());


    }


    public function prenotazioniAttivita($codAttivita)
    {
        $query = "SELECT codPrenotazione, codGlobetrotter, codAttivita, nPartecipanti, importo, surplus
                  FROM Prenotazioni NATURAL  LEFT JOIN PrenotazioniCancellate
                  WHERE codAttivita='$codAttivita' AND dataCancellazione IS NULL";

        $result = mysqli_query($this->db->getDb(), $query);

        if (mysqli_num_rows($result) > 0) {
            $righe = array();
            while ($row = $result->fetch_assoc()) {
                $righe[] = $row;
            }

            $json = $righe;
            return $json;
        }
    }


    public function annullaAttivita ($codAttivita,  $motivo, $dataCancellazione) {

        $query = "INSERT INTO AttivitaCancellate (codAttivita, motivo, dataCancellazione) VALUES ('$codAttivita', '$motivo', '$dataCancellazione');";
        $insert = mysqli_query($this->db->getDb(), $query);
        if($insert == 1){
            return true;
        }else{
            return false;
        }
    }
}