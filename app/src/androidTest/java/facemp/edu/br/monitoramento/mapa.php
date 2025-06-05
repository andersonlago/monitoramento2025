<?php
// Dados de conexão
$servername = "sql212.infinityfree.com";
$username = "if0_39143010";
$password = "WfiZ6ZbhKDzx";
$dbname = "if0_39143010_gps";
$port = 3306;

// Conectar ao banco de dados
$conn = new mysqli($servername, $username, $password, $dbname);

// Verificar a conexão
if ($conn->connect_error) {
    die("Conexão falhou: " . $conn->connect_error);
}

// Obter os valores dos campos do formulário
$usuario_codigo = isset($_GET['usuario_codigo']) ? $_GET['usuario_codigo'] : '';
$data_inicio = isset($_GET['data_inicio']) ? $_GET['data_inicio'] : '';
$data_fim = isset($_GET['data_fim']) ? $_GET['data_fim'] : '';

// Construir a consulta SQL com os filtros
$sql = "SELECT distinct codigo_unico, latitude, longitude, DATE_FORMAT(dt_hora, '%Y-%m-%d %H:%i') AS dt_hora FROM dados WHERE 1=1"; // 1=1 para facilitar a adição de filtros

//if (!empty($usuario_codigo)) {
    $sql .= " AND codigo_unico = '$usuario_codigo'";
//}

if (!empty($data_inicio) && !empty($data_fim)) {
    $sql .= " AND dt_hora BETWEEN '$data_inicio' AND '$data_fim'";
}

// Executar a consulta SQL
$result = $conn->query($sql);

// Array para armazenar os dados do GPS
$gps_data = array();

if ($result->num_rows > 0) {
    // Loop através dos resultados e armazena no array
    while($row = $result->fetch_assoc()) {
        $gps_data[] = $row;
    }
} else {
    echo "Nenhum resultado encontrado.";
}

// Fechar a conexão com o banco de dados
$conn->close();
?>

<!DOCTYPE html>
<html>
<head>
    <title>Mapa com OpenStreetMap</title>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.3/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
   <style>
        html, body {
            height: 100%;
            margin: 0;
        }

        #map {
            height: 100%;
            width: 100%;
            position: fixed; /* Fixa o mapa para ocupar a tela toda */
            top: 0;
            left: 0;
            z-index: 1; /* Garante que o mapa fique atrás do formulário */
        }

        form {
            position: absolute; /* Posiciona o formulário sobre o mapa */
            top: 5px; /* Ajuste a distância do topo */
            left: 40px; /* Ajuste a distância da esquerda */
            background-color: rgba(255, 255, 255, 0.5); /* Fundo branco semi-transparente */
            padding: 1px;
            border-radius: 1px;
            z-index: 2; /* Garante que o formulário fique na frente do mapa */
        }
    </style>
</head>
<body>
  <form method="GET" class="container">
    <div class="form-group row">
        <label for="usuario_codigo" class="col-sm-1 col-form-label col-form-label-sm">
            <small>Código:</small>
        </label>
        <div class="col-sm-4">
            <input type="text" class="form-control form-control-sm" id="usuario_codigo" name="usuario_codigo" value="<?php echo isset($_GET['usuario_codigo']) ? $_GET['usuario_codigo'] : ''; ?>" >
        </div>
    </div>

    <div class="form-group row">
        <label for="data_inicio" class="col-sm-1 col-form-label col-form-label-sm" >
            <small>Início:</small>
        </label>
        <div class="col-sm-4">
            <input type="datetime-local" class="form-control form-control-sm" id="data_inicio" name="data_inicio" value="<?php echo isset($_GET['data_inicio']) ? $_GET['data_inicio'] : ''; ?>">
        </div>
    </div>

    <div class="form-group row">
        <label for="data_fim" class="col-sm-1 col-form-label col-form-label-sm">
            <small>Fim:</small>
        </label>
        <div class="col-sm-4">
            <input type="datetime-local" class="form-control form-control-sm" id="data_fim" name="data_fim" value="<?php echo isset($_GET['data_fim']) ? $_GET['data_fim'] : ''; ?>">
        </div>
    </div>

    <button type="submit" class="btn btn-primary btn-sm">Filtrar</button>
</form>
   <div id="map"></div>

    <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.3/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script>
        var map = L.map('map').setView([-12.5489, -38.7132], 10); // Recôncavo Baiano

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

        <?php foreach ($gps_data as $data): ?>
            L.marker([<?php echo $data['latitude']; ?>, <?php echo $data['longitude']; ?>])
                .addTo(map)
                .bindPopup("<b>Usuário:</b> <?php echo $data['codigo_unico']; ?><br><b>Data/Hora:</b> <?php echo $data['dt_hora']; ?>");
        <?php endforeach; ?>
    </script>
</body>
</html>