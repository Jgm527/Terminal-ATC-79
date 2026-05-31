package piano.atc79.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Gestiona el guardado y la carga de partidas en formato JSON.
 * <p>
 * Los archivos se almacenan en el directorio {@code saves/} con el formato
 * {@code <ICAO>_<yyyyMMdd_HHmmss>.json}.</p>
 */
public final class SaveManager {

    private static final String SAVES_DIR = "saves";
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private SaveManager() {}

    // ---------------------------------------------------------------
    //  Guardar
    // ---------------------------------------------------------------

    /**
     * Guarda el estado actual de una partida en un archivo JSON.
     * Si se proporciona una ruta de archivo anterior, lo elimina tras escribir el nuevo.
     *
     * @param game        el modelo del juego con el estado a persistir
     * @param saveName    nombre descriptivo que el jugador asigna a la partida
     * @param oldFilePath ruta del archivo anterior (null si es un guardado nuevo)
     * @return el nombre del archivo generado (ej. "LEAL_20260520_121700.json")
     * @throws IOException si no se puede escribir el archivo
     */
    public static String saveGame(Game game, String saveName, String oldFilePath) throws IOException {
        String airportCode = game.getAirport().getId();
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String fileName = airportCode + "_" + timestamp + ".json";

        // Extraer datos
        SaveGameData.ScoreData scoreData = new SaveGameData.ScoreData(
                game.getScore().getTotalPoints(),
                game.getScore().getSuccessfulLandings(),
                game.getScore().getSuccessfulTakesOff()
        );

        List<Flight> flights = game.getFlights();
        SaveGameData.FlightData[] flightDataArray = new SaveGameData.FlightData[flights.size()];
        for (int i = 0; i < flights.size(); i++) {
            Flight f = flights.get(i);
            flightDataArray[i] = new SaveGameData.FlightData(
                    f.getCallsign(),
                    f.getModel().getId(),
                    f.getCurrentPosition().getX(),
                    f.getCurrentPosition().getY(),
                    f.getCurrentPosition().getZ(),
                    f.getHeading(),
                    f.getTargetHeading(),
                    f.getSpeed(),
                    f.getTargetSpeed(),
                    f.getTargetAltitude(),
                    f.getFuel(),
                    f.getStatus().name(),
                    f.getAssignedRunway() != null ? f.getAssignedRunway().getId() : null,
                    f.getApproachType(),
                    f.getHoldingPoint() != null ? f.getHoldingPoint().getId() : null,
                    f.isEnteringHolding()
            );
        }

        SaveGameData data = new SaveGameData(
                saveName, airportCode, System.currentTimeMillis(),
                scoreData, flightDataArray, game.isGameOver()
        );

        // Escribir archivo
        File dir = getSavesDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(data, writer);
        }

        // Borrar el archivo anterior si existia (misma partida, nuevo timestamp)
        if (oldFilePath != null) {
            File oldFile = new File(oldFilePath);
            if (oldFile.exists() && !oldFile.getAbsolutePath().equals(file.getAbsolutePath())) {
                oldFile.delete();
            }
        }

        return fileName;
    }

    // ---------------------------------------------------------------
    //  Cargar
    // ---------------------------------------------------------------

    /**
     * Carga una partida desde un archivo JSON y reconstruye el modelo {@link Game}.
     *
     * @param filePath ruta al archivo .json de la partida guardada
     * @return el {@link Game} reconstruido con el estado guardado
     * @throws IOException si no se puede leer el archivo o los datos son invalidos
     */
    public static Game loadGame(String filePath) throws IOException {
        SaveGameData data;
        try (FileReader reader = new FileReader(filePath)) {
            data = GSON.fromJson(reader, SaveGameData.class);
        }

        if (data == null) {
            throw new IOException("El archivo de guardado esta vacio o corrupto.");
        }

        // 1. Reconstruir aeropuerto
        Airport airport = createAirportFromCode(data.getAirportCode());
        if (airport == null) {
            throw new IOException("Aeropuerto desconocido: " + data.getAirportCode());
        }

        // 2. Crear Game
        Game game = new Game(airport);

        // 3. Reconstruir vuelos
        if (data.getFlights() != null) {
            for (SaveGameData.FlightData fd : data.getFlights()) {
                Flight flight = Flight.reconstructFrom(fd, airport);
                if (flight != null) {
                    game.addFlight(flight);
                }
            }
        }

        // 4. Restaurar puntuacion
        if (data.getScore() != null) {
            game.getScore().setTotalPoints(data.getScore().getTotalPoints());
            game.getScore().setSuccessfulLandings(data.getScore().getSuccessfulLandings());
            game.getScore().setSuccessfulTakesOff(data.getScore().getSuccessfulTakesOff());
        }

        // 5. Restaurar fin de juego
        if (data.isGameOver()) {
            game.setGameOver(true);
        }

        // 6. Registrar metadatos de la partida para re-save
        game.setCurrentSaveName(data.getSaveName());
        game.setCurrentSaveFilePath(new File(filePath).getAbsolutePath());

        return game;
    }

    // ---------------------------------------------------------------
    //  Listar partidas guardadas
    // ---------------------------------------------------------------

    /**
     * Metadatos basicos de una partida guardada (sin cargar los vuelos).
     */
    public static class SaveMeta {
        private final String fileName;
        private final String saveName;
        private final String airportCode;
        private final long timestamp;

        public SaveMeta(String fileName, String saveName, String airportCode, long timestamp) {
            this.fileName = fileName;
            this.saveName = saveName;
            this.airportCode = airportCode;
            this.timestamp = timestamp;
        }

        public String getFileName() { return fileName; }
        public String getSaveName() { return saveName; }
        public String getAirportCode() { return airportCode; }
        public long getTimestamp() { return timestamp; }
    }

    /**
     * Lista las partidas guardadas disponibles en el directorio {@code saves/}.
     * Lee unicamente los metadatos de cada archivo (saveName, airportCode, timestamp),
     * sin cargar los vuelos.
     *
     * @return lista de {@link SaveMeta} ordenada por timestamp descendente
     */
    public static List<SaveMeta> listSaves() {
        List<SaveMeta> metaList = new ArrayList<>();
        File dir = getSavesDir();
        if (!dir.exists()) {
            return metaList;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) {
            return metaList;
        }

        for (File f : files) {
            try (FileReader reader = new FileReader(f)) {
                SaveGameData data = GSON.fromJson(reader, SaveGameData.class);
                if (data != null) {
                    metaList.add(new SaveMeta(
                            f.getName(),
                            data.getSaveName(),
                            data.getAirportCode(),
                            data.getTimestamp()
                    ));
                }
            } catch (Exception ignored) {
                // Ignorar archivos corruptos
            }
        }

        metaList.sort(Comparator.comparingLong(SaveMeta::getTimestamp).reversed());
        return metaList;
    }

    /**
     * Elimina un archivo de partida guardada, si existe.
     *
     * @param filePath ruta al archivo .json a eliminar, o null (no hace nada)
     */
    public static void deleteSave(String filePath) {
        if (filePath == null) return;
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    // ---------------------------------------------------------------
    //  Utilidades
    // ---------------------------------------------------------------

    public static File getSavesDir() {
        return new File(SAVES_DIR);
    }

    /**
     * Fabrica un aeropuerto a partir de su codigo ICAO.
     * Duplica la logica de {@code Main.createAirportFromCode} para evitar
     * dependencias circulares.
     */
    private static Airport createAirportFromCode(String code) {
        return switch (code) {
            case "LEAL" -> AirportFactory.createLEAL();
            case "LEBL" -> AirportFactory.createLEBL();
            case "KLAX" -> AirportFactory.createKLAX();
            case "EGLL" -> AirportFactory.createEGLL();
            case "GCXO" -> AirportFactory.createGCXO();
            case "BIKF" -> AirportFactory.createBIKF();
            case "KJFK" -> AirportFactory.createKJFK();
            default -> null;
        };
    }
}
