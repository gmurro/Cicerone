<?php
require_once '../Prenotazione.php';

$response = array();
if($_POST['codGlobetrotter']){
    $codGlobetrotter = $_POST['codGlobetrotter'];

    $prenotazione = new Prenotazione();
    $response = $prenotazione->visualizzaPrenotazioni($codGlobetrotter);

} else {
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>