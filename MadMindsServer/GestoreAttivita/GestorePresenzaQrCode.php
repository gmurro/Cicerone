<?php
require_once '../Prenotazione.php';

$response = array();
if($_POST['codPrenotazione'] && $_POST['codCicerone']){
    $codPrenotazione= $_POST['codPrenotazione'];
    $codCicerone= $_POST['codCicerone'];

    $prenotazione = new Prenotazione();

        if($prenotazione->convalidaPresenzaQRCode($codPrenotazione,$codCicerone)==true) {
            $response['error'] = false;
            $response['message'] = "Presenza convalidata";
        } else {
            $response['error'] = true;
            $response['message'] = "Errore nella convalida della presenza. Prenotazione inesistente o non appartenente alle tue attivita";
        }

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>