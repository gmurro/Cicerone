<?php
require_once '../Prenotazione.php';
require_once '../Globetrotter.php';
require_once '../Attivita.php';
require_once '../Amministrazione.php';

$response = array();
if($_POST['codGlobetrotter'] && $_POST['codAttivita'] && $_POST['nPartecipanti'] && $_POST['importo'] && $_POST['surplus']){
    $codGlobetrotter = $_POST['codGlobetrotter'];
    $codAttivita = $_POST['codAttivita'];
    $nPartecipanti = $_POST['nPartecipanti'];
    $importo = $_POST['importo'];
    $surplus = $_POST['surplus'];

    $globetrotter = new Globetrotter();
    $portafoglio = $globetrotter->visualizzaPortafoglio($codGlobetrotter);


    $attivita = new Attivita();
    $attivita->visualizzaAttivita($codAttivita);
    $creatore = $attivita->getCreatore();
    $portafoglioCicerone = $globetrotter->visualizzaPortafoglio($creatore);
    $saldoAggiornatoCicerone = $portafoglioCicerone + ($importo - round($importo*$surplus/100,2));

    $amministrazione = new Amministrazione();
    $portafoglioAdmin = $amministrazione->visualizzaPortafoglio($amministrazione->getEmail());
    $saldoAggiornatoAdmin = $portafoglioAdmin + round($importo*$surplus/100,2);

    if($portafoglio>=$importo) {
        $saldoAggiornato = $portafoglio - $importo;
        $prenotazione = new Prenotazione();
        $response = $prenotazione->prenotaAttivita($codGlobetrotter, $codAttivita, $nPartecipanti, $importo, $surplus, $saldoAggiornato, $creatore, $amministrazione->getEmail(), $saldoAggiornatoCicerone, $saldoAggiornatoAdmin);
        $prenotazione->creaPresenza($prenotazione->getCodPrenotazione($codGlobetrotter, $codAttivita));
    } else {
        $response['error'] = true;
        $response['message'] = "Saldo non sufficente per prenotare questa attivita";
    }


} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>