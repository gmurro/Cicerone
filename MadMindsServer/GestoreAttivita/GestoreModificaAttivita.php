<?php
require_once '../Attivita.php';
$response = array();

if ($_POST['codAttivita'] && $_POST['descrizione'] && $_POST['latiApp'] && $_POST['longApp'] && $_POST['oraApp']) {

    $codAttivita = $_POST['codAttivita'];
    $desc = $_POST['descrizione'];
    $lat = $_POST['latiApp'];
    $long = $_POST['longApp'];
    $ora = $_POST['oraApp'];

    $attivita = new Attivita();

    $response = $attivita->modificaAttivita($codAttivita, $desc, $lat, $long, $ora);
    $subject = "Modifica attivita' a cui e' prenotato";
    $body1 = "</b>,<br> la informiamo che la sua prenotazione per l'attività \"";
    $body2 = "\" ha subito delle variazioni.<br><br>Noi del team MadMinds le auguriamo una buona giornata";
    $attivita->inviaNotifiche($codAttivita, $subject, $body1, $body2);

    header('Content-Type: application/json');
    echo json_encode($response);
}
