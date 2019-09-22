<?php

require_once '../Attivita.php';
$response = array();
$attivita = new Attivita();

if($_POST['add'] == "addAttivita"){
    if($_POST['creatore'] && $_POST['titolo'] && $_POST['descrizione'] && $_POST['latiApp'] && $_POST['longApp'] && $_POST['oraApp'] && $_POST['lingua']
        && $_POST['citta'] && $_POST['regione'] && $_POST['maxPartecipanti'] && $_POST['luogo'] && $_POST['scadenzaPrenotazione'] && $_POST['data']){

        $idCreatore = $_POST['creatore'];
        $titolo = $_POST['titolo'];
        $desc = $_POST['descrizione'];
        $lat = $_POST['latiApp'];
        $long = $_POST['longApp'];
        $ora = $_POST['oraApp'];
        $lingua = $_POST['lingua'];
        $citta = $_POST['citta'];
        $regione = $_POST['regione'];
        $maxPart = $_POST['maxPartecipanti'];
        $img = $_POST['luogo'];
        $scadenzaPren = $_POST['scadenzaPrenotazione'];
        $data = $_POST['data'];

        $pathFile = "img/".$idCreatore."_".uniqid().".jpg";

        if( salvaImmagine($pathFile, $img) ) {
            $pathFile = "https://madminds.tk/GestoreAttivita/" . $pathFile;
            $response = $attivita-> inserisciAttivita($idCreatore, $titolo, $desc, $lat, $long, $ora, $lingua, $citta, $regione, $maxPart, $pathFile, $scadenzaPren, $data);
        } else {
            $response['error'] = true;
            $response['message'] = "Errore nel caricamento della foto";
        }

    } else {
        $response['error'] = true;
        $response['message'] = "Parametri insufficenti";

    }
} else if ($_POST['add'] == "addFascia") {

    if($_POST['codAttivita'] && $_POST['minPartecipanti'] && $_POST['maxPartecipanti'] && $_POST['prezzo']) {

        $cod = $_POST['codAttivita'];
        $min = $_POST['minPartecipanti'];
        $max = $_POST['maxPartecipanti'];
        $prezzo = $_POST['prezzo'];

        $response = $attivita->inserisciFascePrezzo($cod, $min, $max, $prezzo);

    } else {
        $response['error'] = true;
        $response['message'] = "Parametri insufficenti";
    }

} else {
    $response['error'] = true;
    $response['message'] = "Parametro aggiunta errato";
}


header('Content-Type: application/json');
echo json_encode($response);


function salvaImmagine( $path, $img ) {

    $img = str_replace('data:image/png;base64,', '', $img);
    $img = str_replace('data:image/jpeg;base64,', '', $img);
    $img = str_replace('data:image/jpg;base64,', '', $img);
    $img = str_replace(' ', '+', $img);
    $data = base64_decode($img);
    $success = file_put_contents($path, $data);

    if($success) {
        return true;
    } else {
        return false;
    }
}