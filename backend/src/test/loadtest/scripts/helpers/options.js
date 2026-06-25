export const optionsDefault = {
    stages: [
        { duration: '10s', target: 50  },  // calentamiento
        { duration: '15s',  target: 100  },  // carga normal
        { duration: '20s', target: 100  },  // pico de tráfico
        { duration: '15s',  target: 50  },  // sotiene pico
        { duration: '5s', target: 0 },  // bajada
    ],
    thresholds: {
        'http_req_failed': ['rate<0.01'],
        'checks': ['rate>0.99'],
    },
};

