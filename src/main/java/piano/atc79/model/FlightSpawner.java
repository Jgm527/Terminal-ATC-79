package piano.atc79.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generador dinamico de trafico aereo. En cada tick evalua si debe spawnear
 * un nuevo vuelo basandose en el perfil del aeropuerto, y ensambla el {@link Flight}
 * con callsign unico, modelo ponderado, ruta de entrada y parametros cinematicos.
 * <p>
 * La rafaga inicial se reparte en varios ticks para evitar que todos los aviones
 * aparezcan simultaneamente, y las altitudes se asignan evitando conflictos con
 * otros vuelos activos para minimizar alertas TCAS al nacer.
 */
public class FlightSpawner {

    private static final String[] PREFIXES = {
        "IBE", "AFR", "KLM", "BAW", "DLH", "AEA", "VLG", "SAS", "AZA", "TWA",
        "PAN", "UAL", "DAL", "AAL", "JAL", "SWR", "TAP", "LOT", "FIN", "THA",
        "ELA", "EIN", "BCY", "MON", "NAX"
    };

    private static final int HEADING_VARIATION = 10;
    private static final double ALTITUDE_PER_NM = 300.0;
    private static final int ALTITUDE_ROUNDING = 1000;

    /** Jitter en millas nauticas para diversificar altitudes en el mismo punto de spawn. */
    private static final double ALTITUDE_JITTER_NM = 0.5;

    private final Game game;
    private final SpawnProfile profile;
    private final Random random;
    private int ticksSinceLastSpawn;
    private int nextSpawnThreshold;
    private final Set<String> activeCallsigns;
    private int burstRemaining;
    private boolean burstQueueInitialized;

    /**
     * Construye un nuevo FlightSpawner.
     *
     * @param game    el modelo de juego donde se inyectaran los vuelos
     * @param profile el perfil de spawn con las reglas del aeropuerto
     */
    public FlightSpawner(Game game, SpawnProfile profile) {
        this.game = game;
        this.profile = profile;
        this.random = new Random();
        this.ticksSinceLastSpawn = 0;
        this.nextSpawnThreshold = calculateNextThreshold();
        this.activeCallsigns = new HashSet<>();
        this.burstRemaining = 0;
        this.burstQueueInitialized = false;
    }

    /**
     * Suprime la rafaga inicial de vuelos.
     * <p>
     * Util cuando se carga una partida guardada que ya tiene vuelos activos,
     * para evitar que aparezcan mas aviones de los esperados.</p>
     */
    public void suppressBurst() {
        this.burstRemaining = 0;
        this.burstQueueInitialized = true;
    }

    /**
     * Metodo principal invocado en cada tick del temporizador.
     * Gestiona la rafaga inicial escalonada y el spawn continuo de vuelos.
     */
    public void tick() {
        syncActiveCallsigns();

        if (!burstQueueInitialized) {
            initBurstQueue();
            burstQueueInitialized = true;
        }

        if (burstRemaining > 0) {
            trySpawnFlight();
            burstRemaining--;
            return;
        }

        ticksSinceLastSpawn++;
        if (ticksSinceLastSpawn >= nextSpawnThreshold) {
            ticksSinceLastSpawn = 0;
            nextSpawnThreshold = calculateNextThreshold();

            if (game.getFlights().size() >= profile.getMaxConcurrentFlights()) {
                return;
            }

            trySpawnFlight();
        }
    }

    /**
     * Inicializa la cola de la rafaga inicial. En lugar de spawnear todos
     * los vuelos en un solo tick, los reparte en ticks sucesivos para que
     * el jugador tenga tiempo de reaccionar.
     */
    private void initBurstQueue() {
        int count = profile.getInitialBurstMin()
                + random.nextInt(profile.getInitialBurstMax() - profile.getInitialBurstMin() + 1);
        burstRemaining = Math.min(count, profile.getMaxConcurrentFlights());
    }

    /**
     * Intenta generar y anadir un vuelo al juego. Si el vuelo se crea
     * correctamente, se registra en el modelo y en el conjunto de callsigns activos.
     */
    private void trySpawnFlight() {
        Flight flight = generateFlight();
        if (flight != null) {
            game.addFlight(flight);
            activeCallsigns.add(flight.getCallsign());
        }
    }

    /**
     * Ensambla un nuevo vuelo con parametros aleatorios pero realistas.
     *
     * @return el {@link Flight} generado, o null si no se pudo crear
     */
    private Flight generateFlight() {
        String callsign = generateUniqueCallsign();
        if (callsign == null) {
            return null;
        }

        AircraftModel model = pickWeightedModel();
        if (model == null) {
            return null;
        }

        EntryRoute route = game.getAirport().pickRandomEntryRoute(random);
        Position spawnPoint = route.pickRandomSpawnPoint(random);

        int heading = calculateHeading(route);
        int altitude = calculateAltitude(spawnPoint);
        int speed = model.getCruiseSpeed();

        return new Flight(callsign, model,
                new Position(spawnPoint.getX(), spawnPoint.getY(), altitude),
                heading, speed);
    }

    /**
     * Genera un callsign unico que no este actualmente en uso.
     *
     * @return el callsign generado, o null tras agotar los intentos
     */
    private String generateUniqueCallsign() {
        int attempts = 0;
        while (attempts < 100) {
            String prefix = PREFIXES[random.nextInt(PREFIXES.length)];
            int number = 100 + random.nextInt(9900);
            String callsign = prefix + number;
            if (!activeCallsigns.contains(callsign)) {
                return callsign;
            }
            attempts++;
        }
        return null;
    }

    /**
     * Selecciona un modelo de aeronave ponderado por el perfil del aeropuerto.
     *
     * @return el {@link AircraftModel} seleccionado, o null si no se encontro
     */
    private AircraftModel pickWeightedModel() {
        int roll = random.nextInt(100);
        int cumulative = 0;
        for (Map.Entry<String, Integer> entry : profile.getModelWeights().entrySet()) {
            cumulative += entry.getValue();
            if (roll < cumulative) {
                return AircraftModelRegistry.get(entry.getKey());
            }
        }
        return AircraftModelRegistry.get("B737");
    }

    /**
     * Calcula el heading de entrada con una pequena variacion aleatoria
     * sobre el heading definido por la ruta.
     *
     * @param route la ruta de entrada seleccionada
     * @return el heading final en grados (0-359)
     */
    private int calculateHeading(EntryRoute route) {
        int variation = random.nextInt(HEADING_VARIATION * 2 + 1) - HEADING_VARIATION;
        int heading = route.getHeading() + variation;
        return ((heading % 360) + 360) % 360;
    }

    /**
     * Calcula la altitud de entrada basandose en la distancia al aeropuerto
     * (regla de los 3 grados) y ajustandola al rango permitido.
     * Las altitudes siempre se redondean a multiplos de 1000 pies.
     * <p>
     * Anade un pequeno jitter a la distancia para que aviones en el mismo
     * punto de spawn obtengan altitudes diversas, y verifica que ningun
     * vuelo activo comparta la altitud elegida para minimizar conflictos
     * de separacion vertical al nacer.
     *
     * @param spawnPoint el punto de spawn seleccionado
     * @return la altitud en pies, redondeada al millar
     */
    private int calculateAltitude(Position spawnPoint) {
        double distance = Math.sqrt(spawnPoint.getX() * spawnPoint.getX()
                                  + spawnPoint.getY() * spawnPoint.getY());

        double jitter = (random.nextDouble() - 0.5) * 2.0 * ALTITUDE_JITTER_NM;
        distance += jitter;

        int altitude = (int) (distance * ALTITUDE_PER_NM);
        altitude = Math.max(profile.getMinAltitude(),
                   Math.min(profile.getMaxAltitude(), altitude));
        altitude = (altitude / ALTITUDE_ROUNDING) * ALTITUDE_ROUNDING;

        if (altitude < profile.getMinAltitude()) {
            altitude = ((profile.getMinAltitude() + ALTITUDE_ROUNDING - 1) / ALTITUDE_ROUNDING) * ALTITUDE_ROUNDING;
        }

        return findAvailableAltitude(altitude);
    }

    /**
     * Busca una altitud disponible que no entre en conflicto con otros vuelos activos.
     * Si la altitud preferida ya esta en uso por algun vuelo en el espacio aereo,
     * sube o baja en escalones de 1000 pies hasta encontrar una libre.
     * <p>
     * Si todas las altitudes del rango estan ocupadas (caso extremo con muchos
     * vuelos y pocos niveles), devuelve la preferida aunque haya duplicados.
     * En ese caso la separacion horizontal se encargara del conflicto.
     *
     * @param preferredAltitude la altitud calculada preferida
     * @return la altitud disponible mas cercana a la preferida
     */
    private int findAvailableAltitude(int preferredAltitude) {
        Set<Integer> usedAltitudes = new HashSet<>();
        for (Flight f : game.getFlights()) {
            usedAltitudes.add(f.getCurrentPosition().getZ());
        }

        if (!usedAltitudes.contains(preferredAltitude)) {
            return preferredAltitude;
        }

        int range = (profile.getMaxAltitude() - profile.getMinAltitude()) / ALTITUDE_ROUNDING;
        for (int i = 1; i <= range; i++) {
            int up = preferredAltitude + (i * ALTITUDE_ROUNDING);
            if (up <= profile.getMaxAltitude() && !usedAltitudes.contains(up)) {
                return up;
            }
            int down = preferredAltitude - (i * ALTITUDE_ROUNDING);
            if (down >= profile.getMinAltitude() && !usedAltitudes.contains(down)) {
                return down;
            }
        }

        return preferredAltitude;
    }

    /**
     * Calcula el siguiente umbral de spawn aplicando la variacion
     * porcentual configurada en el perfil.
     *
     * @return el numero de ticks hasta el siguiente spawn
     */
    private int calculateNextThreshold() {
        int variation = (int) (profile.getBaseTicksBetweenSpawns()
                               * profile.getSpawnVariationPercent());
        return profile.getBaseTicksBetweenSpawns()
                + random.nextInt(variation * 2 + 1) - variation;
    }

    /**
     * Sincroniza el conjunto de callsigns activos con los vuelos
     * actualmente presentes en el juego, eliminando los que ya han
     * aterrizado o salido del espacio aereo.
     */
    private void syncActiveCallsigns() {
        Set<String> current = game.getFlights().stream()
                .map(Flight::getCallsign)
                .collect(Collectors.toSet());
        activeCallsigns.retainAll(current);
    }
}
