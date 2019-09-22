<?php
require_once '../Attivita.php';

$response = array();
if($_POST['codAttivita'] && $_POST['nPartecipanti']){
    $codAttivita = $_POST['codAttivita'];
    $nPartecipanti = $_POST['nPartecipanti'];


    $attivita = new Attivita();
    $attivita->visualizzaAttivita($codAttivita);
    $prenotati = $attivita->contaPrenotati($codAttivita);
    $postiLiberi = $attivita->getMaxPartecipanti() - $prenotati;

    if($postiLiberi>=$nPartecipanti) {
        $response['error'] = false;
        $response['message'] = "Posti selezionati liberi, conferma prenotazione";
    } else {
        $response['error'] = true;
        $response['message'] = "Ci sono solo $postiLiberi posti prenotabili per questa attivita";
        $response['posti'] = $postiLiberi;
    }
} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>