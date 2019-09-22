<?php

    require_once '../Amministrazione.php';
    $amministratore = new Amministrazione();
    $array = $amministratore->visualizzaCiceroniPerCitta();

    echo json_encode($array['nCiceroni']);
    echo json_encode($array['citta']);

?>
