package hu.jlaci.jwt;

public class TestConfiguration {

    public static class SimulationParameters {
        public static final long TIME_STEP = 100;
        public static final long SIMULATION_LENGTH = 5 * 60 * 1000;
    }

    public static class SystemCharacteristics {
        /**
         * Number of clients
         */
        public static final int N_CLIENTS = 100;

        /**
         * Protected resource access / client / seconds
         */
        public static final double RESOURCE_ACCESS_FREQUENCY = 2.5;

        /**
         * Average time between token revocation events
         */
        public static final long T_RVK = 60 * 1000;

        /**
         * Short lived TTL
         */
        public static final long T_LIFE = 30 * 1000;

        /**
         * Number of client groups
         */
        public static final int K = 20;
    }

}
