<?php
require_once '../Prenotazione.php';
require_once '../Globetrotter.php';
require_once '../Attivita.php';
require_once '../Amministrazione.php';

$response = array();
if($_POST['codGlobetrotter'] && $_POST['codPrenotazione'] && $_POST['motivo'] && $_POST['codAttivita'] && $_POST['importo'] && $_POST['surplus'] && $_POST['scadenzaPrenotazioni'] && $_POST['dataAttivita']){
    $codGlobetrotter = $_POST['codGlobetrotter'];
    $codPrenotazione = $_POST['codPrenotazione'];
    $motivo = $_POST['motivo'];
    $codAttivita = $_POST['codAttivita'];
    $importo = $_POST['importo'];
    $surplus = $_POST['surplus'];
    $scadenzaPrenotazioni = $_POST['scadenzaPrenotazioni'];
    $dataAttivita = $_POST['dataAttivita'];

    $globetrotter = new Globetrotter();
    $portafoglioGlobetrotter = $globetrotter->visualizzaPortafoglio($codGlobetrotter);

    $attivita = new Attivita();
    $attivita->visualizzaAttivita($codAttivita);
    $creatore = $attivita->getCreatore();
    $portafoglioCicerone = $globetrotter->visualizzaPortafoglio($creatore);

    $amministrazione = new Amministrazione();
    $portafoglioAdmin = $amministrazione->visualizzaPortafoglio($amministrazione->getEmail());

    if($portafoglioCicerone>=($importo - round($importo*$surplus/100, 2)) ) {
        $prenotazione = new Prenotazione();
        $dataCancellazione = date('Y-m-d');

        if($dataCancellazione<=$scadenzaPrenotazioni){
            //rimborso totale
            $saldoAggiornatoGlobetrotter = $portafoglioGlobetrotter + $importo;
            $saldoAggiornatoCicerone = $portafoglioCicerone - ($importo - round($importo*$surplus/100, 2));
            $saldoAggiornatoAdmin = $portafoglioAdmin - round($importo*$surplus/100, 2);

            $isAnnullata = $prenotazione->annullaPrenotazione($codPrenotazione, $motivo, $codGlobetrotter, $codAttivita,  $saldoAggiornatoGlobetrotter, $creatore, $amministrazione->getEmail(), $saldoAggiornatoCicerone, $saldoAggiornatoAdmin);
            if($isAnnullata == true) {
                $response['error'] = false;
                $response['message'] = "Prenotazione annullata e rimborso totale di $importo € effettuato.";
            } else {
                $response['error'] = true;
                $response['message'] = "Errore nell'annullamento della prenotazione.";
            }
        } else if($dataCancellazione>$scadenzaPrenotazioni && $dataCancellazione<=$dataAttivita) {
            //rimborso parziale
            $importo = round($importo/2,2);
            $saldoAggiornatoGlobetrotter = $portafoglioGlobetrotter + $importo;
            $saldoAggiornatoCicerone = $portafoglioCicerone - $importo - round($importo*$surplus/100,2);
            $saldoAggiornatoAdmin = $portafoglioAdmin - round($importo*$surplus/100,2);

            $isAnnullata = $prenotazione->annullaPrenotazione($codPrenotazione, $motivo, $codGlobetrotter, $codAttivita,  $saldoAggiornatoGlobetrotter, $creatore, $amministrazione->getEmail(), $saldoAggiornatoCicerone, $saldoAggiornatoAdmin);
            if($isAnnullata == true) {
                $response['error'] = false;
                $response['message'] = "Prenotazione annullata e rimborso parziale di $importo € effettuato.";
            } else {
                $response['error'] = true;
                $response['message'] = "Errore nell'annullamento della prenotazione.";
            }
        } else {
            //errore
            $response['error'] = true;
            $response['message'] = "La data dell'attività è passata, non puoi più annullare la prenotazione.";
        }

    } else {
        $response['error'] = true;
        $response['message'] = "Purtroppo non è stato possibile annullare la prenotazione ed avere un rimborso";
    }

} else {
    $response['error'] = true;
    $response['message'] = "Riempire tutti i campi";
}
echo json_encode($response);
?>