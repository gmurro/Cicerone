<?php
require_once '../Cicerone.php';
require_once '../Globetrotter.php';
require_once '../Attivita.php';
require_once '../Amministrazione.php';

$response = array();
if($_POST['codAttivita'] && $_POST['motivo'] && $_POST['dataCancellazione']){
    $motivo = $_POST['motivo'];
    $codAttivita = $_POST['codAttivita'];
    $dataCancellazione = $_POST['dataCancellazione'];

    $globetrotter = new Globetrotter();
    $attivita = new Attivita();

    $attivita->visualizzaAttivita($codAttivita);
    $creatore = $attivita->getCreatore();
    $portafoglioCicerone = $globetrotter->visualizzaPortafoglio($creatore);

    $prenotazioni = $attivita->prenotazioniAttivita($codAttivita);
    $importoParziale = 0;
    for ($i = 0; $i < count($prenotazioni); $i++) {
        $importoParziale = $importoParziale + $prenotazioni[$i]["importo"] - round($prenotazioni[$i]["importo"]*$prenotazioni[$i]["surplus"]/100, 2);
    }

    if($portafoglioCicerone>=$importoParziale ) {
        $globetrotter = new Globetrotter();
        $amministrazione = new Amministrazione();
        $cicerone = new Cicerone();


        if ($attivita->annullaAttivita($codAttivita,$motivo,$dataCancellazione) == true) {
            for ($i = 0; $i < count($prenotazioni); $i++) {
                $ricaricaGlobetrotter = $globetrotter->ricaricaPortafoglio($prenotazioni[$i]["codGlobetrotter"],$prenotazioni[$i]["importo"]);
                $surplus = round($prenotazioni[$i]["importo"]*$prenotazioni[$i]["surplus"]/100, 2);
                $prelevaAdmin = $amministrazione->prelevaPortafoglio($amministrazione->getEmail(), $surplus);
            }
            $cicerone->prelevaDaPortafoglio($creatore,$importoParziale);

            $subject = "Annullamento attivita' a cui ti sei prenotato";
            $body1 = "</b>,<br> la informiamo che l'attività \"";
            $body2 = "\" per cui lei si era prenotato, è stata annullata dal Cicerone. Ha già ricevuto un rimborso totale.<br><br>Noi del team MadMinds le auguriamo una buona giornata";
            $attivita->inviaNotifiche($codAttivita, $subject, $body1, $body2);

            $response['error'] = false;
            $response['message'] = "Attività annullata e rimborso totale di $importoParziale € effettuato verso i Globetrotter prenotati.";
        } else {
            $response['error'] = true;
            $response['message'] = "Errore nell'annullamento dell'attività.";
        }


    } else {
        $response['error'] = true;
        $response['message'] = "Il saldo del conto non è sufficente per dare un rimborso completo di ".$importoParziale."€. Ricarica il conto e poi annulla l'attività";
    }

} else {
    $response['error'] = true;
    $response['message'] = "Riempire tutti i campi";
}
echo json_encode($response);
?>