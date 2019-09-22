<!doctype html>
<html>

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Language" content="en">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.6.0/Chart.min.js"></script>
    <title>AdminHome</title>
    <link rel="icon" href="https://www.madminds.tk/VistaAmministrazione/logoSenzaSfondo.png" />
    <meta name="theme-color" content="#298282">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no, shrink-to-fit=no" />
    <meta name="description" content="This is an example dashboard created using build-in elements and components.">
    <meta name="msapplication-tap-highlight" content="no">
    <!--
    =========================================================
    * ArchitectUI HTML Theme Dashboard - v1.0.0
    =========================================================
    * Product Page: https://dashboardpack.com
    * Copyright 2019 DashboardPack (https://dashboardpack.com)
    * Licensed under MIT (https://github.com/DashboardPack/architectui-html-theme-free/blob/master/LICENSE)
    =========================================================
    * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
    -->
    <link href="main.css" rel="stylesheet">
</head>
<body>
<div class="app-container app-theme-white fixed-header" >
    <div class="app-header header-shadow"  style = "background-color: #298282">
        <div class="app-header__logo" style = "background-color: #298282">
            <img src="https://madminds.tk/VistaAmministrazione/images/logoOrizzontale.png" style="width:120px"/>
        </div>
        <div class="app-header__menu">
                <span>
                    <button type="button" class="btn-icon btn-icon-only btn btn-primary btn-sm mobile-toggle-header-nav" style="background-color: #298282; border-color: #298282">
                        <span class="btn-icon-wrapper" >
                            <i class="fa fa-ellipsis-v fa-w-6" ></i>
                        </span>
                    </button>
                </span>
        </div>
        <div class="app-header__content">
            <div class="app-header-left">
            </div>
            <div class="app-header-right">
                <div class="header-btn-lg pr-0">
                    <div class="widget-content p-0">
                        <div class="widget-content-wrapper">
                            <div class="widget-content-left">
                                <div class="btn-group">
                                    <button type="button" tabindex="0" class="dropdown-item" class="page-title-icon" style="background-color: #e38800; class="btn btn-primary">
                                    <a href="http://www.madminds.tk/GestoreAmministrazione/GestoreLogoutAmministrazione.php">
                                        <font  color="white" >LOGOUT</font>
                                    </a>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="app-main">
        <div class="app-main__outer">
            <div class="app-main__inner">
                <div class="app-page-title">
                    <div class="page-title-wrapper">
                        <div class="page-title-heading">
                            <?php
                            require_once '../Amministrazione.php' ;
                            $amministratore = new Amministrazione();
                            $email = $amministratore->getEmail();
                            echo "Benvenuto ".$email;
                            ?>

                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 col-xl-4">
                        <div class="card mb-3 widget-content bg-midnight-bloom">
                            <div class="widget-content-wrapper text-white">
                                <div class="widget-content-left">
                                    <div class="widget-heading">Surplus</div>
                                    <div class="widget-subheading">
                                        Percentuale corrente da applicare alle singoli transazioni.
                                        <br></br>

                                        <form action="../GestoreAmministrazione/GestoreModificaSurplus.php?email= <?php echo $_GET['email'] ?>" class="form-inline" method="post">
                                            <div class="form-group">
                                                <label class="sr-only" for="exampleInputAmount">Surplus</label>
                                                <div class="input-group">
                                                    <input type="text" class="form-control" placeholder="Inserisci nuovo surplus" name="nuovoSurplus">
                                                </div>
                                            </div>
                                            <button type="submit" class="btn btn-primary" name="btInvia">INVIA</button>
                                        </form>
                                        <b>
                                            <?php
                                                echo $_GET['messaggio'];
                                            ?>
                                        </b>
                                    </div>
                                </div>
                                <div class="widget-content-right">
                                    <div class="widget-numbers text-white" style="padding-bottom: 40%;">
                                                <span>
                                                   <?php
                                                   require_once '../Amministrazione.php';
                                                   $amministratore = new Amministrazione();
                                                   $email = $amministratore->getEmail();
                                                   $surplus = $amministratore->visualizzaSurplus($email);
                                                   echo $surplus."%";
                                                   ?>
                                                </span>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 col-xl-4">
                        <div class="card mb-3 widget-content bg-grow-early">
                            <div class="widget-content-wrapper text-white">
                                <div class="widget-content-left">
                                    <div class="widget-heading">Portafoglio</div>
                                    <div class="widget-subheading">Importo ricavato dalle transazioni concluse.</div>
                                </div>
                                <div class="widget-content-right">
                                    <div class="widget-numbers text-white">
                                                <span>
                                                    <?php
                                                    require_once '../Amministrazione.php';
                                                    $amministratore = new Amministrazione();
                                                    $email = $amministratore->getEmail();
                                                    $portafoglio = $amministratore->visualizzaPortafoglio($email);
                                                    echo $portafoglio."€";
                                                    ?>
                                                </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 col-lg-6">
                        <div class="mb-3 card">
                            <div class="card-header-tab card-header-tab-animation card-header">
                                <div class="card-header-title">
                                    <i class="header-icon lnr-apartment icon-gradient bg-love-kiss"> </i>
                                    Città con più ciceroni
                                </div>
                            </div>
                            <div class="card-body">
                                <div class="tab-content">
                                    <div class="tab-pane fade show active" id="tabs-eg-77">
                                        <div class="card mb-3 widget-chart widget-chart2 text-left w-100">
                                            <div class="widget-chat-wrapper-outer">
                                                <div class="widget-chart-wrapper widget-chart-wrapper-lg opacity-10 m-0">
                                                    <canvas id="myCanvas" style="display: block; height: 321px; width: 643px;" width="1286" height="800" class="chartjs-render-monitor" ></canvas>
                                                    <script>
                                                        let myCanvas = document.getElementById("myCanvas").getContext('2d');

                                                        <?php

                                                        require_once '../Amministrazione.php';
                                                        $amministratore = new Amministrazione();
                                                        $array = $amministratore->visualizzaCiceroniPerCitta();
                                                        ?>
                                                        let myLabels = [<?php
                                                            foreach($array['citta'] as $citta) {
                                                                echo "\"$citta\", " ;
                                                            }
                                                        ?>];
                                                        let mydata = [<?php
                                                            foreach($array['nCiceroni'] as $numeroCiceroni) {
                                                                echo "$numeroCiceroni, " ;
                                                            }
                                                            ?>];


                                                        let chart = new Chart(myCanvas, {
                                                            type: 'bar',
                                                            data: {
                                                                labels:myLabels,
                                                                datasets: [{
                                                                    label: 'Numero di ciceroni',
                                                                    data: mydata,
                                                                    backgroundColor: [
                                                                        'rgba(255, 99, 132, 0.2)',
                                                                        'rgba(54, 162, 235, 0.2)',
                                                                        'rgba(255, 206, 86, 0.2)',
                                                                        'rgba(75, 192, 192, 0.2)',
                                                                        'rgba(153, 102, 255, 0.2)',
                                                                        'rgba(255, 159, 64, 0.2)'
                                                                    ],
                                                                    borderColor: [
                                                                        'rgba(255, 99, 132, 1)',
                                                                        'rgba(54, 162, 235, 1)',
                                                                        'rgba(255, 206, 86, 1)',
                                                                        'rgba(75, 192, 192, 1)',
                                                                        'rgba(153, 102, 255, 1)',
                                                                        'rgba(255, 159, 64, 1)'
                                                                    ],
                                                                    borderWidth: 2
                                                                }]
                                                            },
                                                            options: {
                                                                scales: {
                                                                    yAxes: [{
                                                                        ticks: {
                                                                            beginAtZero: true
                                                                        }
                                                                    }]
                                                                }
                                                            }
                                                        });
                                                    </script>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-12 col-lg-6">
                        <div class="mb-3 card">
                            <div class="card-header-tab card-header-tab-animation card-header">
                                <div class="card-header-title">
                                    <i class="header-icon lnr-apartment icon-gradient bg-love-kiss"> </i>
                                    Citta con più attività
                                </div>
                            </div>
                            <div class="card-body">
                                <div class="tab-content">
                                    <div class="tab-pane fade show active" id="tabs-eg-77">
                                        <div class="card mb-3 widget-chart widget-chart2 text-left w-100">
                                            <div class="widget-chat-wrapper-outer">
                                                <div class="widget-chart-wrapper widget-chart-wrapper-lg opacity-10 m-0">
                                                    <canvas id="myCanvas2" style="display: block; height: 321px; width: 643px;" width="1286" height="800" class="chartjs-render-monitor" ></canvas>
                                                    <script>
                                                        let myCanvas2 = document.getElementById("myCanvas2").getContext('2d');
                                                        <?php

                                                        $array2 = $amministratore->visualizzaAttivitaPerCitta();
                                                        ?>
                                                        let myLabels2 = [<?php
                                                            foreach($array2['citta2'] as $citta2) {
                                                                echo "\"$citta2\", " ;
                                                            }
                                                            ?>];
                                                        let mydata2 = [<?php
                                                            foreach($array2['nAttivita'] as $numeroAttivita) {
                                                                echo "$numeroAttivita, " ;
                                                            }
                                                            ?>];


                                                        let chart2 = new Chart(myCanvas2, {
                                                            type: 'bar',
                                                            data: {
                                                                labels:myLabels2,
                                                                datasets: [{
                                                                    label: 'Numero di attività',
                                                                    data: mydata2,
                                                                    backgroundColor: [
                                                                        'rgba(255, 99, 132, 0.2)',
                                                                        'rgba(54, 162, 235, 0.2)',
                                                                        'rgba(255, 206, 86, 0.2)',
                                                                        'rgba(75, 192, 192, 0.2)',
                                                                        'rgba(153, 102, 255, 0.2)',
                                                                        'rgba(255, 159, 64, 0.2)'
                                                                    ],
                                                                    borderColor: [
                                                                        'rgba(255, 99, 132, 1)',
                                                                        'rgba(54, 162, 235, 1)',
                                                                        'rgba(255, 206, 86, 1)',
                                                                        'rgba(75, 192, 192, 1)',
                                                                        'rgba(153, 102, 255, 1)',
                                                                        'rgba(255, 159, 64, 1)'
                                                                    ],
                                                                    borderWidth: 2
                                                                }]
                                                            },
                                                            options: {
                                                                scales: {
                                                                    yAxes: [{
                                                                        ticks: {
                                                                            beginAtZero: true
                                                                        }
                                                                    }]
                                                                }
                                                            }
                                                        });
                                                    </script>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
        <script src="http://maps.google.com/maps/api/js?sensor=true"></script>
    </div>
</div>
</div>
<script type="text/javascript" src="assets/scripts/main.js"></script>
</body>
</html>
