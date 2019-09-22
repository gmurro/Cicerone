<?php
require_once '../Prenotazione.php';

$response = array();
if($_POST['codPrenotazione'] && $_POST['latitGPS'] && $_POST['longitGPS']&& $_POST['latitAttivita'] && $_POST['longitAttivita']&& $_POST['dataAttivita'] && $_POST['oraAppuntamento']&& $_POST['dataCorrente'] && $_POST['oraCorrente']){
    $codPrenotazione= $_POST['codPrenotazione'];
    $latitGPS = $_POST['latitGPS'];
    $longitGPS = $_POST['longitGPS'];
    $latitAttivita = $_POST['latitAttivita'];
    $longitAttivita = $_POST['longitAttivita'];
    $dataAttivita = $_POST['dataAttivita'];
    $oraAppuntamento = $_POST['oraAppuntamento'];
    $dataCorrente = $_POST['dataCorrente'];
    $oraCorrente = $_POST['oraCorrente'];

    $prenotazione = new Prenotazione();

    $distanza = distanzaTraCoordinate($latitGPS, $longitGPS, $latitAttivita, $longitAttivita, "K")*1000;

    $diffOrario= abs(timeDiff($oraCorrente,$oraAppuntamento)/60);

     if($dataCorrente!=$dataAttivita) {
        $response['error'] = true;
        $response['message'] = "Puoi confermare la presenza solo il giorno dell'attività";
     } else if($diffOrario>30) {
        $response['error'] = true;
        $response['message'] = "Puoi confermare la presenza solo nell'orario compreso tra mezz'ora prima e mezz'ora dopo l'appuntamento";
     } else if($distanza>50){
        $response['error'] = true;
        $response['message'] = "Sei a più di 50 metri dal punto di incontro, avvicinati per confermare la presenza";
     }else {
        if($prenotazione->confermaPresenzaGPS($codPrenotazione)==true) {
            $response['error'] = false;
            $response['message'] = "Presenza confermata tramite GPS";
        } else {
            $response['error'] = true;
            $response['message'] = "Errore nella conferma della presenza";
        }
    }

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);

function distanzaTraCoordinate($lat1, $lon1, $lat2, $lon2, $unit) {
    if (($lat1 == $lat2) && ($lon1 == $lon2)) {
        return 0;
    }
    else {
        $theta = $lon1 - $lon2;
        $dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
        $dist = acos($dist);
        $dist = rad2deg($dist);
        $miles = $dist * 60 * 1.1515;
        $unit = strtoupper($unit);

        if ($unit == "K") {
            return ($miles * 1.609344);
        } else if ($unit == "N") {
            return ($miles * 0.8684);
        } else {
            return $miles;
        }
    }
}

function timeDiff($firstTime,$lastTime) {
    $firstTime=strtotime($firstTime);
    $lastTime=strtotime($lastTime);
    $timeDiff=$lastTime-$firstTime;
    return $timeDiff;
}
?>